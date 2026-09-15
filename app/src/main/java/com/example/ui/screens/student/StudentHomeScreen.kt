package com.example.ui.screens.student

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AssessmentResult
import com.example.data.model.StudentProfile
import com.example.data.repository.NutriMindRepository
import com.example.ui.components.CategoryBadge
import com.example.ui.components.DisclaimerCard
import com.example.ui.components.RiskBadge
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.OrangeSecondary

@Composable
fun StudentHomeScreen(
  student: StudentProfile,
  onNavigateTab: (Int) -> Unit // 1: Cek Mandiri, 2: Analisis Makanan, 3: Cek Harian, 4: Edukasi
) {
  val assessments by NutriMindRepository.assessments.collectAsState()
  val studentAssessment = assessments.firstOrNull { it.studentId == student.id }
    ?: assessments.firstOrNull { it.studentName.equals(student.name, ignoreCase = true) }

  val tips = NutriMindRepository.dailyTips
  var currentTipIndex by remember { mutableIntStateOf(0) }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background),
    contentPadding = PaddingValues(bottom = 80.dp)
  ) {
    // Top Hero Card
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
          .background(
            Brush.verticalGradient(
              colors = listOf(Color(0xFF2E7D32), Color(0xFF388E3C))
            )
          )
          .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 24.dp)
      ) {
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Halo, ${student.name}! 👋",
                style = MaterialTheme.typography.headlineSmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                ),
                modifier = Modifier.testTag("greeting_text")
              )
              Text(
                text = "Kelas ${student.className} • NISN ${student.nisn}",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = Color.White.copy(alpha = 0.85f)
                )
              )
            }

            Box(
              modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = if (student.gender.equals("L", true)) "👦" else "👧",
                fontSize = 24.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Card Ringkasan Skrining Terakhir
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .testTag("last_assessment_summary_card"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "Ringkasan Cek Gizi Terakhir",
                  style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20)
                  )
                )
                if (studentAssessment != null) {
                  Text(
                    text = studentAssessment.dateFormatted,
                    style = MaterialTheme.typography.labelSmall.copy(
                      color = Color(0xFF334155),
                      fontWeight = FontWeight.Medium
                    )
                  )
                }
              }

              Spacer(modifier = Modifier.height(10.dp))

              if (studentAssessment != null) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                      text = "IMT ${studentAssessment.bmi}",
                      style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1B5E20)
                      )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = "kg/m²",
                      style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF334155),
                        fontWeight = FontWeight.SemiBold
                      ),
                      modifier = Modifier.padding(bottom = 4.dp)
                    )
                  }

                  Column(horizontalAlignment = Alignment.End) {
                    CategoryBadge(category = studentAssessment.category)
                    Spacer(modifier = Modifier.height(4.dp))
                    RiskBadge(risk = studentAssessment.riskLevel)
                  }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                  text = studentAssessment.educationalAdvice,
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF1E293B),
                    fontWeight = FontWeight.Medium,
                    lineHeight = 16.sp
                  ),
                  maxLines = 2
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                  onClick = { onNavigateTab(1) },
                  modifier = Modifier.fillMaxWidth(),
                  shape = RoundedCornerShape(10.dp)
                ) {
                  Text("Perbarui Cek Gizi Mandiri", fontSize = 13.sp, color = GreenPrimary)
                }
              } else {
                Text(
                  text = "Kamu belum pernah mengisi Cek Gizi Mandiri. Ketahui status IMT dan saran gizi seimbang sekarang!",
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF1E293B),
                    fontWeight = FontWeight.Medium
                  )
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                  onClick = { onNavigateTab(1) },
                  modifier = Modifier.fillMaxWidth(),
                  shape = RoundedCornerShape(10.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                ) {
                  Text("Mulai Cek Gizi Sekarang", fontSize = 13.sp)
                }
              }
            }
          }
        }
      }
    }

    // Section 4 Quick Access Cards
    item {
      Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp)) {
        Text(
          text = "Akses Cepat Fitur",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Text(
          text = "Pilih fitur skrining atau edukasi untuk hari ini",
          style = MaterialTheme.typography.bodySmall.copy(
            color = Color(0xFF334155),
            fontWeight = FontWeight.Medium
          )
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Grid 2x2
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
          FeatureCard(
            title = "Cek Gizi Mandiri",
            subtitle = "Hitung IMT/U Kemenkes",
            icon = Icons.Default.Calculate,
            color = Color(0xFF2E7D32),
            containerColor = Color(0xFFE8F5E9),
            modifier = Modifier.weight(1f).testTag("quick_card_imt"),
            onClick = { onNavigateTab(1) }
          )
          FeatureCard(
            title = "Analisis Makanan",
            subtitle = "Foto & AI Gemini",
            icon = Icons.Default.AutoAwesome,
            color = Color(0xFFE65100),
            containerColor = Color(0xFFFFE0B2),
            modifier = Modifier.weight(1f).testTag("quick_card_food"),
            onClick = { onNavigateTab(2) }
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
          FeatureCard(
            title = "Cek Gizi Harian",
            subtitle = "BMR & Log TDEE",
            icon = Icons.Default.FitnessCenter,
            color = Color(0xFF00796B),
            containerColor = Color(0xFFE0F2F1),
            modifier = Modifier.weight(1f).testTag("quick_card_daily"),
            onClick = { onNavigateTab(3) }
          )
          FeatureCard(
            title = "Edukasi Gizi",
            subtitle = "Artikel & Kuis Asik",
            icon = Icons.Default.MenuBook,
            color = Color(0xFF6A1B9A),
            containerColor = Color(0xFFF3E5F5),
            modifier = Modifier.weight(1f).testTag("quick_card_edu"),
            onClick = { onNavigateTab(4) }
          )
        }
      }
    }

    // Dynamic Daily Tip
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 4.dp)
          .testTag("daily_tip_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4))
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Lightbulb,
                contentDescription = null,
                tint = Color(0xFFF57F17),
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Tips Gizi Remaja Hari Ini",
                style = MaterialTheme.typography.titleSmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFFE65100)
                )
              )
            }

            IconButton(
              onClick = {
                currentTipIndex = (currentTipIndex + 1) % tips.size
              },
              modifier = Modifier.size(28.dp).testTag("refresh_tip_button")
            ) {
              Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Ganti Tips",
                tint = Color(0xFFE65100),
                modifier = Modifier.size(18.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = tips[currentTipIndex],
            style = MaterialTheme.typography.bodySmall.copy(
              color = Color(0xFF3E2723),
              fontWeight = FontWeight.Medium,
              lineHeight = 17.sp
            )
          )
        }
      }
    }

    // Mandatory Disclaimer
    item {
      Spacer(modifier = Modifier.height(14.dp))
      DisclaimerCard(
        modifier = Modifier.padding(horizontal = 20.dp),
        compact = false
      )
      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}

