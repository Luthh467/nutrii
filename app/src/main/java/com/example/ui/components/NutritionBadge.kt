package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NutritionCategory
import com.example.data.model.RiskLevel
import com.example.ui.theme.NutritionNormal
import com.example.ui.theme.NutritionObese
import com.example.ui.theme.NutritionOverweight
import com.example.ui.theme.NutritionUnderweight
import com.example.ui.theme.NutritionVeryUnderweight
import com.example.ui.theme.RiskHigh
import com.example.ui.theme.RiskLow
import com.example.ui.theme.RiskMedium

@Composable
fun CategoryBadge(
  category: NutritionCategory,
  modifier: Modifier = Modifier
) {
  val (bgColor, textColor, borderColor) = when (category) {
    NutritionCategory.VERY_UNDERWEIGHT -> Triple(Color(0xFFFFEBEE), NutritionVeryUnderweight, Color(0xFFFFCDD2))
    NutritionCategory.UNDERWEIGHT -> Triple(Color(0xFFFFF3E0), NutritionUnderweight, Color(0xFFFFCC80))
    NutritionCategory.NORMAL -> Triple(Color(0xFFE8F5E9), NutritionNormal, Color(0xFFA5D6A7))
    NutritionCategory.OVERWEIGHT -> Triple(Color(0xFFFFE0B2), NutritionOverweight, Color(0xFFFFB74D))
    NutritionCategory.OBESE -> Triple(Color(0xFFFCE4EC), NutritionObese, Color(0xFFF48FB1))
  }

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(8.dp))
      .background(bgColor)
      .border(1.dp, borderColor, RoundedCornerShape(8.dp))
      .padding(horizontal = 10.dp, vertical = 4.dp),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = category.label,
      style = MaterialTheme.typography.labelMedium.copy(
        fontWeight = FontWeight.Bold,
        color = textColor,
        fontSize = 12.sp
      )
    )
  }
}

@Composable
fun RiskBadge(
  risk: RiskLevel,
  modifier: Modifier = Modifier
) {
  val (bgColor, dotColor, textColor, borderColor) = when (risk) {
    RiskLevel.LOW -> Quadruple(Color(0xFFE8F5E9), RiskLow, Color(0xFF1B5E20), Color(0xFFA5D6A7))
    RiskLevel.MEDIUM -> Quadruple(Color(0xFFFFF3E0), RiskMedium, Color(0xFF8C3400), Color(0xFFFFCC80))
    RiskLevel.HIGH -> Quadruple(Color(0xFFFFEBEE), RiskHigh, Color(0xFFB71C1C), Color(0xFFFFCDD2))
  }

  Row(
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .background(bgColor)
      .border(1.dp, borderColor, RoundedCornerShape(12.dp))
      .padding(horizontal = 8.dp, vertical = 3.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(7.dp)
        .clip(CircleShape)
        .background(dotColor)
    )
    Spacer(modifier = Modifier.width(5.dp))
    Text(
      text = "Risiko ${risk.label}",
      style = MaterialTheme.typography.labelSmall.copy(
        fontWeight = FontWeight.Bold,
        color = textColor,
        fontSize = 11.sp
      )
    )
  }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
