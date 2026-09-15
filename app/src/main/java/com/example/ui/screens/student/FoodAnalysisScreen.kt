package com.example.ui.screens.student

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.model.FoodAnalysisResult
import com.example.network.GeminiFoodService
import com.example.ui.components.CameraCaptureComponent
import com.example.ui.components.DisclaimerCard
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.OrangeSecondary
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FoodAnalysisScreen() {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()

  var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }
  var photoSourceLabel by remember { mutableStateOf("") }
  var foodHint by remember { mutableStateOf("") }
  var isLoading by remember { mutableStateOf(false) }
  var analysisResult by remember { mutableStateOf<FoodAnalysisResult?>(null) }
  var errorMessage by remember { mutableStateOf("") }
  var isCameraXActive by remember { mutableStateOf(false) }

  // 1. Camera Capture Launcher (Fallback Sistem)
  val cameraLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.TakePicturePreview()
  ) { bitmap ->
    if (bitmap != null) {
      selectedBitmap = bitmap
      photoSourceLabel = "Kamera Sistem"
      isCameraXActive = false
      analysisResult = null
      errorMessage = ""
    }
  }

  // 2. Camera Runtime Permission Launcher (Aktifkan CameraX saat diizinkan)
  val cameraPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    if (isGranted) {
      isCameraXActive = true
      errorMessage = ""
    } else {
      errorMessage = "Izin akses kamera diperlukan agar Anda dapat memotret makanan secara langsung."
    }
  }

  // 3. Photo Picker Launcher (Google Play Policy Zero-Permission Compliant)
  val pickMediaLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri ->
    if (uri != null) {
      try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
        selectedBitmap = bitmap
        photoSourceLabel = "Galeri Foto"
        analysisResult = null
        errorMessage = ""
      } catch (e: Exception) {
        errorMessage = "Gagal memuat gambar dari galeri."
      }
    }
  }

  // Contoh Preset Menu Sekolah untuk Kemudahan Pengujian Cepat di Emulator
  val samplePresets = listOf(
    "Soto Ayam & Nasi" to "Soto ayam kuah kuning hangat dengan nasi putih dan irisan telur rebus",
    "Nasi Goreng Telur" to "Nasi goreng kantin dengan telur mata sapi dan acar mentimun",
    "Gado-Gado Komplit" to "Sayuran rebus, lontong, tahu, tempe dengan bumbu saus kacang",
    "Mie Instan Telur Sawi" to "Mie rebus kuah dengan telur ayam dan sayur sawi hijau",
    "Bekal Nasi Ayam & Sayur" to "Nasi putih, ayam kecap, dan tumis buncis wortel dari rumah"
  )

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(horizontal = 20.dp),
    contentPadding = PaddingValues(top = 20.dp, bottom = 80.dp)
  ) {
    // Header
    item {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.AutoAwesome,
          contentDescription = null,
          tint = OrangeSecondary,
          modifier = Modifier.size(26.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Analisis Zat Gizi Makanan",
          style = MaterialTheme.typography.headlineSmall.copy(
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B)
          )
        )
      }
      Text(
        text = "Potret makanan dengan kamera dan kenali zat gizi (Karbohidrat, Protein, Lemak, Vitamin/Mineral) via Gemini AI",
        style = MaterialTheme.typography.bodySmall.copy(
          color = Color(0xFF334155),
          fontWeight = FontWeight.Medium
        )
      )

      Spacer(modifier = Modifier.height(14.dp))
      DisclaimerCard(compact = true)
      Spacer(modifier = Modifier.height(16.dp))
    }

    // Card Input Kamera & Foto
    item {
      Card(
        modifier = Modifier.fillMaxWidth().testTag("food_photo_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "1. Potret atau Pilih Foto Makanan",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )
          Text(
            text = "Ambil foto menu makan siang, sarapan, bekal, atau jajanan kantin sekolah",
            style = MaterialTheme.typography.bodySmall.copy(
              color = Color(0xFF334155),
              fontWeight = FontWeight.Medium
            )
          )

          Spacer(modifier = Modifier.height(14.dp))

          // Jika Kamera CameraX Sedang Aktif -> Tampilkan Live Viewfinder
          if (isCameraXActive) {
            CameraCaptureComponent(
              modifier = Modifier.fillMaxWidth(),
              onImageCaptured = { bitmap ->
                selectedBitmap = bitmap
                isCameraXActive = false
                photoSourceLabel = "Kamera Langsung (CameraX)"
                analysisResult = null
                errorMessage = ""
              },
              onError = { err ->
                errorMessage = err
                isCameraXActive = false
              },
              onClose = {
                isCameraXActive = false
              }
            )
          } else {
            // Preview Foto atau Placeholder saat kamera belum dibuka
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFFF1F8E9))
                .border(1.dp, Color(0xFFC8E6C9), RoundedCornerShape(14.dp)),
              contentAlignment = Alignment.Center
            ) {
              val bmp = selectedBitmap
              if (bmp != null) {
                Image(
                  bitmap = bmp.asImageBitmap(),
                  contentDescription = "Foto Makanan Terpilih",
                  modifier = Modifier.fillMaxSize()
                )

                // Badge Sumber Foto
                Box(
                  modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.65f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      imageVector = Icons.Default.CheckCircle,
                      contentDescription = null,
                      tint = Color(0xFF69F0AE),
                      modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                      text = photoSourceLabel.ifBlank { "Foto Siap" },
                      style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                      )
                    )
                  }
                }
              } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Box(
                    modifier = Modifier
                      .size(56.dp)
                      .clip(CircleShape)
                      .background(Color(0xFFE8F5E9)),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(
                      imageVector = Icons.Default.CameraAlt,
                      contentDescription = null,
                      tint = GreenPrimary,
                      modifier = Modifier.size(30.dp)
                    )
                  }
                  Spacer(modifier = Modifier.height(10.dp))
                  Text(
                    text = "Kamera Langsung Siap Memotret",
                    style = MaterialTheme.typography.bodyMedium.copy(
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFF1B5E20)
                    )
                  )
                  Text(
                    text = "Gunakan CameraX untuk membidik makanan secara langsung",
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = Color(0xFF334155),
                      fontWeight = FontWeight.Medium
                    )
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tombol Utama: Buka Kamera Langsung (CameraX dengan CameraSelector & ImageCapture)
            Button(
              onClick = {
                val hasPermission = ContextCompat.checkSelfPermission(
                  context,
                  Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED

                if (hasPermission) {
                  isCameraXActive = true
                  errorMessage = ""
                } else {
                  cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
              },
              modifier = Modifier.fillMaxWidth().height(48.dp).testTag("camerax_open_button"),
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
            ) {
              Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(20.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                if (selectedBitmap != null) "Foto Ulang (CameraX)" else "Buka Kamera Langsung (CameraX)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tombol Alternatif: Kamera Bawaan & Galeri
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              OutlinedButton(
                onClick = {
                  val hasPermission = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                  ) == PackageManager.PERMISSION_GRANTED

                  if (hasPermission) {
                    cameraLauncher.launch()
                  } else {
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                  }
                },
                modifier = Modifier.weight(1f).height(42.dp).testTag("camera_system_button"),
                shape = RoundedCornerShape(10.dp)
              ) {
                Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Kamera HP", fontSize = 12.sp)
              }

              OutlinedButton(
                onClick = {
                  pickMediaLauncher.launch(
                    androidx.activity.result.PickVisualMediaRequest(
                      ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                  )
                },
                modifier = Modifier.weight(1f).height(42.dp).testTag("gallery_button"),
                shape = RoundedCornerShape(10.dp)
              ) {
                Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Galeri Foto", fontSize = 12.sp)
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Pilihan Cepat Preset Menu Kantin (Sangat membantu pengujian tanpa kamera fisik)
          Text(
            text = "Atau Uji Cepat dengan Menu Kantin / Bekal:",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold, color = OrangeSecondary)
          )
          Spacer(modifier = Modifier.height(6.dp))

          LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(samplePresets) { (name, desc) ->
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(10.dp))
                  .background(Color(0xFFFFF3E0))
                  .border(1.dp, Color(0xFFFFCC80), RoundedCornerShape(10.dp))
                  .clickable {
                    foodHint = desc
                    photoSourceLabel = "Contoh Menu: $name"
                    selectedBitmap = createSampleFoodBitmap(name)
                    analysisResult = null
                    errorMessage = ""
                  }
                  .padding(horizontal = 10.dp, vertical = 6.dp)
              ) {
                Text(
                  text = "🍱 $name",
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = Color(0xFFE65100),
                    fontWeight = FontWeight.Medium
                  )
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Catatan / Konteks Tambahan Siswa
          OutlinedTextField(
            value = foodHint,
            onValueChange = { foodHint = it },
            label = { Text("Catatan / Tambahan Info Menu (Opsional)") },
            placeholder = { Text("Misal: Nasi soto ayam, tanpa emping, tambah jeruk nipis") },
            modifier = Modifier.fillMaxWidth().testTag("food_hint_input"),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
          )

          if (errorMessage.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Tombol Hubungkan ke Gemini API
          Button(
            onClick = {
              val bmp = selectedBitmap ?: createSampleFoodBitmap(foodHint.ifBlank { "Menu Makanan Siswa" })
              selectedBitmap = bmp
              isLoading = true
              errorMessage = ""
              scope.launch {
                val result = GeminiFoodService.analyzeFoodImage(bmp, foodHint)
                isLoading = false
                result.onSuccess {
                  analysisResult = it
                }.onFailure {
                  errorMessage = "Gagal memproses gambar makanan: ${it.message}"
                }
              }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth().height(50.dp).testTag("analyze_food_button"),
            colors = ButtonDefaults.buttonColors(containerColor = OrangeSecondary),
            shape = RoundedCornerShape(12.dp)
          ) {
            if (isLoading) {
              CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
              Spacer(modifier = Modifier.width(10.dp))
              Text("Gemini AI Sedang Mengenali Zat Gizi...", fontWeight = FontWeight.Bold)
            } else {
              Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(20.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text("Analisis Zat Gizi dengan Gemini API", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
          }
        }
      }
    }

    // HASIL ANALISIS ZAT GIZI GEMINI AI
    item {
      AnimatedVisibility(visible = analysisResult != null) {
        val res = analysisResult ?: return@AnimatedVisibility
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 18.dp)
            .testTag("food_analysis_result_card"),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            // Header Hasil
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Hasil Analisis Gizi AI",
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = GreenPrimary
                  )
                )
              }

              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(Color(0xFFE8F5E9))
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text(
                  text = res.balanceStatus,
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.Bold
                  )
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Nama Makanan & Estimasi Porsi
            Text(
              text = res.foodName,
              style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1E293B)
              )
            )

            Text(
              text = "Estimasi Porsi: ${res.estimatedPortion}",
              style = MaterialTheme.typography.bodySmall.copy(
                color = Color(0xFF9A3412),
                fontWeight = FontWeight.Bold
              )
            )

            // Bahan-Bahan Terdeteksi
            if (res.identifiedIngredients.isNotEmpty()) {
              Spacer(modifier = Modifier.height(12.dp))
              Text(
                text = "Komponen / Bahan Terdeteksi:",
                style = MaterialTheme.typography.labelSmall.copy(
                  color = Color(0xFF334155),
                  fontWeight = FontWeight.Bold
                )
              )
              Spacer(modifier = Modifier.height(4.dp))
              FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                res.identifiedIngredients.forEach { ing ->
                  Box(
                    modifier = Modifier
                      .clip(RoundedCornerShape(6.dp))
                      .background(Color(0xFFE8F5E9))
                      .padding(horizontal = 8.dp, vertical = 3.dp)
                  ) {
                    Text(
                      text = ing,
                      style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFF1B5E20),
                        fontWeight = FontWeight.SemiBold
                      )
                    )
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4 KOTAK KANDUNGAN ZAT GIZI UTAMA
            Text(
              text = "Rincian Kandungan Zat Gizi Utama:",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(8.dp))

            // 1. Karbohidrat
            NutrientDetailCard(
              title = "Karbohidrat (Zat Tenaga)",
              desc = res.carbohydrateNote.ifBlank { "Sumber glukosa untuk konsentrasi belajar dan energi beraktivitas." },
              iconEmoji = "🌾",
              bgColor = Color(0xFFFFF8E1),
              titleColor = Color(0xFF92400E)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // 2. Protein
            NutrientDetailCard(
              title = "Protein (Zat Pembangun)",
              desc = res.proteinNote.ifBlank { "Penting untuk pertumbuhan sel, otot, dan imunitas remaja." },
              iconEmoji = "🍗",
              bgColor = Color(0xFFE8F5E9),
              titleColor = Color(0xFF1B5E20)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // 3. Lemak
            NutrientDetailCard(
              title = "Lemak (Cadangan Energi)",
              desc = res.fatNote.ifBlank { "Pelindung organ vital dan pelarut vitamin A, D, E, K." },
              iconEmoji = "🥑",
              bgColor = Color(0xFFFFE0B2),
              titleColor = Color(0xFF7C2D12)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // 4. Vitamin, Mineral & Serat
            NutrientDetailCard(
              title = "Vitamin, Mineral & Serat (Zat Pengatur)",
              desc = res.vitaminMineralNote.ifBlank { "Mendukung daya tahan tubuh dan metabolisme lancar." },
              iconEmoji = "🥗",
              bgColor = Color(0xFFE0F2F1),
              titleColor = Color(0xFF004D40)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Evaluasi Keseimbangan & Saran Piring Makan
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFFFF3E0))
                .padding(14.dp)
            ) {
              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = Color(0xFFBF360C),
                    modifier = Modifier.size(18.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "Saran Variasi & Keseimbangan 'Isi Piringku':",
                    style = MaterialTheme.typography.labelMedium.copy(
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFFBF360C)
                    )
                  )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = res.balanceAdvice,
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF1C1917),
                    fontWeight = FontWeight.Medium,
                    lineHeight = 17.sp
                  )
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Catatan Praktis
            Text(
              text = "💡 Tips Siswa: ${res.recommendation}",
              style = MaterialTheme.typography.bodySmall.copy(
                color = Color(0xFF0F172A),
                lineHeight = 16.sp,
                fontWeight = FontWeight.Medium
              )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Tombol Potret Makanan Lain
            OutlinedButton(
              onClick = {
                selectedBitmap = null
                analysisResult = null
                photoSourceLabel = ""
              },
              modifier = Modifier.fillMaxWidth().height(44.dp),
              shape = RoundedCornerShape(10.dp)
            ) {
              Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Potret / Analisis Makanan Lain", fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Catatan Wajib
            Text(
              text = "*Catatan: Hasil analisis ini adalah perkiraan awal untuk edukasi gizi madrasah/SMA, bukan penghitungan kalori presisi laboratorium atau pengganti diagnosis ahli gizi.",
              style = MaterialTheme.typography.labelSmall.copy(
                color = Color(0xFF475569),
                fontSize = 10.sp,
                lineHeight = 13.sp,
                fontWeight = FontWeight.Medium
              )
            )
          }
        }
      }
    }
  }
}

