package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DisclaimerCard(
  modifier: Modifier = Modifier,
  compact: Boolean = false
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(Color(0xFFFFF8E1))
      .border(1.dp, Color(0xFFFFE082), RoundedCornerShape(14.dp))
      .padding(if (compact) 10.dp else 14.dp)
      .testTag("disclaimer_card")
  ) {
    Row(verticalAlignment = Alignment.Top) {
      Icon(
        imageVector = Icons.Default.WarningAmber,
        contentDescription = "Peringatan Medis",
        tint = Color(0xFFE65100),
        modifier = Modifier
          .size(if (compact) 20.dp else 24.dp)
          .padding(top = 2.dp)
      )
      Spacer(modifier = Modifier.width(10.dp))
      Column {
        Text(
          text = "Catatan Skrining Awal (Bukan Diagnosis Medis)",
          style = MaterialTheme.typography.labelLarge.copy(
            fontWeight = FontWeight.Bold,
            color = Color(0xFFBF360C)
          )
        )
        Text(
          text = "NutriMind AI adalah alat bantu edukasi dan skrining awal gizi untuk siswa madrasah/SMA, BUKAN alat diagnosis medis. Untuk evaluasi dan rekomendasi gizi lebih lanjut, konsultasikan dengan petugas UKS atau tenaga kesehatan di Puskesmas.",
          style = MaterialTheme.typography.bodySmall.copy(
            color = Color(0xFF2E1C14),
            fontSize = if (compact) 11.sp else 12.sp,
            lineHeight = if (compact) 15.sp else 17.sp,
            fontWeight = FontWeight.Medium
          ),
          modifier = Modifier.padding(top = 2.dp)
        )
      }
    }
  }
}
