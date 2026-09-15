package com.example.ui.screens.student

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyLog
import com.example.data.model.StudentProfile
import com.example.data.nutrition.NutritionCalculator
import com.example.data.repository.NutriMindRepository
import com.example.ui.components.DisclaimerCard
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.OrangeSecondary
import java.util.UUID

@Composable
fun DailyNutritionScreen(
  student: StudentProfile
) {
  val context = LocalContext.current
  val syncStatus by NutriMindRepository.syncStatus.collectAsState()

  // Ambil data BB & TB dari skrining terakhir jika ada
  val assessments by NutriMindRepository.assessments.collectAsState()
  val lastAssessment = assessments.firstOrNull { it.studentId == student.id }
    ?: assessments.firstOrNull { it.studentName.equals(student.name, ignoreCase = true) }

  var weightKg by remember { mutableStateOf(lastAssessment?.weightKg?.toString() ?: "55") }
  var heightCm by remember { mutableStateOf(lastAssessment?.heightCm?.toString() ?: "165") }

  // Checklist Form
  var breakfastOption by remember { mutableStateOf("Ya, Sarapan Lengkap") }
  var activityType by remember { mutableStateOf("Olahraga / Ekskul Sekolah") }
  var activityDurationMinutes by remember { mutableIntStateOf(30) }
  var waterGlasses by remember { mutableIntStateOf(8) }
  var snackHabit by remember { mutableStateOf("Buah Segar / Susu") }

  // Multiplier Aktivitas
  var activityLevel by remember { mutableStateOf("Aktif (3-5x/minggu)") }
  val multiplier = when (activityLevel) {
    "Jarang Olahraga" -> 1.2
    "Cukup Aktif (1-2x/mgg)" -> 1.375
    "Aktif (3-5x/minggu)" -> 1.55
    else -> 1.725
  }

  var currentResultLog by remember { mutableStateOf<DailyLog?>(null) }
  var savedSuccess by remember { mutableStateOf(false) }

  val allLogs by NutriMindRepository.dailyLogs.collectAsState()
  val studentLogs = allLogs.filter {
    it.studentId == student.id || it.studentName.equals(student.name, ignoreCase = true)
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(horizontal = 20.dp),
    contentPadding = PaddingValues(top = 20.dp, bottom = 80.dp)
  ) {
    item {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = Color(0xFF00796B), modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Cek Gizi Harian",
          style = MaterialTheme.typography.headlineSmall.copy(
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B)
          )
        )
      }
      Text(
        text = "Checklist kebiasaan harian & perhitungan estimasi energi Harris-Benedict (BMR & TDEE)",
        style = MaterialTheme.typography.bodySmall.copy(
          color = Color(0xFF334155),
          fontWeight = FontWeight.Medium
        )
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Cloud Database Sync Indicator
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(Color(0xFFE0F2F1))
          .padding(horizontal = 12.dp, vertical = 8.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.CloudDone,
            contentDescription = null,
            tint = Color(0xFF00796B),
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Database Cloud UKS Aktif • Log tersinkronisasi antar-device",
            style = MaterialTheme.typography.labelSmall.copy(
              color = Color(0xFF004D40),
              fontWeight = FontWeight.Bold
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))
      DisclaimerCard(compact = true)
      Spacer(modifier = Modifier.height(16.dp))
    }

    // FORM CHECKLIST
    item {
      Card(
        modifier = Modifier.fillMaxWidth().testTag("daily_checklist_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text(
            text = "Checklist Hari Ini",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )

          Spacer(modifier = Modifier.height(12.dp))

          // 1. Sarapan Pagi
          Text(text = "1. Apakah sudah sarapan hari ini?", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold))
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
            listOf("Ya, Lengkap", "Camilan Saja", "Tidak Sarapan").forEach { opt ->
              FilterChip(
                selected = breakfastOption.startsWith(opt.take(4)),
                onClick = { breakfastOption = opt },
                label = { Text(opt, fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = GreenPrimary.copy(alpha = 0.15f),
                  selectedLabelColor = GreenPrimary
                )
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // 2. Air Putih
          Text(text = "2. Konsumsi Air Putih Hari Ini", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold))
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 6.dp)
          ) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFE0F2F1)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.LocalDrink, contentDescription = null, tint = Color(0xFF00796B), modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))

            IconButton(
              onClick = { if (waterGlasses > 0) waterGlasses-- },
              modifier = Modifier.size(36.dp).background(Color(0xFFF1F5F1), CircleShape)
            ) {
              Icon(Icons.Default.Remove, contentDescription = "Kurang")
            }

            Text(
              text = "$waterGlasses Gelas",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              modifier = Modifier.padding(horizontal = 14.dp)
            )

            IconButton(
              onClick = { if (waterGlasses < 16) waterGlasses++ },
              modifier = Modifier.size(36.dp).background(Color(0xFFF1F5F1), CircleShape)
            ) {
              Icon(Icons.Default.Add, contentDescription = "Tambah")
            }

            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "~${waterGlasses * 250} ml",
              style = MaterialTheme.typography.bodySmall.copy(
                color = Color(0xFF334155),
                fontWeight = FontWeight.SemiBold
              )
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          // 3. Kebiasaan Jajan
          Text(text = "3. Kebiasaan Jajan / Cemilan", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold))
          Row(
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            listOf("Buah / Susu", "Gorengan", "Minuman Manis", "Bekal Rumah").forEach { snack ->
              FilterChip(
                selected = snackHabit.contains(snack.take(4)),
                onClick = { snackHabit = snack },
                label = { Text(snack, fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = OrangeSecondary.copy(alpha = 0.15f),
                  selectedLabelColor = OrangeSecondary
                )
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // 4. Aktivitas Fisik & Durasi
          Text(text = "4. Tingkat Aktivitas Fisik", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold))
          Row(
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            listOf("Jarang Olahraga", "Aktif (3-5x/minggu)").forEach { act ->
              FilterChip(
                selected = activityLevel == act,
                onClick = { activityLevel = act },
                label = { Text(act, fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = Color(0xFF00796B).copy(alpha = 0.15f),
                  selectedLabelColor = Color(0xFF00796B)
                )
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            OutlinedTextField(
              value = activityType,
              onValueChange = { activityType = it },
              label = { Text("Jenis Aktivitas") },
              modifier = Modifier.weight(1.3f),
              shape = RoundedCornerShape(10.dp),
              singleLine = true
            )
            OutlinedTextField(
              value = activityDurationMinutes.toString(),
              onValueChange = { activityDurationMinutes = it.toIntOrNull() ?: 0 },
              label = { Text("Durasi (Mnt)") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              modifier = Modifier.weight(0.9f),
              shape = RoundedCornerShape(10.dp),
              singleLine = true
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Berat & Tinggi untuk Rumus Harris-Benedict
          Text(
            text = "Data Fisik untuk Estimasi Energi (Harris-Benedict):",
            style = MaterialTheme.typography.labelSmall.copy(
              color = Color(0xFF334155),
              fontWeight = FontWeight.SemiBold
            )
          )
          Row(
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            OutlinedTextField(
              value = weightKg,
              onValueChange = { weightKg = it },
              label = { Text("BB (kg)") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(10.dp),
              singleLine = true
            )
            OutlinedTextField(
              value = heightCm,
              onValueChange = { heightCm = it },
              label = { Text("TB (cm)") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(10.dp),
              singleLine = true
            )
          }

          Spacer(modifier = Modifier.height(16.dp))

          Button(
            onClick = {
              val w = weightKg.toDoubleOrNull() ?: 55.0
              val h = heightCm.toDoubleOrNull() ?: 165.0
              val isMale = student.gender.equals("L", true)

              val bmr = NutritionCalculator.calculateBmr(w, h, student.age, isMale)
              val tdee = NutritionCalculator.calculateTdee(bmr, multiplier)

              val feedback = buildString {
                if (breakfastOption.contains("Tidak")) {
                  append("⚠️ Perhatian: Melewatkan sarapan dapat menurunkan konsentrasi pada jam pelajaran pagi. ")
                } else {
                  append("✓ Bagus! Sarapan pagi mendukung kestabilan gula darah. ")
                }
                if (waterGlasses < 8) {
                  append("Tingkatkan minum air putih hingga minimal 8 gelas untuk mencegah dehidrasi di kelas. ")
                } else {
                  append("Cairan tubuh tercukupi dengan baik (${waterGlasses} gelas). ")
                }
                if (snackHabit.contains("Gorengan") || snackHabit.contains("Manis")) {
                  append("Coba variasikan dengan camilan buah potong atau kacang rebus saat istirahat sekolah.")
                }
              }

              currentResultLog = DailyLog(
                id = "dlg-${UUID.randomUUID()}",
                studentId = student.id,
                studentName = student.name,
                dateFormatted = NutriMindRepository.getTodayDateFormatted(),
                hasBreakfast = !breakfastOption.contains("Tidak"),
                physicalActivityType = activityType,
                physicalActivityMinutes = activityDurationMinutes,
                waterGlasses = waterGlasses,
                snackHabit = snackHabit,
                bmr = bmr,
                tdee = tdee,
                activityLevelLabel = activityLevel,
                feedbackNotes = feedback
              )
              savedSuccess = false
            },
            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("calculate_daily_button"),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B)),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Hitung Kebutuhan Energi & Log", fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    // HASIL ESTIMASI ENERGI
    item {
      AnimatedVisibility(visible = currentResultLog != null) {
        val log = currentResultLog ?: return@AnimatedVisibility
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .testTag("daily_log_result_card"),
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Text(
              text = "Estimasi Kebutuhan Energi Harian",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00796B)
              )
            )
            Text(
              text = "Rumus Harris-Benedict (Revisi) x Faktor Aktivitas",
              style = MaterialTheme.typography.bodySmall.copy(
                color = Color(0xFF334155),
                fontWeight = FontWeight.Medium
              )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Box BMR & TDEE
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2F1))
              ) {
                Column(modifier = Modifier.padding(12.dp)) {
                  Text(text = "BMR (Basal)", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF004D40), fontWeight = FontWeight.Bold))
                  Text(
                    text = "${log.bmr.toInt()} kkal",
                    style = MaterialTheme.typography.titleLarge.copy(
                      fontWeight = FontWeight.ExtraBold,
                      color = Color(0xFF004D40)
                    )
                  )
                  Text(
                    text = "Energi organ dasar",
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = Color(0xFF004D40),
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Medium
                    )
                  )
                }
              }

              Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE0B2))
              ) {
                Column(modifier = Modifier.padding(12.dp)) {
                  Text(text = "TDEE (Total Energi)", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF7C2D12), fontWeight = FontWeight.Bold))
                  Text(
                    text = "${log.tdee.toInt()} kkal",
                    style = MaterialTheme.typography.titleLarge.copy(
                      fontWeight = FontWeight.ExtraBold,
                      color = Color(0xFF7C2D12)
                    )
                  )
                  Text(
                    text = "Kebutuhan per hari",
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = Color(0xFF7C2D12),
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Medium
                    )
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Evaluasi Feedback
            Text(
              text = "Evaluasi Kebiasaan Hari Ini:",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = log.feedbackNotes,
              style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF263238), lineHeight = 17.sp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (!savedSuccess) {
              Button(
                onClick = {
                  NutriMindRepository.addDailyLog(log, context)
                  savedSuccess = true
                },
                modifier = Modifier.fillMaxWidth().height(46.dp).testTag("save_daily_log_button"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00796B)),
                shape = RoundedCornerShape(12.dp)
              ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Simpan ke Database Cloud & UKS", fontWeight = FontWeight.Bold)
              }
            } else {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(10.dp))
                  .background(Color(0xFFD7EED8))
                  .padding(12.dp),
                contentAlignment = Alignment.Center
              ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      Icons.Default.CloudDone,
                      contentDescription = null,
                      tint = Color(0xFF1B5E20),
                      modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = "Tersimpan ke Database Cloud!",
                      style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F3812)
                      )
                    )
                  }
                  Spacer(modifier = Modifier.height(2.dp))
                  Text(
                    text = "Data log gizi langsung masuk ke Dashboard UKS & tersimpan aman lintas device.",
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = Color(0xFF1B5E20),
                      textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = "*Catatan: Nilai TDEE ini adalah perkiraan edukatif kebutuhan energi harian, bukan resep gizi klinis.",
              style = MaterialTheme.typography.labelSmall.copy(
                color = Color(0xFF475569),
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
              )
            )
          }
        }
      }
    }

    // RIWAYAT LOG HARIAN
    item {
      Spacer(modifier = Modifier.height(24.dp))
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.History, contentDescription = null, tint = Color(0xFF00796B), modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Riwayat Log Harian Saya (${studentLogs.size})",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
      }
      Spacer(modifier = Modifier.height(10.dp))
    }

    if (studentLogs.isEmpty()) {
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
        ) {
          Text(
            text = "Belum ada catatan log harian. Isi checklist di atas untuk mencatat kebiasaan pertamamu.",
            style = MaterialTheme.typography.bodySmall.copy(
              color = Color(0xFF334155),
              fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(16.dp)
          )
        }
      }
    } else {
      items(studentLogs) { item ->
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(text = item.dateFormatted, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
              Text(
                text = "TDEE: ${item.tdee.toInt()} kkal",
                style = MaterialTheme.typography.labelMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF9A3412)
                )
              )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Sarapan: ${if (item.hasBreakfast) "Ya" else "Tidak"} • Air: ${item.waterGlasses} gelas • Aktivitas: ${item.physicalActivityType} (${item.physicalActivityMinutes} mnt)",
              style = MaterialTheme.typography.bodySmall.copy(
                color = Color(0xFF1E293B),
                fontWeight = FontWeight.Medium
              )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = item.feedbackNotes,
              style = MaterialTheme.typography.labelSmall.copy(
                color = Color(0xFF334155),
                lineHeight = 14.sp,
                fontWeight = FontWeight.Medium
              )
            )
          }
        }
      }
    }
  }
}