@Composable
private fun NutrientDetailCard(
  title: String,
  desc: String,
  iconEmoji: String,
  bgColor: Color,
  titleColor: Color
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(10.dp))
      .background(bgColor)
      .padding(10.dp)
  ) {
    Row(verticalAlignment = Alignment.Top) {
      Text(text = iconEmoji, fontSize = 18.sp, modifier = Modifier.padding(top = 1.dp))
      Spacer(modifier = Modifier.width(8.dp))
      Column {
        Text(
          text = title,
          style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            color = titleColor
          )
        )
        Text(
          text = desc,
          style = MaterialTheme.typography.bodySmall.copy(
            color = Color(0xFF0F172A),
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            lineHeight = 15.sp
          )
        )
      }
    }
  }
}

/**
 * Membuat bitmap visual untuk pengujian preset tanpa kamera fisik
 */
private fun createSampleFoodBitmap(foodName: String): Bitmap {
  val width = 450
  val height = 300
  val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
  val canvas = Canvas(bitmap)

  val bgPaint = Paint().apply {
    color = android.graphics.Color.parseColor("#E8F5E9")
    style = Paint.Style.FILL
  }
  canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

  val platePaint = Paint().apply {
    color = android.graphics.Color.parseColor("#FFFFFF")
    style = Paint.Style.FILL
  }
  canvas.drawCircle(width / 2f, height / 2f, 120f, platePaint)

  val borderPaint = Paint().apply {
    color = android.graphics.Color.parseColor("#C8E6C9")
    style = Paint.Style.STROKE
    strokeWidth = 6f
  }
  canvas.drawCircle(width / 2f, height / 2f, 120f, borderPaint)

  val textPaint = Paint().apply {
    color = android.graphics.Color.parseColor("#1B5E20")
    textSize = 22f
    isAntiAlias = true
    textAlign = Paint.Align.CENTER
  }
  canvas.drawText("🍱 $foodName", width / 2f, height / 2f + 8f, textPaint)

  return bitmap
}
