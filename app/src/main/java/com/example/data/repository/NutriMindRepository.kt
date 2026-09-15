package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.data.firebase.NutriMindFirestoreService
import com.example.data.model.AssessmentResult
import com.example.data.model.DailyLog
import com.example.data.model.EducationCard
import com.example.data.model.NutritionCategory
import com.example.data.model.QuizQuestion
import com.example.data.model.RiskLevel
import com.example.data.model.StudentProfile
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

object NutriMindRepository {

  private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))

  // Akun Petugas UKS Default
  const val UKS_DEFAULT_USER = "petugas_uks"
  const val UKS_DEFAULT_PASS = "uks123"

  private var isSyncInitialized = false
  private var assessmentsListener: ListenerRegistration? = null
  private var dailyLogsListener: ListenerRegistration? = null

  private val _syncStatus = MutableStateFlow("Penyimpanan Cloud & Lokal Siap")
  val syncStatus: StateFlow<String> = _syncStatus.asStateFlow()

  fun initFirestoreSync(context: Context) {
    if (isSyncInitialized) return
    isSyncInitialized = true

    try {
      assessmentsListener = NutriMindFirestoreService.listenToAssessments(context) { remoteList ->
        if (remoteList.isNotEmpty()) {
          _assessments.update { currentList ->
            val remoteMap = remoteList.associateBy { it.id }
            val merged = (remoteList + currentList.filter { !remoteMap.containsKey(it.id) })
              .sortedByDescending { it.timestamp }
            merged
          }
          _syncStatus.value = "Tersinkronisasi Cloud Database UKS"
        }
      }

      dailyLogsListener = NutriMindFirestoreService.listenToDailyLogs(context) { remoteLogs ->
        if (remoteLogs.isNotEmpty()) {
          _dailyLogs.update { currentLogs ->
            val remoteMap = remoteLogs.associateBy { it.id }
            val merged = (remoteLogs + currentLogs.filter { !remoteMap.containsKey(it.id) })
              .sortedByDescending { it.timestamp }
            merged
          }
          _syncStatus.value = "Tersinkronisasi Cloud Database UKS"
        }
      }
    } catch (e: Exception) {
      Log.e("NutriMindRepository", "Firestore sync init: ${e.message}", e)
      _syncStatus.value = "Mode Offline Siap"
    }
  }

  // Preset Siswa Demo untuk Pengujian Cepat
  val demoStudents = listOf(
    StudentProfile("std-1", "Ahmad Fauzi", "0068123456", "XI MIPA 2", "L", 16),
    StudentProfile("std-2", "Nabila Putri", "0068123457", "XI IPS 1", "P", 16),
    StudentProfile("std-3", "Muhammad Rizky", "0059123458", "XII MIPA 1", "L", 17),
    StudentProfile("std-4", "Siti Rahmawati", "0071123459", "X-1", "P", 15),
    StudentProfile("std-5", "Dimas Pratama", "0068123460", "XI MIPA 2", "L", 16),
    StudentProfile("std-6", "Aisyah Zahra", "0059123461", "XII IPS 2", "P", 17),
    StudentProfile("std-7", "Bagus Santoso", "0071123462", "X-2", "L", 15)
  )

  // Riwayat Skrining Gizi (Assessment)
  private val _assessments = MutableStateFlow<List<AssessmentResult>>(
    listOf(
      // Ahmad Fauzi (Normal)
      AssessmentResult(
        id = "asm-1",
        studentId = "std-1",
        studentName = "Ahmad Fauzi",
        className = "XI MIPA 2",
        gender = "L",
        age = 16,
        weightKg = 58.0,
        heightCm = 168.0,
        bmi = 20.5,
        category = NutritionCategory.NORMAL,
        riskLevel = RiskLevel.LOW,
        keyFactors = listOf(
          "Pertumbuhan fisik dan status gizi selaras dengan grafik pertumbuhan remaja",
          "Daya tahan tubuh dan konsentrasi belajar optimal"
        ),
        educationalAdvice = "Pertahankan konsumsi sayur & buah harian serta olahraga teratur.",
        dateFormatted = "10 Sep 2026",
        timestamp = System.currentTimeMillis() - 3 * 86400000L
      ),
      // Nabila Putri (Kurus / Perlu Perhatian)
      AssessmentResult(
        id = "asm-2",
        studentId = "std-2",
        studentName = "Nabila Putri",
        className = "XI IPS 1",
        gender = "P",
        age = 16,
        weightKg = 38.5,
        heightCm = 158.0,
        bmi = 15.4,
        category = NutritionCategory.UNDERWEIGHT,
        riskLevel = RiskLevel.MEDIUM,
        keyFactors = listOf(
          "Asupan energi harian belum mencukupi pertumbuhan puncak",
          "Sering melewatkan sarapan pagi sebelum sekolah",
          "Risiko mudah lelah saat jam pelajaran siang"
        ),
        educationalAdvice = "Tingkatkan porsi makan dan konsumsi cemilan padat kalori sehat seperti kacang & susu.",
        dateFormatted = "08 Sep 2026",
        timestamp = System.currentTimeMillis() - 5 * 86400000L,
        uksNote = "Sudah diberikan konseling awal & vitamin zat besi dari UKS."
      ),
      // Muhammad Rizky (Gemuk / Overweight)
      AssessmentResult(
        id = "asm-3",
        studentId = "std-3",
        studentName = "Muhammad Rizky",
        className = "XII MIPA 1",
        gender = "L",
        age = 17,
        weightKg = 82.0,
        heightCm = 172.0,
        bmi = 27.7,
        category = NutritionCategory.OVERWEIGHT,
        riskLevel = RiskLevel.MEDIUM,
        keyFactors = listOf(
          "Asupan kalori berlebih dari minuman manis dan gorengan kantin",
          "Kurang aktivitas fisik kardio teratur"
        ),
        educationalAdvice = "Kurangi minuman boba/es teh manis dan tingkatkan olahraga futsal/jogging 30 menit.",
        dateFormatted = "05 Sep 2026",
        timestamp = System.currentTimeMillis() - 8 * 86400000L,
        uksNote = "Target penurunan konsumsi gula kantin dan jalan santai 6000 langkah."
      ),
      // Siti Rahmawati (Sangat Kurus / Tinggi Risiko)
      AssessmentResult(
        id = "asm-4",
        studentId = "std-4",
        studentName = "Siti Rahmawati",
        className = "X-1",
        gender = "P",
        age = 15,
        weightKg = 32.0,
        heightCm = 152.0,
        bmi = 13.8,
        category = NutritionCategory.VERY_UNDERWEIGHT,
        riskLevel = RiskLevel.HIGH,
        keyFactors = listOf(
          "Risiko defisiensi energi & mikronutrien kronis (< -3 SD)",
          "Keluhan pusing dan pucat di kelas",
          "Perlu skrining anemia klinis"
        ),
        educationalAdvice = "Wajib dirujuk ke Puskesmas mitra UKS untuk evaluasi status anemia dan PMT.",
        dateFormatted = "03 Sep 2026",
        timestamp = System.currentTimeMillis() - 10 * 86400000L,
        uksNote = "PRIORITAS TINGGI: Rujukan Puskesmas sudah diterbitkan tanggal 4 Sep."
      ),
      // Dimas Pratama (Normal)
      AssessmentResult(
        id = "asm-5",
        studentId = "std-5",
        studentName = "Dimas Pratama",
        className = "XI MIPA 2",
        gender = "L",
        age = 16,
        weightKg = 61.0,
        heightCm = 170.0,
        bmi = 21.1,
        category = NutritionCategory.NORMAL,
        riskLevel = RiskLevel.LOW,
        keyFactors = listOf("Status gizi seimbang, aktif di ekstrakurikuler basket"),
        educationalAdvice = "Jaga hidrasi cairan tubuh selama latihan olahraga.",
        dateFormatted = "11 Sep 2026",
        timestamp = System.currentTimeMillis() - 2 * 86400000L
      ),
      // Aisyah Zahra (Obesitas / Tinggi Risiko)
      AssessmentResult(
        id = "asm-6",
        studentId = "std-6",
        studentName = "Aisyah Zahra",
        className = "XII IPS 2",
        gender = "P",
        age = 17,
        weightKg = 78.0,
        heightCm = 155.0,
        bmi = 32.5,
        category = NutritionCategory.OBESE,
        riskLevel = RiskLevel.HIGH,
        keyFactors = listOf(
          "IMT > +2 SD, beban sendi lutut saat olahraga",
          "Kebiasaan ngemil tengah malam saat belajar ujian"
        ),
        educationalAdvice = "Evaluasi diet bersama petugas gizi dan jadwalkan aktivitas fisik aerobik bertahap.",
        dateFormatted = "02 Sep 2026",
        timestamp = System.currentTimeMillis() - 11 * 86400000L,
        uksNote = "Jadwal pendampingan diet sehat mingguan di ruang UKS."
      ),
      // Bagus Santoso (Normal)
      AssessmentResult(
        id = "asm-7",
        studentId = "std-7",
        studentName = "Bagus Santoso",
        className = "X-2",
        gender = "L",
        age = 15,
        weightKg = 50.0,
        heightCm = 162.0,
        bmi = 19.1,
        category = NutritionCategory.NORMAL,
        riskLevel = RiskLevel.LOW,
        keyFactors = listOf("Status gizi baik, asupan bekal seimbang"),
        educationalAdvice = "Pertahankan kebiasaan membawa bekal sehat dari rumah.",
        dateFormatted = "12 Sep 2026",
        timestamp = System.currentTimeMillis() - 1 * 86400000L
      )
    )
  )
  val assessments: StateFlow<List<AssessmentResult>> = _assessments.asStateFlow()

  // Log Harian Siswa
  private val _dailyLogs = MutableStateFlow<List<DailyLog>>(
    listOf(
      DailyLog(
        id = "dlg-1",
        studentId = "std-1",
        studentName = "Ahmad Fauzi",
        dateFormatted = "12 Sep 2026",
        hasBreakfast = true,
        physicalActivityType = "Futsal / Sepak Bola",
        physicalActivityMinutes = 45,
        waterGlasses = 8,
        snackHabit = "Buah & roti gandum",
        bmr = 1540.0,
        tdee = 2387.0,
        activityLevelLabel = "Aktif (3-5x/minggu)",
        feedbackNotes = "Hebat! Sarapan terpenuhi, aktivitas fisik optimal, dan hidrasi 8 gelas tercapai."
      ),
      DailyLog(
        id = "dlg-2",
        studentId = "std-1",
        studentName = "Ahmad Fauzi",
        dateFormatted = "11 Sep 2026",
        hasBreakfast = false,
        physicalActivityType = "Jalan Santai Sekolah",
        physicalActivityMinutes = 20,
        waterGlasses = 6,
        snackHabit = "Gorengan kantin",
        bmr = 1540.0,
        tdee = 1848.0,
        activityLevelLabel = "Jarang / Sedentary",
        feedbackNotes = "Perhatian: Melewatkan sarapan dapat menurunkan konsentrasi pada jam pelajaran pertama."
      )
    )
  )
  val dailyLogs: StateFlow<List<DailyLog>> = _dailyLogs.asStateFlow()

  // Tips Gizi Berganti-ganti (Dynamic Tips)
  val dailyTips = listOf(
    "Sarapan pagi menyumbang 25% kebutuhan energi harian dan meningkatkan fokus saat ulangan!",
    "Minum 8-10 gelas air putih per hari membantu mencegah kantuk dan pusing saat belajar di kelas.",
    "Smart Snacking: Ganti gorengan berminyak dengan buah segar, kacang rebus, atau susu UHT rendah gula.",
    "Bagi remaja putri, konsumsi Tablet Tambah Darah (TTD) 1x seminggu penting untuk mencegah anemia 5L.",
    "Isi Piringku: 1/3 piring makanan pokok, 1/3 piring sayuran, 1/6 piring lauk pauk, dan 1/6 piring buah.",
    "Kurangi minuman manis botolan/boba! Gula berlebih mempercepat rasa lelah dan menambah timbunan lemak.",
    "Jalan kaki 30 menit sehari atau aktif di ekskul madrasah membantu menjaga berat badan ideal dan mood belajar."
  )

  // Artikel Edukasi Gizi
  val educationArticles = listOf(
    EducationCard(
      id = "edu-1",
      title = "Panduan 'Isi Piringku' untuk Siswa Madrasah/SMA",
      category = "Pola Makan Seimbang",
      readTime = "3 mnt",
      iconEmoji = "🍱",
      summary = "Ketahui komposisi ideal piring makanmu: karbohidrat, protein, sayur, dan buah dalam porsi seimbang.",
      fullBody = "Konsep Isi Piringku dari Kementerian Kesehatan RI membagi satu piring menjadi: 1/3 makanan pokok (nasi, jagung, ubi), 1/3 sayuran hijau/berwarna, 1/6 lauk pauk sumber protein (telur, tempe, tahu, ikan, daging), dan 1/6 buah-buahan segar.\n\nBagi siswa yang banyak berpikir dan beraktivitas di sekolah, porsi ini menjamin pasokan glukosa stabil untuk otak tanpa membuat mengantuk sehabis makan."
    ),
    EducationCard(
      id = "edu-2",
      title = "Smart Snacking: Jajan Kantin Bergizi & Tetap Lezat",
      category = "Camilan Sehat",
      readTime = "2 mnt",
      iconEmoji = "🥜",
      summary = "Strategi memilih jajanan kantin yang mengenyangkan tanpa tumpukan minyak dan pemanis buatan.",
      fullBody = "Cemilan di jam istirahat sekolah tidak harus selalu gorengan tepung atau cireng. Pilihlah opsi cerdas seperti: kacang tanah/kedelai rebus, lemper isi ayam, jagung manis, telur gulung higienis, buah potong, atau susu UHT tawar.\n\nCamilan tinggi serat dan protein lambat dicerna sehingga kamu tetap bertenaga hingga jam pulang sekolah."
    ),
    EducationCard(
      id = "edu-3",
      title = "Pentingnya Sarapan Pagi Sebelum Belajar",
      category = "Kebiasaan Sehat",
      readTime = "3 mnt",
      iconEmoji = "🍳",
      summary = "Mengapa sarapan menentukan performa akademis dan konsentrasi di kelas madrasah/SMA.",
      fullBody = "Setelah berpuasa tidur selama 7-8 jam semalam, cadangan glikogen di hati menurun. Tanpa sarapan, kadar gula darah turun sehingga otak sulit memproses materi matematika, hafalan, atau logika.\n\nSarapan sederhana seperti oatmeal telur, roti telur ceplok, atau nasi lauk tempe dan segelas air hangat sudah cukup membangkitkan metabolisme pagimu."
    ),
    EducationCard(
      id = "edu-4",
      title = "Mitos & Fakta: Melewatkan Makan Bikin Cepat Kurus?",
      category = "Mitos vs Fakta",
      readTime = "4 mnt",
      iconEmoji = "🔍",
      summary = "Bongkar mitos diet keliru remaja yang justru merusak metabolisme dan memicu binge eating.",
      fullBody = "MITOS: 'Kalau mau kurus cepat, jangan makan siang atau makan malam!'\nFAKTA: Melewatkan waktu makan justru memperlambat laju metabolisme tubuh (starvation mode) dan memicu makan berlebih secara impulsif di malam hari.\n\nYang benar adalah mengatur porsi, memperbanyak sayuran dan serat, serta membatasi minuman manis dan gorengan."
    ),
    EducationCard(
      id = "edu-5",
      title = "Waspada Anemia Remaja Putri & Manfaat Tablet Tambah Darah",
      category = "Kesehatan Remaja",
      readTime = "3 mnt",
      iconEmoji = "🩸",
      summary = "Kenali gejala 5L (Lesu, Lelah, Letih, Lemah, Lalai) dan cara pencegahan anemia gizi besi.",
      fullBody = "Remaja putri rentan anemia karena kehilangan zat besi saat menstruasi berkala serta asupan gizi yang kurang. Gejala 5L menyebabkan konsentrasi menurun, mudah mengantuk, dan prestasi belajar turun.\n\nKonsumsilah makanan tinggi zat besi hewani (hati ayam, daging, ikan) dan minum 1 Tablet Tambah Darah (TTD) seminggu sekali yang dibagikan rutin oleh program UKS madrasah/sekolah."
    ),
    EducationCard(
      id = "edu-6",
      title = "Hidrasi Cerdas: Kenapa Air Putih Tak Tergantikan?",
      category = "Hidrasi",
      readTime = "2 mnt",
      iconEmoji = "💧",
      summary = "Dehidrasi ringan 2% saja sudah menurunkan memori jangka pendek dan daya tangkap pelajaran.",
      fullBody = "Minuman kekinian seperti es kopi susu manis, boba, atau teh kemasan mengandung 20-30 gram gula per sajian. Selain menambah kalori kosong, kafein dan gula tinggi bersifat diuretik ringan yang membuat tubuh cepat haus kembali.\n\nBawalah botol minum pribadi berisi air putih minimal 600ml-1 liter ke sekolah dan isi ulang di dispenser UKS/kantin sehat."
    )
  )

  // Kuis Interaktif Gizi Remaja
  val quizQuestions = listOf(
    QuizQuestion(
      id = 1,
      question = "Berapa proporsi makanan pokok dan sayuran menurut panduan 'Isi Piringku' Kemenkes RI?",
      options = listOf(
        "Masing-masing 1/3 piring makan",
        "Makanan pokok 3/4 piring, sayuran 1/4 piring",
        "Sayuran hanya boleh hiasan 5%",
        "Semua bebas tanpa aturan porsi"
      ),
      correctIndex = 0,
      explanation = "Tepat! Konsep 'Isi Piringku' membagi 1 piring menjadi: 1/3 makanan pokok, 1/3 sayuran, 1/6 lauk pauk, dan 1/6 buah-buahan."
    ),
    QuizQuestion(
      id = 2,
      question = "Manakah pilihan 'Smart Snacking' terbaik saat istirahat sekolah?",
      options = listOf(
        "Gorengan bertepung dengan minyak pekat",
        "Kacang rebus, buah segar, atau susu UHT rendah gula",
        "Minuman boba manis porsi jumbo",
        "Cemilan keripik berpengawet pedas ekstrim"
      ),
      correctIndex = 1,
      explanation = "Benar! Kacang rebus, buah segar, dan susu UHT memberi pasokan protein, serat, dan energi stabil tanpa lonjakan gula drastis."
    ),
    QuizQuestion(
      id = 3,
      question = "Mengapa remaja putri dianjurkan minum Tablet Tambah Darah (TTD) 1x per minggu?",
      options = listOf(
        "Agar berat badan bertambah drastis",
        "Mencegah anemia gizi besi akibat menstruasi & mendukung konsentrasi belajar",
        "Sebagai pengganti sarapan pagi",
        "Sebagai suplemen tidur"
      ),
      correctIndex = 1,
      explanation = "Tepat sekali! TTD membantu menggantikan cadangan zat besi yang hilang saat siklus menstruasi dan mencegah gejala 5L di sekolah."
    ),
    QuizQuestion(
      id = 4,
      question = "Apa fungsi utama menghitung IMT (Indeks Massa Tubuh) pada remaja?",
      options = listOf(
        "Sebagai diagnosis mutlak penyakit dalam",
        "Sebagai alat skrining awal status gizi untuk edukasi dan intervensi dini",
        "Untuk menentukan ranking nilai akademik",
        "Untuk membatasi izin mengikuti ujian"
      ),
      correctIndex = 1,
      explanation = "Benar! IMT adalah alat skrining awal (bukan diagnosis klinis) untuk memantau apakah remaja berada pada rentang gizi baik atau berisiko."
    )
  )

  // Method Tambah / Update Skrining
  fun addAssessment(assessment: AssessmentResult, context: Context? = null) {
    _assessments.update { list ->
      listOf(assessment) + list.filter { it.id != assessment.id }
    }
    NutriMindFirestoreService.saveAssessment(assessment, context)
  }

  // Method Tambah Catatan UKS
  fun updateUksNote(assessmentId: String, note: String, context: Context? = null) {
    _assessments.update { list ->
      list.map { item ->
        if (item.id == assessmentId) item.copy(uksNote = note) else item
      }
    }
    NutriMindFirestoreService.updateUksNote(assessmentId, note, context)
  }

  // Method Tambah Log Harian
  fun addDailyLog(log: DailyLog, context: Context? = null) {
    _dailyLogs.update { list ->
      listOf(log) + list.filter { it.id != log.id }
    }
    NutriMindFirestoreService.saveDailyLog(log, context)
  }

  fun saveStudentProfile(student: StudentProfile, context: Context? = null) {
    NutriMindFirestoreService.saveStudentProfile(student, context)
  }

  // Ambil skrining terakhir untuk siswa tertentu
  fun getLatestAssessmentForStudent(studentId: String): AssessmentResult? {
    return _assessments.value.firstOrNull { it.studentId == studentId }
  }

  // Ambil riwayat skrining untuk siswa tertentu
  fun getAssessmentsForStudent(studentId: String): List<AssessmentResult> {
    return _assessments.value.filter { it.studentId == studentId }
  }

  // Ambil riwayat log harian untuk siswa tertentu
  fun getDailyLogsForStudent(studentId: String): List<DailyLog> {
    return _dailyLogs.value.filter { it.studentId == studentId }
  }

  fun getTodayDateFormatted(): String {
    return dateFormat.format(Date())
  }
}
