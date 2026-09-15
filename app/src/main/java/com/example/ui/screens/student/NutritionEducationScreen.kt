package com.example.ui.screens.student

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EducationCard
import com.example.data.model.NutritionCategory
import com.example.data.model.QuizQuestion
import com.example.data.model.StudentProfile
import com.example.data.repository.NutriMindRepository
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.OrangeSecondary

@Composable
fun NutritionEducationScreen(
  student: StudentProfile
) {
  var selectedTab by remember { mutableIntStateOf(0) } // 0: Artikel Edukasi, 1: Kuis Interaktif
  var selectedArticle by remember { mutableStateOf<EducationCard?>(null) }

  // Ambil data status skrining siswa untuk rekomendasi terarah
  val assessments by NutriMindRepository.assessments.collectAsState()
  val lastAssessment = assessments.firstOrNull { it.studentId == student.id }
    ?: assessments.firstOrNull { it.studentName.equals(student.name, ignoreCase = true) }

  val articles = NutriMindRepository.educationArticles

  // Quiz state
  var currentQuestionIndex by remember { mutableIntStateOf(0) }
  var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
  var hasAnsweredCurrent by remember { mutableStateOf(false) }
  var quizScore by remember { mutableIntStateOf(0) }
  var isQuizCompleted by remember { mutableStateOf(false) }

  val quizList = NutriMindRepository.quizQuestions

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(horizontal = 20.dp),
    contentPadding = PaddingValues(top = 20.dp, bottom = 80.dp)
  ) {
    item {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.MenuBook, contentDescription = null, tint = Color(0xFF6A1B9A), modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Edukasi Gizi",
          style = MaterialTheme.typography.headlineSmall.copy(
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B)
          )
        )
      }
      Text(
        text = "Modul literasi gizi seimbang, smart snacking, mitos-fakta, dan kuis pemahaman",
        style = MaterialTheme.typography.bodySmall.copy(
          color = Color(0xFF334155),
          fontWeight = FontWeight.Medium
        )
      )

      Spacer(modifier = Modifier.height(14.dp))

      TabRow(
        selectedTabIndex = selectedTab,
        containerColor = Color(0xFFF1F5F1),
        modifier = Modifier.clip(RoundedCornerShape(12.dp))
      ) {
        Tab(
          selected = selectedTab == 0,
          onClick = { selectedTab = 0 },
          text = { Text("Artikel & Tips", fontWeight = FontWeight.SemiBold) },
          modifier = Modifier.testTag("tab_edu_articles")
        )
        Tab(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          text = { Text("Kuis Pemahaman", fontWeight = FontWeight.SemiBold) },
          modifier = Modifier.testTag("tab_edu_quiz")
        )
      }

      Spacer(modifier = Modifier.height(16.dp))
    }

    if (selectedTab == 0) {
      // TAB 1: ARTIKEL EDUKASI
      // Banner Rekomendasi Khusus
      if (lastAssessment != null) {
        item {
          Card(
            modifier = Modifier.fillMaxWidth().testTag("recommended_article_card"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.Star, contentDescription = null, tint = OrangeSecondary, modifier = Modifier.size(20.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                  text = "Rekomendasi Berdasarkan Status Gizimu (${lastAssessment.category.label})",
                  style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFBF360C)
                  )
                )
                Text(
                  text = when (lastAssessment.category) {
                    NutritionCategory.OVERWEIGHT, NutritionCategory.OBESE ->
                      "Disarankan membaca: 'Smart Snacking' dan 'Mitos Melewatkan Makan'."
                    NutritionCategory.VERY_UNDERWEIGHT, NutritionCategory.UNDERWEIGHT ->
                      "Disarankan membaca: 'Pentingnya Sarapan Pagi' dan 'Isi Piringku'."
                    else ->
                      "Disarankan membaca: 'Isi Piringku' & 'Hidrasi Cerdas' untuk menjaga kebugaran."
                  },
                  style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF1C1917),
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp
                  )
                )
              }
            }
          }
          Spacer(modifier = Modifier.height(14.dp))
        }
      }

      // Daftar Artikel
      items(articles) { article ->
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { selectedArticle = article }
            .testTag("article_card_${article.id}"),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(Color(0xFFF3E5F5)),
              contentAlignment = Alignment.Center
            ) {
              Text(text = article.iconEmoji, fontSize = 22.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFEDE7F6))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text(
                    text = article.category,
                    style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF512DA8), fontSize = 10.sp)
                  )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "• ${article.readTime}",
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = Color(0xFF334155),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 10.sp
                  )
                )
              }

              Spacer(modifier = Modifier.height(4.dp))

              Text(
                text = article.title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
              )

              Text(
                text = article.summary,
                style = MaterialTheme.typography.bodySmall.copy(
                  color = Color(0xFF334155),
                  fontWeight = FontWeight.Medium,
                  fontSize = 11.sp
                ),
                maxLines = 2
              )
            }

            Icon(
              imageVector = Icons.Default.ArrowForward,
              contentDescription = "Baca",
              tint = Color(0xFF6A1B9A),
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }
    } else {
      // TAB 2: KUIS INTERAKTIF
      item {
        if (!isQuizCompleted) {
          val q = quizList[currentQuestionIndex]
          Card(
            modifier = Modifier.fillMaxWidth().testTag("quiz_card"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
          ) {
            Column(modifier = Modifier.padding(18.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "Soal ${currentQuestionIndex + 1} dari ${quizList.size}",
                  style = MaterialTheme.typography.labelMedium.copy(
                    color = Color(0xFF6A1B9A),
                    fontWeight = FontWeight.Bold
                  )
                )
                Text(
                  text = "Skor: $quizScore",
                  style = MaterialTheme.typography.labelMedium.copy(
                    color = GreenPrimary,
                    fontWeight = FontWeight.Bold
                  )
                )
              }

              Spacer(modifier = Modifier.height(12.dp))

              Text(
                text = q.question,
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  lineHeight = 22.sp
                )
              )

              Spacer(modifier = Modifier.height(16.dp))

              // Pilihan Jawaban
              q.options.forEachIndexed { idx, optionText ->
                val isSelected = selectedOptionIndex == idx
                val isCorrect = idx == q.correctIndex

                val (containerColor, borderCol) = when {
                  !hasAnsweredCurrent -> if (isSelected) Color(0xFFEDE7F6) to Color(0xFF7E57C2) else Color(0xFFF9FAF8) to Color(0xFFE0E0E0)
                  isCorrect -> Color(0xFFE8F5E9) to Color(0xFF4CAF50)
                  isSelected && !isCorrect -> Color(0xFFFFEBEE) to Color(0xFFE57373)
                  else -> Color(0xFFF9FAF8) to Color(0xFFE0E0E0)
                }

                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(containerColor)
                    .clickable(enabled = !hasAnsweredCurrent) {
                      selectedOptionIndex = idx
                    }
                    .padding(14.dp)
                ) {
                  Text(
                    text = "${('A' + idx)}. $optionText",
                    style = MaterialTheme.typography.bodyMedium.copy(
                      fontWeight = if (isSelected || (hasAnsweredCurrent && isCorrect)) FontWeight.Bold else FontWeight.Normal,
                      color = if (hasAnsweredCurrent && isCorrect) Color(0xFF1B5E20) else Color(0xFF1E293B)
                    )
                  )
                }
              }

              // Penjelasan jika sudah dijawab
              if (hasAnsweredCurrent) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFE8F5E9))
                    .padding(12.dp)
                ) {
                  Column {
                    Text(
                      text = "Penjelasan:",
                      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                    )
                    Text(
                      text = q.explanation,
                      style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF1B5E20), fontSize = 11.sp)
                    )
                  }
                }
              }

              Spacer(modifier = Modifier.height(18.dp))

              if (!hasAnsweredCurrent) {
                Button(
                  onClick = {
                    if (selectedOptionIndex != null) {
                      hasAnsweredCurrent = true
                      if (selectedOptionIndex == q.correctIndex) {
                        quizScore += 25
                      }
                    }
                  },
                  enabled = selectedOptionIndex != null,
                  modifier = Modifier.fillMaxWidth().height(46.dp),
                  shape = RoundedCornerShape(12.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A))
                ) {
                  Text("Kunci Jawaban", fontWeight = FontWeight.Bold)
                }
              } else {
                Button(
                  onClick = {
                    if (currentQuestionIndex < quizList.size - 1) {
                      currentQuestionIndex++
                      selectedOptionIndex = null
                      hasAnsweredCurrent = false
                    } else {
                      isQuizCompleted = true
                    }
                  },
                  modifier = Modifier.fillMaxWidth().height(46.dp),
                  shape = RoundedCornerShape(12.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                ) {
                  Text(
                    if (currentQuestionIndex < quizList.size - 1) "Soal Berikutnya →" else "Lihat Hasil Akhir",
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            }
          }
        } else {
          // SELESAI KUIS
          Card(
            modifier = Modifier.fillMaxWidth().testTag("quiz_completed_card"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
          ) {
            Column(
              modifier = Modifier.padding(24.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = Color(0xFFFFB300),
                modifier = Modifier.size(64.dp)
              )
              Spacer(modifier = Modifier.height(10.dp))
              Text(
                text = "Kuis Gizi Selesai!",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
              )
              Text(
                text = "Skor Pemahaman Kamu: $quizScore / 100",
                style = MaterialTheme.typography.headlineSmall.copy(
                  fontWeight = FontWeight.ExtraBold,
                  color = GreenPrimary
                )
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = if (quizScore >= 75)
                  "Luar biasa! Pemahaman gizimu sangat baik untuk mendukung tubuh bugar di sekolah."
                else
                  "Cukup baik! Baca modul edukasi di tab artikel untuk memperdalam pemahaman gizimu.",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = Color(0xFF1E293B),
                  fontWeight = FontWeight.Medium
                ),
                lineHeight = 16.sp
              )
              Spacer(modifier = Modifier.height(20.dp))
              Button(
                onClick = {
                  currentQuestionIndex = 0
                  selectedOptionIndex = null
                  hasAnsweredCurrent = false
                  quizScore = 0
                  isQuizCompleted = false
                },
                modifier = Modifier.fillMaxWidth().height(46.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A))
              ) {
                Text("Ulangi Kuis", fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    }
  }

  // DIALOG BACA ARTIKEL LENGKAP
  val article = selectedArticle
  if (article != null) {
    AlertDialog(
      onDismissRequest = { selectedArticle = null },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(text = article.iconEmoji, fontSize = 24.sp)
          Spacer(modifier = Modifier.width(8.dp))
          Text(text = article.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        }
      },
      text = {
        Column {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(Color(0xFFEDE7F6))
              .padding(horizontal = 8.dp, vertical = 2.dp)
          ) {
            Text(text = "${article.category} • Waktu baca ${article.readTime}", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF512DA8)))
          }
          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = article.fullBody,
            style = MaterialTheme.typography.bodyMedium.copy(
              lineHeight = 20.sp,
              color = Color(0xFF0F172A),
              fontWeight = FontWeight.Normal
            )
          )
        }
      },
      confirmButton = {
        Button(
          onClick = { selectedArticle = null },
          colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
        ) {
          Text("Selesai Membaca")
        }
      }
    )
  }
}
