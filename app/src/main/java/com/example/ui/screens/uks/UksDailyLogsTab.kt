package com.example.ui.screens.uks

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BreakfastDining
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyLog
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.OrangeSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UksDailyLogsTab(
  dailyLogs: List<DailyLog>,
  syncStatus: String
) {
  var searchQuery by remember { mutableStateOf("") }
  var selectedFilter by remember { mutableStateOf("Semua") }
  var selectedLogDetail by remember { mutableStateOf<DailyLog?>(null) }
  var acknowledgedLogs by remember { mutableStateOf(setOf<String>()) }

  // Statistik Agregat Gizi Harian Siswa
  val totalLogs = dailyLogs.size
  val breakfastCount = dailyLogs.count { it.hasBreakfast }
  val skippedBreakfastCount = totalLogs - breakfastCount
  val breakfastPercent = if (totalLogs > 0) (breakfastCount * 100) / totalLogs else 0
  val avgWater = if (totalLogs > 0) dailyLogs.map { it.waterGlasses }.average().toInt() else 0
  val lowWaterCount = dailyLogs.count { it.waterGlasses < 6 }
  val activeExerciseCount = dailyLogs.count { it.physicalActivityMinutes >= 30 }

  // Filter List
  val filteredLogs = dailyLogs.filter { log ->
    val matchesSearch = log.studentName.contains(searchQuery, ignoreCase = true) ||
      log.dateFormatted.contains(searchQuery, ignoreCase = true)

    val matchesFilter = when (selectedFilter) {
      "Sarapan: Ya" -> log.hasBreakfast
      "Lewatkan Sarapan" -> !log.hasBreakfast
      "Kurang Air (<6 Gls)" -> log.waterGlasses < 6
      "Aktivitas Fisik >= 30m" -> log.physicalActivityMinutes >= 30
      else -> true
    }

    matchesSearch && matchesFilter
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp)
  ) {
    // Cloud Status Banner
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(Color(0xFFE0F2F1))
          .border(1.dp, Color(0xFF80CBC4), RoundedCornerShape(12.dp))
          .padding(horizontal = 14.dp, vertical = 10.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.CloudDone,
            contentDescription = null,
            tint = Color(0xFF00796B),
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "Sinkronisasi Real-Time Siswa (Lintas Device)",
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF004D40)
              )
            )
            Text(
              text = "Setiap kali siswa mengisi cek gizi dari smartphone masing-masing, data langsung masuk ke sini.",
              style = MaterialTheme.typography.labelSmall.copy(
                color = Color(0xFF004D40),
                lineHeight = 14.sp
              )
            )
          }
        }
      }
      Spacer(modifier = Modifier.height(16.dp))
    }

    // STATISTIK KARTU GIZI HARIAN
    item {
      Text(
        text = "Ringkasan Kebiasaan Gizi Siswa Hari Ini",
        style = MaterialTheme.typography.titleSmall.copy(
          fontWeight = FontWeight.Bold,
          color = Color(0xFF0F172A)
        )
      )
      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Kartu Total Log
        Card(
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Text(
              text = "Total Log Masuk",
              style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF475569), fontWeight = FontWeight.SemiBold)
            )
            Text(
              text = "$totalLogs",
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            )
            Text(
              text = "Dari semua device",
              style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF64748B), fontSize = 10.sp)
            )
          }
        }

        // Kartu Sarapan
        Card(
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (breakfastPercent >= 70) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
          )
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Text(
              text = "Siswa Sarapan",
              style = MaterialTheme.typography.labelSmall.copy(
                color = if (breakfastPercent >= 70) Color(0xFF1B5E20) else Color(0xFFB45309),
                fontWeight = FontWeight.SemiBold
              )
            )
            Text(
              text = "$breakfastPercent%",
              style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = if (breakfastPercent >= 70) Color(0xFF1B5E20) else Color(0xFFB45309)
              )
            )
            Text(
              text = "$skippedBreakfastCount lewati sarapan",
              style = MaterialTheme.typography.labelSmall.copy(
                color = if (breakfastPercent >= 70) Color(0xFF2E7D32) else Color(0xFFB45309),
                fontSize = 10.sp
              )
            )
          }
        }

        // Kartu Rata-rata Hidrasi
        Card(
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2FE))
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Text(
              text = "Rata2 Air Putih",
              style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF0369A1), fontWeight = FontWeight.SemiBold)
            )
            Text(
              text = "$avgWater Gls",
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = Color(0xFF0369A1))
            )
            Text(
              text = "$lowWaterCount perlu hidrasi",
              style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF0284C7), fontSize = 10.sp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))
    }

    // SEARCH & FILTER CHIPS
    item {
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        placeholder = { Text("Cari nama siswa atau tanggal...", color = Color(0xFF64748B)) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF475569)) },
        modifier = Modifier
          .fillMaxWidth()
          .testTag("search_daily_logs_input"),
        singleLine = true,
        shape = RoundedCornerShape(12.dp)
      )

      Spacer(modifier = Modifier.height(10.dp))

      val filters = listOf("Semua", "Sarapan: Ya", "Lewatkan Sarapan", "Kurang Air (<6 Gls)", "Aktivitas Fisik >= 30m")
      LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(filters) { filter ->
          FilterChip(
            selected = selectedFilter == filter,
            onClick = { selectedFilter = filter },
            label = {
              Text(
                text = filter,
                style = MaterialTheme.typography.labelSmall.copy(
                  color = if (selectedFilter == filter) Color.White else Color(0xFF334155),
                  fontWeight = FontWeight.SemiBold
                )
              )
            },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = GreenPrimary
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))
      Text(
        text = "Menampilkan ${filteredLogs.size} log aktivitas harian siswa:",
        style = MaterialTheme.typography.labelMedium.copy(
          color = Color(0xFF475569),
          fontWeight = FontWeight.Medium
        )
      )
      Spacer(modifier = Modifier.height(10.dp))
    }

    // LIST CARDS
    if (filteredLogs.isEmpty()) {
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(Icons.Default.Restaurant, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "Belum Ada Log Gizi Harian yang Cocok",
              style = MaterialTheme.typography.titleMedium.copy(color = Color(0xFF334155), fontWeight = FontWeight.Bold)
            )
            Text(
              text = "Siswa dapat mengisi form cek gizi harian dari device masing-masing.",
              style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF64748B))
            )
          }
        }
      }
    } else {
      items(filteredLogs) { log ->
        val isAcknowledged = acknowledgedLogs.contains(log.id)

        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { selectedLogDetail = log }
            .testTag("daily_log_item_${log.id}"),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            // Row Nama & Tanggal
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8F5E9)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = GreenPrimary,
                    modifier = Modifier.size(20.dp)
                  )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    text = log.studentName,
                    style = MaterialTheme.typography.titleMedium.copy(
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFF0F172A)
                    )
                  )
                  Text(
                    text = "Tanggal: ${log.dateFormatted}",
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = Color(0xFF475569),
                      fontWeight = FontWeight.Medium
                    )
                  )
                }
              }

              // Status Sarapan Pill
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (log.hasBreakfast) Color(0xFFE8F5E9) else Color(0xFFFFEBEE))
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text(
                  text = if (log.hasBreakfast) "✓ Sarapan" else "⚠️ Lewatkan",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (log.hasBreakfast) Color(0xFF1B5E20) else Color(0xFFC62828)
                  )
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Rincian Aktivitas
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              // Air Minum
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (log.waterGlasses >= 8) Color(0xFFE0F2FE) else Color(0xFFFFF7ED))
                  .padding(horizontal = 8.dp, vertical = 6.dp)
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.LocalDrink,
                    contentDescription = null,
                    tint = if (log.waterGlasses >= 8) Color(0xFF0284C7) else Color(0xFFEA580C),
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "${log.waterGlasses} Gelas Air",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontWeight = FontWeight.SemiBold,
                      color = if (log.waterGlasses >= 8) Color(0xFF0369A1) else Color(0xFFC2410C)
                    )
                  )
                }
              }

              // Olahraga
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(8.dp))
                  .background(Color(0xFFF1F5F9))
                  .padding(horizontal = 8.dp, vertical = 6.dp)
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.DirectionsRun,
                    contentDescription = null,
                    tint = Color(0xFF475569),
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "${log.physicalActivityMinutes} menit",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontWeight = FontWeight.SemiBold,
                      color = Color(0xFF1E293B)
                    )
                  )
                }
              }

              // TDEE
              Box(
                modifier = Modifier
                  .weight(1.1f)
                  .clip(RoundedCornerShape(8.dp))
                  .background(Color(0xFFFEF3C7))
                  .padding(horizontal = 8.dp, vertical = 6.dp)
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = null,
                    tint = Color(0xFFD97706),
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "TDEE: ${log.tdee.toInt()} kkal",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFF92400E)
                    )
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Catatan Feedback Siswa
            Text(
              text = "Evaluasi: ${log.feedbackNotes}",
              style = MaterialTheme.typography.bodySmall.copy(
                color = Color(0xFF334155),
                lineHeight = 16.sp
              ),
              maxLines = 2
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Action UKS
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Kebiasaan snack: ${log.snackHabit}",
                style = MaterialTheme.typography.labelSmall.copy(
                  color = Color(0xFF64748B),
                  fontWeight = FontWeight.Medium
                )
              )

              Button(
                onClick = {
                  acknowledgedLogs = if (isAcknowledged) acknowledgedLogs - log.id else acknowledgedLogs + log.id
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                  containerColor = if (isAcknowledged) Color(0xFFE2E8F0) else OrangeSecondary
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                modifier = Modifier.height(32.dp)
              ) {
                Text(
                  text = if (isAcknowledged) "✓ Sudah Dicek" else "Tandai Dicek",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isAcknowledged) Color(0xFF334155) else Color.White
                  )
                )
              }
            }
          }
        }
      }
    }
  }

  // DIALOG DETAIL LOG SISWA
  if (selectedLogDetail != null) {
    val detail = selectedLogDetail!!
    AlertDialog(
      onDismissRequest = { selectedLogDetail = null },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Restaurant, contentDescription = null, tint = GreenPrimary)
          Spacer(modifier = Modifier.width(8.dp))
          Text("Detail Cek Gizi Harian", fontWeight = FontWeight.Bold)
        }
      },
      text = {
        Column {
          Text(
            text = detail.studentName,
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = Color(0xFF0F172A)
            )
          )
          Text(
            text = "Tanggal: ${detail.dateFormatted}",
            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF475569))
          )

          Spacer(modifier = Modifier.height(12.dp))

          // Card Rincian
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(Color(0xFFF8FAFC))
              .padding(12.dp)
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text("Status Sarapan:", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF334155)))
                Text(
                  text = if (detail.hasBreakfast) "✓ Sarapan Lengkap" else "⚠️ Melewatkan Sarapan",
                  style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (detail.hasBreakfast) Color(0xFF1B5E20) else Color(0xFFC62828)
                  )
                )
              }

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text("Konsumsi Air Putih:", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF334155)))
                Text(
                  text = "${detail.waterGlasses} Gelas / hari",
                  style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (detail.waterGlasses >= 8) Color(0xFF0284C7) else Color(0xFFC2410C)
                  )
                )
              }

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text("Aktivitas Fisik:", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF334155)))
                Text(
                  text = "${detail.physicalActivityMinutes} mnt (${detail.physicalActivityType})",
                  style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                )
              }

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text("Kebiasaan Camilan:", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF334155)))
                Text(
                  text = detail.snackHabit,
                  style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                )
              }

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text("BMR (Basal Rate):", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF334155)))
                Text("${detail.bmr.toInt()} kkal/hari", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
              }

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text("TDEE (Total Energi):", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF334155)))
                Text("${detail.tdee.toInt()} kkal/hari", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFFD97706)))
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          Text(
            text = "Catatan Rekomendasi Siswa:",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = detail.feedbackNotes,
            style = MaterialTheme.typography.bodySmall.copy(
              color = Color(0xFF334155),
              lineHeight = 17.sp
            )
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            acknowledgedLogs = acknowledgedLogs + detail.id
            selectedLogDetail = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
        ) {
          Text("Tandai Sudah Dipantau")
        }
      },
      dismissButton = {
        TextButton(onClick = { selectedLogDetail = null }) {
          Text("Tutup")
        }
      }
    )
  }
}
