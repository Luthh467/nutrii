package com.example.data.nutrition

import com.example.data.model.NutritionCategory
import com.example.data.model.RiskLevel
import java.util.Locale
import kotlin.math.roundToInt

object NutritionCalculator {

  /**
   * Hitung Indeks Massa Tubuh (IMT): BB (kg) / (TB (m))^2
   */
  fun calculateBmi(weightKg: Double, heightCm: Double): Double {
    if (heightCm <= 0 || weightKg <= 0) return 0.0
    val heightM = heightCm / 100.0
    val rawBmi = weightKg / (heightM * heightM)
    return String.format(Locale.US, "%.1f", rawBmi).toDoubleOrNull() ?: rawBmi
  }

  /**
   * Klasifikasi IMT/U Kemenkes RI (Permenkes No. 2 Tahun 2020) untuk remaja 15-18 tahun.
   * Ambang batas:
   * Sangat Kurus: < -3 SD
   * Kurus: -3 SD s/d < -2 SD
   * Normal: -2 SD s/d +1 SD
   * Gemuk: > +1 SD s/d +2 SD
   * Obesitas: > +2 SD
   */
  fun evaluateBmiAge(
    bmi: Double,
    age: Int,
    gender: String // "L" atau "P"
  ): ClassificationResult {
    val isMale = gender.equals("L", ignoreCase = true) || gender.contains("Laki", ignoreCase = true)
    val clampedAge = age.coerceIn(15, 18)

    // Ambang batas (SD: -3, -2, +1, +2) berdasarkan Permenkes No. 2/2020
    val (sdMinus3, sdMinus2, sdPlus1, sdPlus2) = when {
      isMale -> when (clampedAge) {
        15 -> listOf(14.2, 15.6, 22.7, 26.8)
        16 -> listOf(14.7, 16.2, 23.5, 27.5)
        17 -> listOf(15.3, 16.9, 24.3, 28.2)
        else -> listOf(15.7, 17.3, 24.9, 28.9)
      }
      else -> when (clampedAge) {
        15 -> listOf(14.4, 15.9, 23.5, 27.7)
        16 -> listOf(14.8, 16.3, 24.1, 28.3)
        17 -> listOf(15.0, 16.4, 24.5, 28.6)
        else -> listOf(15.1, 16.4, 24.8, 28.8)
      }
    }

    val (category, risk) = when {
      bmi < sdMinus3 -> NutritionCategory.VERY_UNDERWEIGHT to RiskLevel.HIGH
      bmi < sdMinus2 -> NutritionCategory.UNDERWEIGHT to RiskLevel.MEDIUM
      bmi <= sdPlus1 -> NutritionCategory.NORMAL to RiskLevel.LOW
      bmi <= sdPlus2 -> NutritionCategory.OVERWEIGHT to RiskLevel.MEDIUM
      else -> NutritionCategory.OBESE to RiskLevel.HIGH
    }

    val factors = when (category) {
      NutritionCategory.VERY_UNDERWEIGHT -> listOf(
        "Risiko defisiensi energi & mikronutrien kronis",
        "Rentan mengalami kelelahan, daya tahan tubuh menurun, dan kesulitan fokus belajar",
        "Perlu skrining kemungkinan anemia & asupan protein harian"
      )
      NutritionCategory.UNDERWEIGHT -> listOf(
        "Asupan energi harian belum mencukupi kebutuhan pertumbuhan puncak remaja",
        "Perlu peningkatan porsi makan padat gizi secara bertahap",
        "Waspadai pola makan tidak teratur atau sering melewatkan makan utama"
      )
      NutritionCategory.NORMAL -> listOf(
        "Pertumbuhan fisik dan status gizi selaras dengan grafik pertumbuhan remaja",
        "Daya tahan tubuh dan konsentrasi belajar optimal",
        "Pertahankan variasi gizi seimbang (Isi Piringku) dan kebiasaan aktif bergerak"
      )
      NutritionCategory.OVERWEIGHT -> listOf(
        "Asupan kalori berlebih dari cemilan manis, minuman kemasan, atau gorengan",
        "Kurang aktivitas fisik terstruktur di luar jam sekolah",
        "Waktu layar (screen-time) berlebih yang memicu sedentary lifestyle"
      )
      NutritionCategory.OBESE -> listOf(
        "Peningkatan risiko resistensi insulin dan kelelahan dini saat berolahraga",
        "Beban berlebih pada persendian saat aktivitas fisik tinggi",
        "Pola makan tinggi gula-garam-lemak yang perlu dievaluasi bersama tenaga gizi"
      )
    }

    val advice = when (category) {
      NutritionCategory.VERY_UNDERWEIGHT ->
        "Sangat dianjurkan berkonsultasi dengan petugas UKS atau puskesmas. Perbanyak konsumsi makanan sumber protein hewani (telur, ikan, ayam, susu) serta tambah frekuensi makan selingan padat kalori sehat."
      NutritionCategory.UNDERWEIGHT ->
        "Tingkatkan porsi makan secara bertahap dengan menu gizi seimbang. Jangan pernah melewatkan sarapan pagi, dan pilih camilan sehat seperti kacang-kacangan, susu, atau pisang saat jam istirahat sekolah."
      NutritionCategory.NORMAL ->
        "Selamat! Status gizi kamu dalam rentang sehat. Pertahankan dengan konsumsi sayur dan buah setiap hari, cukupi air putih minimal 8 gelas/hari, dan tetap rutin berolahraga 3-5 kali seminggu."
      NutritionCategory.OVERWEIGHT ->
        "Kurangi konsumsi jajanan tinggi gula dan gorengan di kantin. Tingkatkan aktivitas fisik menyenangkan minimal 30-45 menit sehari (jalan kaki, bersepeda, badminton, atau futsal bersama teman)."
      NutritionCategory.OBESE ->
        "Disarankan berkonsultasi dengan petugas UKS untuk pendampingan pola makan sehat bertahap. Fokus pada pengurangan porsi karbohidrat sederhana/minuman manis, perbanyak sayuran hijau, dan tingkatkan langkah harian."
    }

    return ClassificationResult(
      category = category,
      riskLevel = risk,
      keyFactors = factors,
      advice = advice
    )
  }

  /**
   * Rumus Harris-Benedict (Revisi)
   * Laki-laki: BMR = 88.362 + (13.397 × BB) + (4.799 × TB) − (5.677 × Usia)
   * Perempuan: BMR = 447.593 + (9.247 × BB) + (3.098 × TB) − (4.330 × Usia)
   */
  fun calculateBmr(weightKg: Double, heightCm: Double, age: Int, isMale: Boolean): Double {
    val bmr = if (isMale) {
      88.362 + (13.397 * weightKg) + (4.799 * heightCm) - (5.677 * age)
    } else {
      447.593 + (9.247 * weightKg) + (3.098 * heightCm) - (4.330 * age)
    }
    return (bmr * 10).roundToInt() / 10.0
  }

  /**
   * Hitung TDEE berdasarkan faktor aktivitas
   */
  fun calculateTdee(bmr: Double, multiplier: Double): Double {
    return ((bmr * multiplier) * 10).roundToInt() / 10.0
  }

  data class ClassificationResult(
    val category: NutritionCategory,
    val riskLevel: RiskLevel,
    val keyFactors: List<String>,
    val advice: String
  )
}
