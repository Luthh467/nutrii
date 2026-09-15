package com.example.data.firebase

import android.content.Context
import android.util.Log
import com.example.data.model.AssessmentResult
import com.example.data.model.DailyLog
import com.example.data.model.NutritionCategory
import com.example.data.model.RiskLevel
import com.example.data.model.StudentProfile
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions

object NutriMindFirestoreService {

  private const val TAG = "NutriMindFirestore"
  private const val COLLECTION_ASSESSMENTS = "assessments"
  private const val COLLECTION_DAILY_LOGS = "daily_logs"
  private const val COLLECTION_STUDENTS = "students"

  private var firestoreInstance: FirebaseFirestore? = null

  fun getFirestore(context: Context? = null): FirebaseFirestore? {
    if (firestoreInstance != null) return firestoreInstance
    return try {
      if (FirebaseApp.getApps(context ?: return null).isNotEmpty()) {
        val db = FirebaseFirestore.getInstance()
        firestoreInstance = db
        db
      } else {
        Log.w(TAG, "FirebaseApp is not initialized yet.")
        null
      }
    } catch (e: Exception) {
      Log.e(TAG, "Error initializing Firestore: ${e.message}", e)
      null
    }
  }

  // --- ASSESSMENTS (SKRINING IMT) ---

  fun saveAssessment(
    assessment: AssessmentResult,
    context: Context? = null,
    onSuccess: () -> Unit = {},
    onError: (Exception) -> Unit = {}
  ) {
    val db = getFirestore(context)
    if (db == null) {
      Log.d(TAG, "Firestore offline/unavailable, assessment saved in local memory.")
      onSuccess()
      return
    }

    val map = hashMapOf(
      "id" to assessment.id,
      "studentId" to assessment.studentId,
      "studentName" to assessment.studentName,
      "className" to assessment.className,
      "gender" to assessment.gender,
      "age" to assessment.age,
      "weightKg" to assessment.weightKg,
      "heightCm" to assessment.heightCm,
      "bmi" to assessment.bmi,
      "category" to assessment.category.name,
      "riskLevel" to assessment.riskLevel.name,
      "keyFactors" to assessment.keyFactors,
      "educationalAdvice" to assessment.educationalAdvice,
      "dateFormatted" to assessment.dateFormatted,
      "timestamp" to assessment.timestamp,
      "uksNote" to assessment.uksNote
    )

    db.collection(COLLECTION_ASSESSMENTS)
      .document(assessment.id)
      .set(map, SetOptions.merge())
      .addOnSuccessListener {
        Log.d(TAG, "Assessment synced to Firestore: ${assessment.id}")
        onSuccess()
      }
      .addOnFailureListener { e ->
        Log.e(TAG, "Failed to sync assessment to Firestore: ${e.message}", e)
        onError(e)
      }
  }

  fun updateUksNote(
    assessmentId: String,
    note: String,
    context: Context? = null,
    onSuccess: () -> Unit = {},
    onError: (Exception) -> Unit = {}
  ) {
    val db = getFirestore(context)
    if (db == null) {
      onSuccess()
      return
    }

    db.collection(COLLECTION_ASSESSMENTS)
      .document(assessmentId)
      .update("uksNote", note)
      .addOnSuccessListener {
        Log.d(TAG, "UKS note updated in Firestore for assessment: $assessmentId")
        onSuccess()
      }
      .addOnFailureListener { e ->
        Log.e(TAG, "Failed to update UKS note: ${e.message}", e)
        onError(e)
      }
  }

