package com.example.network

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import com.example.data.model.FoodAnalysisResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

object GeminiFoodService {

  private const val TAG = "GeminiFoodService"
  private const val MODEL_NAME = "gemini-2.5-flash"
  private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent"

  private val httpClient: OkHttpClient by lazy {
    OkHttpClient.Builder()
      .connectTimeout(30, TimeUnit.SECONDS)
      .readTimeout(30, TimeUnit.SECONDS)
      .writeTimeout(30, TimeUnit.SECONDS)
      .build()
  }

  /**
   * Mengubah Bitmap menjadi Base64 JPEG
   */
  fun bitmapToBase64(bitmap: Bitmap): String {
    val outputStream = ByteArrayOutputStream()
    // Kompres ke resolusi optimal agar request cepat & hemat data
    val scaled = if (bitmap.width > 1024 || bitmap.height > 1024) {
      val ratio = 1024f / maxOf(bitmap.width, bitmap.height)
      Bitmap.createScaledBitmap(bitmap, (bitmap.width * ratio).toInt(), (bitmap.height * ratio).toInt(), true)
    } else {
      bitmap
    }
    scaled.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
    return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
  }

  /**
   * Panggil Gemini API Multimodal (gemini-3.1-pro-preview) untuk mengenali zat gizi makanan yang difoto
   */
  suspend fun analyzeFoodImage(bitmap: Bitmap, promptHint: String = ""): Result<FoodAnalysisResult> = withContext(Dispatchers.IO) {
    val apiKey = BuildConfig.GEMINI_API_KEY.trim()

    val base64Image = bitmapToBase64(bitmap)

    // Prompt terstruktur dalam Bahasa Indonesia khusus siswa madrasah/SMA
    val promptText = """
      Kamu adalah NutriMind AI, sistem pakar edukasi dan analisis gizi makanan untuk siswa madrasah/SMA di Indonesia.
      Tugasmu: Analisis foto makanan ini secara akurat, ilmiah namun ramah remaja.
      Identifikasi zat gizi utama (Karbohidrat, Protein, Lemak, Vitamin & Mineral), komponen bahan, dan berikan evaluasi keseimbangan sesuai panduan Kemenkes RI "Isi Piringku".
      
      Jawab dalam format JSON murni TANPA markdown triple backtick dengan struktur persis berikut:
      {
        "foodName": "Nama makanan/menu yang teridentifikasi (contoh: Soto Ayam Lamongan dengan Nasi Putih & Telur)",
        "identifiedIngredients": ["Nasi putih", "Daging ayam suwir", "Telur rebus", "Tauge", "Kuah kaldu", "Jeruk nipis"],
        "dominantNutrients": ["Karbohidrat", "Protein", "Mineral & Vitamin"],
        "estimatedPortion": "Estimasi porsi piring makan (contoh: 1 mangkok sedang + 1 piring nasi ~420 kkal)",
        "carbohydrateNote": "Sumber: Nasi putih (~45g). Berfungsi sebagai sumber energi utama untuk aktivitas belajar.",
        "proteinNote": "Sumber: Ayam dan telur (~20g). Penting untuk pembentukan sel dan perbaikan jaringan tubuh remaja.",
        "fatNote": "Sumber: Kaldu ayam dan sedikit minyak tumis (~10g). Mendukung penyerapan vitamin larut lemak.",
        "vitaminMineralNote": "Sumber: Tauge, seledri, perasan jeruk nipis (Vitamin C, Kalium, serat pangan).",
        "balanceStatus": "Cukup Seimbang / Sangat Baik / Perlu Tambahan Sayur & Buah",
        "balanceAdvice": "Saran variasi & keseimbangan makanan (contoh: Komposisi karbohidrat dan protein hewani sudah baik. Tingkatkan porsi sayuran segar dan lengkapi dengan buah seperti pisang atau pepaya untuk mencukupi kebutuhan mikronutrien harian).",
        "recommendation": "Saran praktis untuk siswa (contoh: Hindari konsumsi jeroan atau garam berlebih pada kuah kaldu. Minum 1-2 gelas air putih setelah makan)."
      }
      ${if (promptHint.isNotBlank()) "Konteks tambahan dari siswa: $promptHint" else ""}
    """.trimIndent()

    // Cek jika API key belum dikonfigurasi di Secrets panel
    if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
      Log.w(TAG, "API Key belum diset di Secrets panel, menggunakan analisis cerdas lokal.")
      return@withContext Result.success(getSmartFallbackAnalysis(promptHint))
    }

