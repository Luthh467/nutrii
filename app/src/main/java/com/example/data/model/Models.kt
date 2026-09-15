package com.example.data.model

enum class UserRole {
  STUDENT,
  UKS_OFFICER
}

enum class NutritionCategory(
  val label: String,
  val subtitle: String,
  val defaultRisk: RiskLevel
) {
  VERY_UNDERWEIGHT("Sangat Kurus", "Gizi Buruk (< -3 SD)", RiskLevel.HIGH),
  UNDERWEIGHT("Kurus", "Gizi Kurang (-3 SD s.d < -2 SD)", RiskLevel.MEDIUM),
  NORMAL("Normal", "Gizi Baik (-2 SD s.d +1 SD)", RiskLevel.LOW),
  OVERWEIGHT("Gemuk", "Berisiko Lebih (+1 SD s.d +2 SD)", RiskLevel.MEDIUM),
  OBESE("Obesitas", "Obesitas (> +2 SD)", RiskLevel.HIGH)
}

enum class RiskLevel(val label: String) {
  LOW("Rendah"),
  MEDIUM("Sedang"),
  HIGH("Tinggi")
}

data class StudentProfile(
  val id: String,
  val name: String,
  val nisn: String,
  val className: String,
  val gender: String, // "L" atau "P"
  val age: Int
)

data class AssessmentResult(
  val id: String,
  val studentId: String,
  val studentName: String,
  val className: String,
  val gender: String,
  val age: Int,
  val weightKg: Double,
  val heightCm: Double,
  val bmi: Double,
  val category: NutritionCategory,
  val riskLevel: RiskLevel,
  val keyFactors: List<String>,
  val educationalAdvice: String,
  val dateFormatted: String,
  val timestamp: Long = System.currentTimeMillis(),
  val uksNote: String = ""
)

data class DailyLog(
  val id: String,
  val studentId: String,
  val studentName: String,
  val dateFormatted: String,
  val hasBreakfast: Boolean,
  val physicalActivityType: String,
  val physicalActivityMinutes: Int,
  val waterGlasses: Int,
  val snackHabit: String,
  val bmr: Double,
  val tdee: Double,
  val activityLevelLabel: String,
  val feedbackNotes: String,
  val timestamp: Long = System.currentTimeMillis()
)

data class FoodAnalysisResult(
  val foodName: String,
  val dominantNutrients: List<String>,
  val estimatedPortion: String,
  val balanceAdvice: String,
  val recommendation: String,
  val identifiedIngredients: List<String> = emptyList(),
  val carbohydrateNote: String = "",
  val proteinNote: String = "",
  val fatNote: String = "",
  val vitaminMineralNote: String = "",
  val balanceStatus: String = "Cukup Seimbang"
)

data class EducationCard(
  val id: String,
  val title: String,
  val category: String,
  val readTime: String,
  val summary: String,
  val fullBody: String,
  val forCategory: NutritionCategory? = null,
  val iconEmoji: String = "🥗"
)

data class QuizQuestion(
  val id: Int,
  val question: String,
  val options: List<String>,
  val correctIndex: Int,
  val explanation: String
)
