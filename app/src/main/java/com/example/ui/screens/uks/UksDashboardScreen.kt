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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AssessmentResult
import com.example.data.model.NutritionCategory
import com.example.data.model.RiskLevel
import com.example.data.repository.NutriMindRepository
import com.example.ui.components.CategoryBadge
import com.example.ui.components.DisclaimerCard
import com.example.ui.components.RiskBadge
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.OrangeSecondary
import com.example.ui.theme.RiskHigh

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UksDashboardScreen(
  onLogout: () -> Unit
) {
  val assessments by NutriMindRepository.assessments.collectAsState()
  val dailyLogs by NutriMindRepository.dailyLogs.collectAsState()
  val syncStatus by NutriMindRepository.syncStatus.collectAsState()
  val context = LocalContext.current

  var selectedTab by remember { mutableIntStateOf(0) } // 0: Skrining IMT, 1: Log Gizi Harian Siswa
  var searchQuery by remember { mutableStateOf("") }
  var selectedClassFilter by remember { mutableStateOf("Semua") }
  var selectedRiskFilter by remember { mutableStateOf("Semua") }
  var selectedStudentDetail by remember { mutableStateOf<AssessmentResult?>(null) }
  var isEditingNote by remember { mutableStateOf(false) }
  var uksNoteInput by remember { mutableStateOf("") }

  // Statistik agregat
  val totalScreened = assessments.map { it.studentName }.distinct().size
  val normalCount = assessments.count { it.category == NutritionCategory.NORMAL }
  val underweightCount = assessments.count { it.category == NutritionCategory.UNDERWEIGHT }
  val veryUnderweightCount = assessments.count { it.category == NutritionCategory.VERY_UNDERWEIGHT }
  val overweightCount = assessments.count { it.category == NutritionCategory.OVERWEIGHT }
  val obeseCount = assessments.count { it.category == NutritionCategory.OBESE }

  val highRiskCount = assessments.count { it.riskLevel == RiskLevel.HIGH }
  val mediumRiskCount = assessments.count { it.riskLevel == RiskLevel.MEDIUM }
  val lowRiskCount = assessments.count { it.riskLevel == RiskLevel.LOW }

  // Filtered List
  val filteredAssessments = assessments.filter { item ->
    val matchesSearch = item.studentName.contains(searchQuery, ignoreCase = true) ||
      item.className.contains(searchQuery, ignoreCase = true)

    val matchesClass = when (selectedClassFilter) {
      "Kelas X" -> item.className.startsWith("X-") || item.className.startsWith("X ")
      "Kelas XI" -> item.className.startsWith("XI")
      "Kelas XII" -> item.className.startsWith("XII")
      else -> true
    }

    val matchesRisk = when (selectedRiskFilter) {
      "Tinggi" -> item.riskLevel == RiskLevel.HIGH
      "Sedang" -> item.riskLevel == RiskLevel.MEDIUM
      "Rendah" -> item.riskLevel == RiskLevel.LOW
      else -> true
    }

    matchesSearch && matchesClass && matchesRisk
  }

  Scaffold(
    topBar = {
      Column {
        TopAppBar(
          title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                Icons.Default.HealthAndSafety,
                contentDescription = null,
                tint = OrangeSecondary,
                modifier = Modifier.size(24.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                  text = "Dashboard Petugas UKS",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                  text = "Monitoring Skrining Gizi Madrasah/SMA",
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = Color(0xFF334155),
                    fontWeight = FontWeight.Medium
                  )
                )
              }
            }
          },
          actions = {
            IconButton(onClick = onLogout, modifier = Modifier.testTag("uks_logout_button")) {
              Icon(Icons.Default.ExitToApp, contentDescription = "Keluar", tint = Color(0xFFC62828))
            }
          },
          colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
        )

        TabRow(
          selectedTabIndex = selectedTab,
          containerColor = MaterialTheme.colorScheme.surface,
          contentColor = OrangeSecondary
        ) {
          Tab(
            selected = selectedTab == 0,
            onClick = { selectedTab = 0 },
            text = {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Assessment, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Skrining IMT (${assessments.size})", fontWeight = FontWeight.SemiBold)
              }
            },
            modifier = Modifier.testTag("tab_uks_imt")
          )
          Tab(
            selected = selectedTab == 1,
            onClick = { selectedTab = 1 },
            text = {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Restaurant, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Log Gizi Harian (${dailyLogs.size})", fontWeight = FontWeight.SemiBold)
              }
            },
            modifier = Modifier.testTag("tab_uks_daily")
          )
        }
      }
    }
  ) { paddingValues ->
    if (selectedTab == 1) {
      Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
        UksDailyLogsTab(
          dailyLogs = dailyLogs,
          syncStatus = syncStatus
        )
      }
    } else {
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .background(MaterialTheme.colorScheme.background)
          .padding(paddingValues)
          .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp)
      ) {
      // Disclaimer Card
      item {
        DisclaimerCard(compact = true)
        Spacer(modifier = Modifier.height(16.dp))
      }

      // SECTION 1: RINGKASAN UMUM
      item {
        Text(
          text = "Ringkasan Skrining Tingkat Sekolah",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(10.dp))

        // Total & Prioritas Perhatian
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          Card(
            modifier = Modifier.weight(1f).testTag("stat_total_screened"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Text(text = "Total Siswa Diskrining", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF1B5E20)))
              Text(
                text = "$totalScreened Siswa",
                style = MaterialTheme.typography.headlineSmall.copy(
                  fontWeight = FontWeight.ExtraBold,
                  color = Color(0xFF1B5E20)
                )
              )
              Text(
                text = "${assessments.size} data periksa",
                style = MaterialTheme.typography.labelSmall.copy(
                  color = Color(0xFF1B5E20),
                  fontWeight = FontWeight.Medium
                )
              )
            }
          }

          Card(
            modifier = Modifier.weight(1f).testTag("stat_high_risk"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Text(text = "Perlu Intervensi Segera", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFB71C1C)))
              Text(
                text = "$highRiskCount Siswa",
                style = MaterialTheme.typography.headlineSmall.copy(
                  fontWeight = FontWeight.ExtraBold,
                  color = Color(0xFFB71C1C)
                )
              )
              Text(
                text = "Risiko Tinggi (Sangat Kurus / Obesitas)",
                style = MaterialTheme.typography.labelSmall.copy(
                  color = Color(0xFF991B1B),
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Medium
                )
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Distribusi Status Gizi
        Card(
          modifier = Modifier.fillMaxWidth().testTag("nutrition_distribution_card"),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text(
              text = "Distribusi Status Gizi Siswa (IMT/U Kemenkes)",
              style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Bar visual per status
            DistributionRow(label = "Normal", count = normalCount, total = assessments.size, color = GreenPrimary)
            DistributionRow(label = "Kurus", count = underweightCount, total = assessments.size, color = Color(0xFFFB8C00))
            DistributionRow(label = "Sangat Kurus", count = veryUnderweightCount, total = assessments.size, color = Color(0xFFD32F2F))
            DistributionRow(label = "Gemuk", count = overweightCount, total = assessments.size, color = Color(0xFFFF9800))
            DistributionRow(label = "Obesitas", count = obeseCount, total = assessments.size, color = Color(0xFFC2185B))
          }
        }

        Spacer(modifier = Modifier.height(20.dp))
      }

      // SECTION 2: SEARCH & FILTER SISWA
      item {
        Text(
          text = "Daftar Data Skrining Siswa",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(10.dp))

        // Search Input
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("Cari nama siswa atau kelas...") },
          leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { searchQuery = "" }) {
                Icon(Icons.Default.Close, contentDescription = null)
              }
            }
          },
          modifier = Modifier.fillMaxWidth().testTag("uks_search_input"),
          shape = RoundedCornerShape(12.dp),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Class Filter Chips
        Text(
          text = "Filter Jenjang Kelas:",
          style = MaterialTheme.typography.labelSmall.copy(
            color = Color(0xFF334155),
            fontWeight = FontWeight.SemiBold
          )
        )
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 4.dp)) {
          listOf("Semua", "Kelas X", "Kelas XI", "Kelas XII").forEach { filter ->
            FilterChip(
              selected = selectedClassFilter == filter,
              onClick = { selectedClassFilter = filter },
              label = { Text(filter, fontSize = 11.sp) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = OrangeSecondary.copy(alpha = 0.15f),
                selectedLabelColor = OrangeSecondary
              )
            )
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Risk Filter Chips
        Text(
          text = "Filter Tingkat Risiko:",
          style = MaterialTheme.typography.labelSmall.copy(
            color = Color(0xFF334155),
            fontWeight = FontWeight.SemiBold
          )
        )
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 4.dp)) {
          listOf("Semua", "Tinggi", "Sedang", "Rendah").forEach { risk ->
            FilterChip(
              selected = selectedRiskFilter == risk,
              onClick = { selectedRiskFilter = risk },
              label = { Text(risk, fontSize = 11.sp) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = when (risk) {
                  "Tinggi" -> Color(0xFFFFCDD2)
                  "Sedang" -> Color(0xFFFFE0B2)
                  "Rendah" -> Color(0xFFC8E6C9)
                  else -> Color(0xFFE0E0E0)
                }
              )
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))
      }

      // SECTION 3: LIST OF STUDENTS (WITH VISUAL HIGHLIGHTS)
      if (filteredAssessments.isEmpty()) {
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
          ) {
            Text(
              text = "Tidak ada data siswa yang cocok dengan filter pencarian.",
              style = MaterialTheme.typography.bodySmall.copy(
                color = Color(0xFF334155),
                fontWeight = FontWeight.Medium
              ),
              modifier = Modifier.padding(16.dp)
            )
          }
        }
      } else {
        items(filteredAssessments) { item ->
          val isAttentionRequired = item.riskLevel == RiskLevel.HIGH || item.riskLevel == RiskLevel.MEDIUM

          Card(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 5.dp)
              .clip(RoundedCornerShape(14.dp))
              .border(
                width = if (item.riskLevel == RiskLevel.HIGH) 1.5.dp else 0.5.dp,
                color = if (item.riskLevel == RiskLevel.HIGH) Color(0xFFEF5350) else Color(0xFFE0E0E0),
                shape = RoundedCornerShape(14.dp)
              )
              .clickable {
                selectedStudentDetail = item
                uksNoteInput = item.uksNote
              }
              .testTag("student_row_${item.id}"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
              containerColor = if (item.riskLevel == RiskLevel.HIGH) Color(0xFFFFF8F8) else MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  if (item.riskLevel == RiskLevel.HIGH) {
                    Icon(
                      imageVector = Icons.Default.PriorityHigh,
                      contentDescription = "Perhatian UKS",
                      tint = Color(0xFFD32F2F),
                      modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                  }
                  Text(
                    text = item.studentName,
                    style = MaterialTheme.typography.titleSmall.copy(
                      fontWeight = FontWeight.Bold,
                      color = if (item.riskLevel == RiskLevel.HIGH) Color(0xFFB71C1C) else Color(0xFF1E293B)
                    )
                  )
                }

                CategoryBadge(category = item.category)
              }

              Spacer(modifier = Modifier.height(4.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "${item.className} • Usia ${item.age} th • ${if (item.gender == "L") "Laki-laki" else "Perempuan"}",
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF334155),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                  )
                )
                RiskBadge(risk = item.riskLevel)
              }

              Spacer(modifier = Modifier.height(6.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "IMT: ${item.bmi} kg/m² (BB ${item.weightKg} kg, TB ${item.heightCm} cm)",
                  style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF0F172A)
                  )
                )
                Text(
                  text = item.dateFormatted,
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = Color(0xFF475569),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                  )
                )
              }

              if (item.uksNote.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFE0F2F1))
                    .padding(6.dp)
                ) {
                  Text(
                    text = "📝 Tindak Lanjut: ${item.uksNote}",
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = Color(0xFF004D40),
                      fontSize = 11.sp
                    )
                  )
                }
              }
            }
          }
        }
      }
    }
  }

  // DIALOG DETAIL SISWA & EDIT CATATAN UKS
  val detail = selectedStudentDetail
  if (detail != null) {
    val studentHistory = assessments.filter {
      it.studentId == detail.studentId || it.studentName.equals(detail.studentName, ignoreCase = true)
    }

    AlertDialog(
      onDismissRequest = {
        selectedStudentDetail = null
        isEditingNote = false
      },
      title = {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Person, contentDescription = null, tint = GreenPrimary)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = detail.studentName,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
          }
          Text(
            text = "Kelas ${detail.className} • ${detail.age} Tahun • ${if (detail.gender == "L") "Laki-laki" else "Perempuan"}",
            style = MaterialTheme.typography.bodySmall.copy(
              color = Color(0xFF334155),
              fontWeight = FontWeight.Medium
            )
          )
        }
      },
      text = {
        Column(modifier = Modifier.fillMaxWidth()) {
          // Status Saat Ini
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            CategoryBadge(category = detail.category)
            RiskBadge(risk = detail.riskLevel)
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = "IMT Terbaru: ${detail.bmi} kg/m²",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
          )
          Text(
            text = "Berat: ${detail.weightKg} kg • Tinggi: ${detail.heightCm} cm",
            style = MaterialTheme.typography.bodySmall.copy(
              color = Color(0xFF1E293B),
              fontWeight = FontWeight.Medium
            )
          )

          Spacer(modifier = Modifier.height(8.dp))

          // Key factors
          Text(
            text = "Faktor Pengamatan:",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
          )
          detail.keyFactors.forEach { factor ->
            Text(
              text = "• $factor",
              style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 11.sp,
                color = Color(0xFF0F172A),
                fontWeight = FontWeight.Medium
              )
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          // TREN RIWAYAT IMT
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Timeline, contentDescription = null, tint = OrangeSecondary, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Riwayat & Tren Perubahan IMT:",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
            )
          }
          Spacer(modifier = Modifier.height(4.dp))

          studentHistory.forEach { hist ->
            Row(
              modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = hist.dateFormatted,
                style = MaterialTheme.typography.labelSmall.copy(
                  color = Color(0xFF334155),
                  fontWeight = FontWeight.Medium
                )
              )
              Text(
                text = "${hist.bmi} kg/m² (${hist.category.label})",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // CATATAN KONSULTASI / TINDAK LANJUT UKS
          Text(
            text = "Catatan Konsultasi / Tindak Lanjut UKS:",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF004D40))
          )
          Spacer(modifier = Modifier.height(4.dp))

          if (isEditingNote) {
            OutlinedTextField(
              value = uksNoteInput,
              onValueChange = { uksNoteInput = it },
              placeholder = { Text("Tulis rekomendasi, rujukan Puskesmas, atau pemantauan...") },
              modifier = Modifier.fillMaxWidth().height(100.dp).testTag("uks_note_editor_input"),
              shape = RoundedCornerShape(8.dp)
            )
          } else {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFE0F2F1))
                .padding(10.dp)
            ) {
              Text(
                text = detail.uksNote.ifBlank { "Belum ada catatan UKS. Klik 'Edit Catatan' untuk menambahkan." },
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF004D40))
              )
            }
          }
        }
      },
      confirmButton = {
        if (isEditingNote) {
          Button(
            onClick = {
              NutriMindRepository.updateUksNote(detail.id, uksNoteInput, context)
              selectedStudentDetail = detail.copy(uksNote = uksNoteInput)
              isEditingNote = false
            },
            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
            modifier = Modifier.testTag("save_uks_note_button")
          ) {
            Text("Simpan Catatan")
          }
        } else {
          Button(
            onClick = { isEditingNote = true },
            colors = ButtonDefaults.buttonColors(containerColor = OrangeSecondary),
            modifier = Modifier.testTag("edit_uks_note_button")
          ) {
            Text("Edit Catatan UKS")
          }
        }
      },
      dismissButton = {
        TextButton(onClick = { selectedStudentDetail = null; isEditingNote = false }) {
          Text("Tutup")
        }
      }
    )
  }
}
}

@Composable
private fun DistributionRow(
  label: String,
  count: Int,
  total: Int,
  color: Color
) {
  val ratio = if (total > 0) count.toFloat() / total.toFloat() else 0f
  val percentage = (ratio * 100).toInt()

  Column(modifier = Modifier.padding(vertical = 4.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(text = label, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium))
      Text(
        text = "$count siswa ($percentage%)",
        style = MaterialTheme.typography.labelSmall.copy(
          color = Color(0xFF334155),
          fontWeight = FontWeight.SemiBold
        )
      )
    }

    Spacer(modifier = Modifier.height(4.dp))

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(7.dp)
        .clip(CircleShape)
        .background(Color(0xFFEEEEEE))
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth(fraction = maxOf(ratio, 0.02f))
          .height(7.dp)
          .clip(CircleShape)
          .background(color)
      )
    }
  }
}