  fun listenToAssessments(
    context: Context? = null,
    onUpdate: (List<AssessmentResult>) -> Unit
  ): ListenerRegistration? {
    val db = getFirestore(context) ?: return null

    return db.collection(COLLECTION_ASSESSMENTS)
      .addSnapshotListener { snapshot, error ->
        if (error != null) {
          Log.e(TAG, "Listen to assessments failed: ${error.message}", error)
          return@addSnapshotListener
        }

        if (snapshot != null && !snapshot.isEmpty) {
          val list = snapshot.documents.mapNotNull { doc ->
            try {
              AssessmentResult(
                id = doc.getString("id") ?: doc.id,
                studentId = doc.getString("studentId") ?: "",
                studentName = doc.getString("studentName") ?: "Siswa",
                className = doc.getString("className") ?: "-",
                gender = doc.getString("gender") ?: "L",
                age = (doc.getLong("age") ?: 16L).toInt(),
                weightKg = doc.getDouble("weightKg") ?: 50.0,
                heightCm = doc.getDouble("heightCm") ?: 160.0,
                bmi = doc.getDouble("bmi") ?: 20.0,
                category = try {
                  NutritionCategory.valueOf(doc.getString("category") ?: "NORMAL")
                } catch (e: Exception) {
                  NutritionCategory.NORMAL
                },
                riskLevel = try {
                  RiskLevel.valueOf(doc.getString("riskLevel") ?: "LOW")
                } catch (e: Exception) {
                  RiskLevel.LOW
                },
                keyFactors = (doc.get("keyFactors") as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList(),
                educationalAdvice = doc.getString("educationalAdvice") ?: "",
                dateFormatted = doc.getString("dateFormatted") ?: "",
                timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis(),
                uksNote = doc.getString("uksNote") ?: ""
              )
            } catch (e: Exception) {
              Log.e(TAG, "Error parsing assessment doc ${doc.id}: ${e.message}")
              null
            }
          }
          onUpdate(list.sortedByDescending { it.timestamp })
        }
      }
  }

  // --- DAILY LOGS (LOG GIZI HARIAN SISWA) ---

  fun saveDailyLog(
    log: DailyLog,
    context: Context? = null,
    onSuccess: () -> Unit = {},
    onError: (Exception) -> Unit = {}
  ) {
    val db = getFirestore(context)
    if (db == null) {
      Log.d(TAG, "Firestore offline/unavailable, daily log saved locally.")
      onSuccess()
      return
    }

    val map = hashMapOf(
      "id" to log.id,
      "studentId" to log.studentId,
      "studentName" to log.studentName,
      "dateFormatted" to log.dateFormatted,
      "hasBreakfast" to log.hasBreakfast,
      "physicalActivityType" to log.physicalActivityType,
      "physicalActivityMinutes" to log.physicalActivityMinutes,
      "waterGlasses" to log.waterGlasses,
      "snackHabit" to log.snackHabit,
      "bmr" to log.bmr,
      "tdee" to log.tdee,
      "activityLevelLabel" to log.activityLevelLabel,
      "feedbackNotes" to log.feedbackNotes,
      "timestamp" to log.timestamp
    )

    db.collection(COLLECTION_DAILY_LOGS)
      .document(log.id)
      .set(map, SetOptions.merge())
      .addOnSuccessListener {
        Log.d(TAG, "Daily log synced to Firestore: ${log.id}")
        onSuccess()
      }
      .addOnFailureListener { e ->
        Log.e(TAG, "Failed to sync daily log: ${e.message}", e)
        onError(e)
      }
  }

  fun listenToDailyLogs(
    context: Context? = null,
    onUpdate: (List<DailyLog>) -> Unit
  ): ListenerRegistration? {
    val db = getFirestore(context) ?: return null

    return db.collection(COLLECTION_DAILY_LOGS)
      .addSnapshotListener { snapshot, error ->
        if (error != null) {
          Log.e(TAG, "Listen to daily logs failed: ${error.message}", error)
          return@addSnapshotListener
        }

        if (snapshot != null && !snapshot.isEmpty) {
          val list = snapshot.documents.mapNotNull { doc ->
            try {
              DailyLog(
                id = doc.getString("id") ?: doc.id,
                studentId = doc.getString("studentId") ?: "",
                studentName = doc.getString("studentName") ?: "Siswa",
                dateFormatted = doc.getString("dateFormatted") ?: "",
                hasBreakfast = doc.getBoolean("hasBreakfast") ?: false,
                physicalActivityType = doc.getString("physicalActivityType") ?: "-",
                physicalActivityMinutes = (doc.getLong("physicalActivityMinutes") ?: 0L).toInt(),
                waterGlasses = (doc.getLong("waterGlasses") ?: 0L).toInt(),
                snackHabit = doc.getString("snackHabit") ?: "-",
                bmr = doc.getDouble("bmr") ?: 0.0,
                tdee = doc.getDouble("tdee") ?: 0.0,
                activityLevelLabel = doc.getString("activityLevelLabel") ?: "Sedentary",
                feedbackNotes = doc.getString("feedbackNotes") ?: "",
                timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()
              )
            } catch (e: Exception) {
              Log.e(TAG, "Error parsing daily log doc ${doc.id}: ${e.message}")
              null
            }
          }
          onUpdate(list.sortedByDescending { it.timestamp })
        }
      }
  }

  // --- STUDENT PROFILES ---

  fun saveStudentProfile(
    student: StudentProfile,
    context: Context? = null,
    onSuccess: () -> Unit = {},
    onError: (Exception) -> Unit = {}
  ) {
    val db = getFirestore(context)
    if (db == null) {
      onSuccess()
      return
    }

    val map = hashMapOf(
      "id" to student.id,
      "name" to student.name,
      "nisn" to student.nisn,
      "className" to student.className,
      "gender" to student.gender,
      "age" to student.age,
      "updatedAt" to System.currentTimeMillis()
    )

    db.collection(COLLECTION_STUDENTS)
      .document(student.id)
      .set(map, SetOptions.merge())
      .addOnSuccessListener { onSuccess() }
      .addOnFailureListener { onError(it) }
  }
}