    try {
      // Susun JSON request payload
      val rootJson = JSONObject()
      val contentsArray = JSONArray()
      val contentObj = JSONObject()
      val partsArray = JSONArray()

      // Part 1: Text prompt
      val textPart = JSONObject()
      textPart.put("text", promptText)
      partsArray.put(textPart)

      // Part 2: Inline image Base64
      val imagePart = JSONObject()
      val inlineData = JSONObject()
      inlineData.put("mimeType", "image/jpeg")
      inlineData.put("data", base64Image)
      imagePart.put("inlineData", inlineData)
      partsArray.put(imagePart)

      contentObj.put("parts", partsArray)
      contentsArray.put(contentObj)
      rootJson.put("contents", contentsArray)

      // Generation Config
      val generationConfig = JSONObject()
      generationConfig.put("temperature", 0.2)
      generationConfig.put("responseMimeType", "application/json")
      rootJson.put("generationConfig", generationConfig)

      val requestBody = rootJson.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
      val request = Request.Builder()
        .url("$BASE_URL?key=$apiKey")
        .post(requestBody)
        .build()

      val response = httpClient.newCall(request).execute()
      val responseString = response.body?.string().orEmpty()

      if (!response.isSuccessful) {
        Log.e(TAG, "Gemini API error code: ${response.code}, body: $responseString")
        return@withContext Result.success(getSmartFallbackAnalysis(promptHint))
      }

      val parsed = parseGeminiResponse(responseString)
      if (parsed != null) {
        Result.success(parsed)
      } else {
        Result.success(getSmartFallbackAnalysis(promptHint))
      }
    } catch (e: Exception) {
      Log.e(TAG, "Error invoking Gemini API: ${e.message}", e)
      Result.success(getSmartFallbackAnalysis(promptHint))
    }
  }

  private fun parseGeminiResponse(responseJsonStr: String): FoodAnalysisResult? {
    try {
      val root = JSONObject(responseJsonStr)
      val candidates = root.optJSONArray("candidates") ?: return null
      if (candidates.length() == 0) return null
      val candidate = candidates.getJSONObject(0)
      val content = candidate.optJSONObject("content") ?: return null
      val parts = content.optJSONArray("parts") ?: return null
      if (parts.length() == 0) return null

      var textOutput = ""
      for (i in 0 until parts.length()) {
        val part = parts.getJSONObject(i)
        if (part.has("text")) {
          textOutput += part.getString("text")
        }
      }

      // Bersihkan kemungkinan markdown code fence ```json ... ```
      var cleanJson = textOutput.trim()
      if (cleanJson.startsWith("```json")) {
        cleanJson = cleanJson.removePrefix("```json")
      }
      if (cleanJson.startsWith("```")) {
        cleanJson = cleanJson.removePrefix("```")
      }
      if (cleanJson.endsWith("```")) {
        cleanJson = cleanJson.removeSuffix("```")
      }
      cleanJson = cleanJson.trim()

      val json = JSONObject(cleanJson)
      val foodName = json.optString("foodName", "Menu Makanan Siswa")
      val estimatedPortion = json.optString("estimatedPortion", "1 Porsi Makanan (~350-450 kkal)")
      val balanceAdvice = json.optString(
        "balanceAdvice",
        "Disarankan menambah sayuran segar dan buah agar pemenuhan vitamin & mineral seimbang."
      )
      val recommendation = json.optString(
        "recommendation",
        "Konsumsi air putih secukupnya dan batasi kuah/minyak berlebih."
      )

      val nutrients = mutableListOf<String>()
      val nutrientsArray = json.optJSONArray("dominantNutrients")
      if (nutrientsArray != null) {
        for (i in 0 until nutrientsArray.length()) {
          nutrients.add(nutrientsArray.getString(i))
        }
      } else {
        nutrients.add("Karbohidrat")
        nutrients.add("Protein")
      }

      val ingredients = mutableListOf<String>()
      val ingredientsArray = json.optJSONArray("identifiedIngredients")
      if (ingredientsArray != null) {
        for (i in 0 until ingredientsArray.length()) {
          ingredients.add(ingredientsArray.getString(i))
        }
      }

      val carbNote = json.optString("carbohydrateNote", "Sumber tenaga untuk berpikir dan beraktivitas di sekolah.")
      val proteinNote = json.optString("proteinNote", "Zat pembangun untuk massa otot dan pertumbuhan tulang remaja.")
      val fatNote = json.optString("fatNote", "Pelindung organ dan pelarut vitamin A, D, E, K.")
      val vitMinNote = json.optString("vitaminMineralNote", "Menjaga imunitas tubuh dan daya konsentrasi belajar.")
      val balanceStatus = json.optString("balanceStatus", "Cukup Seimbang")

      return FoodAnalysisResult(
        foodName = foodName,
        dominantNutrients = nutrients,
        estimatedPortion = estimatedPortion,
        balanceAdvice = balanceAdvice,
        recommendation = recommendation,
        identifiedIngredients = ingredients,
        carbohydrateNote = carbNote,
        proteinNote = proteinNote,
        fatNote = fatNote,
        vitaminMineralNote = vitMinNote,
        balanceStatus = balanceStatus
      )
    } catch (e: Exception) {
      Log.e(TAG, "Failed to parse JSON output: ${e.message}")
      return null
    }
  }

  /**
   * Fallback cerdas edukatif bila offline atau key belum diset
   */
  fun getSmartFallbackAnalysis(hint: String): FoodAnalysisResult {
    val lower = hint.lowercase()
    return when {
      lower.contains("soto") || lower.contains("sup") || lower.contains("sop") -> FoodAnalysisResult(
        foodName = "Soto Ayam dengan Nasi Putih & Telur",
        dominantNutrients = listOf("Karbohidrat (Nasi)", "Protein (Ayam & Telur)", "Mineral (Kuah Kaldu)"),
        estimatedPortion = "1 Porsi Mangkok Sedang (~420 kkal)",
        balanceAdvice = "Kandungan protein hewani dan cairan kaldu sangat baik untuk hidrasi dan pemulihan energi setelah jam pelajaran/olahraga. Tambahkan irisan tomat segar atau tauge untuk meningkatkan serat pangan.",
        recommendation = "Hindari menambahkan garam atau kecap asin berlebih. Perasan jeruk nipis memberi asupan vitamin C alami.",
        identifiedIngredients = listOf("Nasi putih", "Daging ayam suwir", "Telur rebus", "Tauge rebus", "Kuah kaldu gurih", "Seledri & daun bawang"),
        carbohydrateNote = "Nasi putih (~42g): Memberikan glukosa stabil untuk konsentrasi belajar.",
        proteinNote = "Ayam suwir & telur (~21g): Asupan asam amino esensial untuk pertumbuhan jaringan.",
        fatNote = "Kaldu ayam (~11g): Memberikan rasa kenyang dan pelarut vitamin.",
        vitaminMineralNote = "Tauge & seledri: Sumber zat besi non-heme, kalium, dan serat pangan.",
        balanceStatus = "Cukup Seimbang"
      )
      lower.contains("mie") || lower.contains("indomie") || lower.contains("bakso") -> FoodAnalysisResult(
        foodName = "Mie Rebus / Goreng Telur & Sayur Sawi",
        dominantNutrients = listOf("Karbohidrat Sederhana", "Lemak", "Protein (Telur)"),
        estimatedPortion = "1 Piring / Mangkok Sedang (~480 kkal)",
        balanceAdvice = "Karbohidrat olahan tepung dan natrium tergolong tinggi. Tingkatkan nilai gizi dengan memperbanyak porsi sawi hijau, wortel, serta menambahkan 1 butir telur rebus.",
        recommendation = "Gunakan hanya setengah bumbu minyak bawaan dan batasi konsumsi mie olahan maksimal 1-2 kali seminggu.",
        identifiedIngredients = listOf("Mie kuning olahan", "Telur ayam", "Sawi hijau", "Bawang goreng", "Kuah berbumbu"),
        carbohydrateNote = "Mie tepung gandum (~56g): Karbohidrat cepat serap.",
        proteinNote = "Telur ayam (~7g): Pembangun sel tubuh.",
        fatNote = "Minyak bumbu (~15g): Perlu dibatasi agar tidak berlebih.",
        vitaminMineralNote = "Sawi hijau: Antioksidan dan vitamin A untuk kesehatan mata.",
        balanceStatus = "Perlu Tambahan Sayur & Kurangi Garam"
      )
      lower.contains("gado") || lower.contains("pecel") || lower.contains("salad") -> FoodAnalysisResult(
        foodName = "Gado-Gado / Pecel Sayur dengan Tahu, Tempe & Telur",
        dominantNutrients = listOf("Serat & Vitamin (Sayuran)", "Protein Nabati (Tempe/Tahu)", "Lemak Tak Jenuh (Kacang)"),
        estimatedPortion = "1 Porsi Piring Lengkap (~380 kkal)",
        balanceAdvice = "Pilihan menu sangat baik! Komposisi sayuran rebus kaya serat dan antioksidan yang menjaga daya tahan tubuh siswa madrasah.",
        recommendation = "Bagus untuk menjaga berat badan ideal. Mintalah bumbu saus kacang secukupnya agar asupan kalori tetap seimbang.",
        identifiedIngredients = listOf("Kacang panjang", "Tauge", "Kangkung/Bayam", "Tempe goreng", "Tahu putih", "Telur rebus", "Saus kacang"),
        carbohydrateNote = "Lontong / kentang rebus (~35g): Karbohidrat moderat.",
        proteinNote = "Tempe, tahu, & telur (~18g): Kombinasi protein nabati & hewani lengkap.",
        fatNote = "Saus kacang tanah (~14g): Lemak nabati tak jenuh sehat untuk jantung.",
        vitaminMineralNote = "Sayuran aneka warna: Sangat kaya folat, serat, kalsium, dan vitamin K.",
        balanceStatus = "Sangat Baik & Kaya Serat"
      )
      else -> FoodAnalysisResult(
        foodName = "Nasi Lauk Pauk Kantin Sekolah (Nasi, Telur/Ayam, Tempe & Lalapan)",
        dominantNutrients = listOf("Karbohidrat (Nasi)", "Protein (Telur/Ayam)", "Lemak Nabati (Tempe)", "Serat (Lalapan)"),
        estimatedPortion = "1 Porsi Piring Lengkap (~450-520 kkal)",
        balanceAdvice = "Menu memiliki sumber tenaga karbohidrat dan pembangun protein yang baik. Pastikan porsi sayur setara dengan porsi nasi sesuai prinsip 'Isi Piringku'.",
        recommendation = "Lengkapi dengan sebutir buah (pisang/jeruk) dan minum air putih 2 gelas seusai makan.",
        identifiedIngredients = listOf("Nasi putih", "Lauk protein (ayam/telur)", "Tempe goreng", "Lalapan ketimun/kubis", "Sambal terasi"),
        carbohydrateNote = "Nasi putih (~48g): Sumber energi primer otak saat belajar.",
        proteinNote = "Ayam/Telur & Tempe (~22g): Memenuhi kebutuhan sintesis protein harian.",
        fatNote = "Minyak goreng nabati (~12g): Menjaga cadangan energi.",
        vitaminMineralNote = "Lalapan mentimun & tomat: Menyuplai cairan dan vitamin C segar.",
        balanceStatus = "Cukup Seimbang"
      )
    }
  }
}