@Composable
private fun FeatureCard(
  title: String,
  subtitle: String,
  icon: ImageVector,
  color: Color,
  containerColor: Color,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Card(
    modifier = modifier
      .clip(RoundedCornerShape(16.dp))
      .clickable(onClick = onClick),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = containerColor),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Box(
        modifier = Modifier
          .size(38.dp)
          .clip(CircleShape)
          .background(color.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(imageVector = icon, contentDescription = title, tint = color, modifier = Modifier.size(22.dp))
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = title,
        style = MaterialTheme.typography.titleSmall.copy(
          fontWeight = FontWeight.Bold,
          color = Color(0xFF1E293B)
        )
      )

      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodySmall.copy(
          color = Color(0xFF334155),
          fontWeight = FontWeight.Medium,
          fontSize = 11.sp
        )
      )

      Spacer(modifier = Modifier.height(6.dp))

      val actionColor = when (color) {
        Color(0xFFE65100) -> Color(0xFF9A3412)
        Color(0xFF2E7D32) -> Color(0xFF1B5E20)
        Color(0xFF00796B) -> Color(0xFF004D40)
        Color(0xFF6A1B9A) -> Color(0xFF4A148C)
        else -> color
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = "Buka",
          style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            color = actionColor
          )
        )
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
          imageVector = Icons.Default.ArrowForward,
          contentDescription = null,
          tint = actionColor,
          modifier = Modifier.size(12.dp)
        )
      }
    }
  }
}
