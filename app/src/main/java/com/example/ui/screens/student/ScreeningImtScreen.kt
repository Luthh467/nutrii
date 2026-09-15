package com.example.ui.screens.student

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.data.model.AssessmentResult
import com.example.data.model.StudentProfile
import com.example.data.nutrition.NutritionCalculator
import com.example.data.repository.NutriMindRepository
import com.example.ui.components.CategoryBadge
import com.example.ui.components.DisclaimerCard
import com.example.ui.components.RiskBadge
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.OrangeSecondary
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun ScreeningImtScreen(
  student: StudentProfile
) {
  val context = LocalContext.current
  var ageInput by remember { mutableStateOf(student.age.toString()) }
  var genderInput by remember { mutableStateOf(student.gender) } // "L" atau "P"
  var weightInput by remember { mutableStateOf("") }
  var heightInput by remember { mutableStateOf("") }
  var validationError by remember { mutableStateOf("") }

  var currentResult by remember { mutableStateOf<AssessmentResult?>(null) }
  var saveSuccessMessage by remember { mutableStateOf(false) }

  val assessments by NutriMindRepository.assessments.collectAsState()
  val myHistory = assessments.filter {
    it.studentId == student.id || it.studentName.equals(student.name, ignoreCase = true)
  }

  val scope = rememberCoroutineScope()

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(horizontal = 20.dp),
    contentPadding = PaddingValues(top = 20.dp, bottom = 80.dp)
  ) {
    item {
      Text(
        text = "Cek Gizi Mandiri",
        style = MaterialTheme.typography.headlineSmall.copy(
          fontWeight = FontWeight.Bold,
          color = GreenPrimary
        )
      )
      Text(
        text = "Skrining IMT/U Kemenkes RI (Permenkes No. 2 Tahun 2020)",
        style = MaterialTheme.typography.bodySmall.copy(
          color = Color(0xFF334155),
          fontWeight = FontWeight.Medium
        )
      )

      Spacer(modifier = Modifier.height(14.dp))
      DisclaimerCard(compact = true)
      Spacer(modifier = Modifier.height(16.dp))
    }

    // Input Form Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth().testTag("imt_input_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text(
            text = "Data Fisik & Pertumbuhan",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )
          Text(
            text = "Masukkan data pengukuran tubuh terbaru",
            style = MaterialTheme.typography.bodySmall.copy(
              color = Color(0xFF334155),
              fontWeight = FontWeight.Medium
            )
          )

          Spacer(modifier = Modifier.height(14.dp))

          // Gender Selector
          Text(
            text = "Jenis Kelamin",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
          )
          Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 4.dp)) {
            FilterChip(
              selected = genderInput == "L",
              onClick = { genderInput = "L" },
              label = { Text("Laki-laki (Siswa)") },
              leadingIcon = if (genderInput == "L") { { Icon(Icons.Default.Check, null) } } else null,
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = GreenPrimary.copy(alpha = 0.15f),
                selectedLabelColor = GreenPrimary
              ),
              modifier = Modifier.testTag("gender_male_chip")
            )
            FilterChip(
              selected = genderInput == "P",
              onClick = { genderInput = "P" },
              label = { Text("Perempuan (Siswi)") },
              leadingIcon = if (genderInput == "P") { { Icon(Icons.Default.Check, null) } } else null,
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = GreenPrimary.copy(alpha = 0.15f),
                selectedLabelColor = GreenPrimary
              ),
              modifier = Modifier.testTag("gender_female_chip")
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Usia
          OutlinedTextField(
            value = ageInput,
            onValueChange = { ageInput = it; validationError = "" },
            label = { Text("Usia Siswa (15 - 18 Tahun)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth().testTag("age_input"),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
          )

          Spacer(modifier = Modifier.height(12.dp))

          // Berat Badan & Tinggi Badan
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            OutlinedTextField(
              value = weightInput,
              onValueChange = { weightInput = it; validationError = "" },
              label = { Text("Berat Badan") },
              placeholder = { Text("50") },
              suffix = { Text("kg") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
              modifier = Modifier.weight(1f).testTag("weight_input"),
              shape = RoundedCornerShape(12.dp),
              singleLine = true
            )

            OutlinedTextField(
              value = heightInput,
              onValueChange = { heightInput = it; validationError = "" },
              label = { Text("Tinggi Badan") },
              placeholder = { Text("165") },
              suffix = { Text("cm") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
              modifier = Modifier.weight(1f).testTag("height_input"),
              shape = RoundedCornerShape(12.dp),
              singleLine = true
            )
          }

          // Rumus Info Box
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 10.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(Color(0xFFF1F8E9))
              .padding(8.dp)
          ) {
            Text(
              text = "Rumus Kemenkes: IMT = Berat Badan (kg) ÷ (Tinggi Badan (m))²",
              style = MaterialTheme.typography.labelSmall.copy(
                color = Color(0xFF33691E),
                fontSize = 11.sp
              )
            )
          }

          if (validationError.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = validationError,
              color = MaterialTheme.colorScheme.error,
              style = MaterialTheme.typography.bodySmall
            )
          }

          Spacer(modifier = Modifier.height(16.dp))

          Button(
            onClick = {
              val age = ageInput.toIntOrNull()
              val weight = weightInput.toDoubleOrNull()
              val height = heightInput.toDoubleOrNull()

              if (age == null || age !in 15..18) {
                validationError = "Masukkan usia remaja antara 15 hingga 18 tahun."
                return@Button
              }
              if (weight == null || weight !in 20.0..200.0) {
                validationError = "Masukkan berat badan yang valid (20 - 200 kg)."
                return@Button
              }
              if (height == null || height !in 100.0..220.0) {
                validationError = "Masukkan tinggi badan yang valid (100 - 220 cm)."
                return@Button
              }

              val bmi = NutritionCalculator.calculateBmi(weight, height)
              val classification = NutritionCalculator.evaluateBmiAge(bmi, age, genderInput)

              currentResult = AssessmentResult(
                id = "asm-${UUID.randomUUID()}",
                studentId = student.id,
                studentName = student.name,
                className = student.className,
                gender = genderInput,
                age = age,
                weightKg = weight,
                heightCm = height,
                bmi = bmi,
                category = classification.category,
                riskLevel = classification.riskLevel,
                keyFactors = classification.keyFactors,
                educationalAdvice = classification.advice,
                dateFormatted = NutriMindRepository.getTodayDateFormatted()
              )
              saveSuccessMessage = false
            },
            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("calculate_imt_button"),
            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Hitung & Klasifikasi IMT", fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    // HASIL SKRINING
    item {
      AnimatedVisibility(visible = currentResult != null) {
        val result = currentResult ?: return@AnimatedVisibility
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .testTag("assessment_result_card"),
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Text(
              text = "Hasil Skrining Awal Gizi",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = GreenPrimary
              )
            )
            Text(
              text = "Berdasarkan standar IMT/U Kemenkes RI (Permenkes No. 2/2020)",
              style = MaterialTheme.typography.bodySmall.copy(
                color = Color(0xFF334155),
                fontWeight = FontWeight.Medium
              )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // IMT Highlight Box
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFFF9FAF8))
                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(14.dp))
                .padding(16.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text(
                    text = "Nilai IMT Siswa",
                    style = MaterialTheme.typography.bodySmall.copy(
                      color = Color(0xFF334155),
                      fontWeight = FontWeight.SemiBold
                    )
                  )
                  Text(
                    text = "${result.bmi} kg/m²",
                    style = MaterialTheme.typography.headlineLarge.copy(
                      fontWeight = FontWeight.ExtraBold,
                      color = Color(0xFF1B5E20)
                    )
                  )
                  Text(
                    text = "BB: ${result.weightKg} kg • TB: ${result.heightCm} cm",
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = Color(0xFF0F172A),
                      fontWeight = FontWeight.Medium
                    )
                  )
                }

                Column(horizontalAlignment = Alignment.End) {
                  CategoryBadge(category = result.category)
                  Spacer(modifier = Modifier.height(6.dp))
                  RiskBadge(risk = result.riskLevel)
                }
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Faktor yang perlu diperhatikan
            Text(
              text = "Faktor yang Perlu Diperhatikan:",
              style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF263238)
              )
            )
            Spacer(modifier = Modifier.height(6.dp))
            result.keyFactors.forEach { factor ->
              Row(modifier = Modifier.padding(vertical = 3.dp), verticalAlignment = Alignment.Top) {
                Text(text = "•", color = OrangeSecondary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 6.dp))
                Text(
                  text = factor,
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF37474F),
                    lineHeight = 17.sp
                  )
                )
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Saran Edukatif
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFE8F5E9))
                .padding(12.dp)
            ) {
              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = Color(0xFF2E7D32),
                    modifier = Modifier.size(18.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "Saran Edukatif untuk Siswa:",
                    style = MaterialTheme.typography.labelMedium.copy(
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFF1B5E20)
                    )
                  )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = result.educationalAdvice,
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF1B5E20),
                    lineHeight = 17.sp
                  )
                )
              }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!saveSuccessMessage) {
              Button(
                onClick = {
                  NutriMindRepository.addAssessment(result, context)
                  saveSuccessMessage = true
                },
                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("save_assessment_button"),
                colors = ButtonDefaults.buttonColors(containerColor = OrangeSecondary),
                shape = RoundedCornerShape(12.dp)
              ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Simpan ke Database Skrining UKS", fontWeight = FontWeight.Bold)
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    Icons.Default.CloudDone,
                    contentDescription = null,
                    tint = Color(0xFF1B5E20),
                    modifier = Modifier.size(18.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "✓ Skrining tersimpan di Cloud Database & terpantau di Dashboard UKS!",
                    style = MaterialTheme.typography.bodySmall.copy(
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFF0F3812)
                    )
                  )
                }
              }
            }
          }
        }
      }
    }

    // RIWAYAT SKRINING SISWA
    item {
      Spacer(modifier = Modifier.height(24.dp))
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.History, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Riwayat Skrining Saya (${myHistory.size})",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
      }
      Spacer(modifier = Modifier.height(10.dp))
    }

    if (myHistory.isEmpty()) {
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
        ) {
          Text(
            text = "Belum ada riwayat tersimpan. Isi form di atas untuk menyimpan skrining pertamamu.",
            style = MaterialTheme.typography.bodySmall.copy(
              color = Color(0xFF334155),
              fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(16.dp)
          )
        }
      }
    } else {
      items(myHistory) { item ->
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
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = item.dateFormatted,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
              )
              Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                CategoryBadge(category = item.category)
                RiskBadge(risk = item.riskLevel)
              }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "IMT: ${item.bmi} kg/m² • BB: ${item.weightKg} kg • TB: ${item.heightCm} cm",
              style = MaterialTheme.typography.bodySmall.copy(
                color = Color(0xFF1E293B),
                fontWeight = FontWeight.Medium
              )
            )
            if (item.uksNote.isNotBlank()) {
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "Catatan Petugas UKS: \"${item.uksNote}\"",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = Color(0xFF004D40),
                  fontWeight = FontWeight.Medium
                )
              )
            }
          }
        }
      }
    }
  }
}
