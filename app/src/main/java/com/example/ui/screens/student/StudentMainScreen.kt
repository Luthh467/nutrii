package com.example.ui.screens.student

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StudentProfile
import com.example.ui.theme.GreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentMainScreen(
  student: StudentProfile,
  onLogout: () -> Unit
) {
  var selectedTab by remember { mutableIntStateOf(0) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = when (selectedTab) {
              0 -> "NutriMind AI"
              1 -> "Cek Gizi Mandiri"
              2 -> "Analisis Makanan"
              3 -> "Cek Gizi Harian"
              else -> "Edukasi Gizi"
            },
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = Color(0xFF1B5E20)
            )
          )
        },
        actions = {
          IconButton(
            onClick = onLogout,
            modifier = Modifier.testTag("logout_button")
          ) {
            Icon(
              imageVector = Icons.Default.ExitToApp,
              contentDescription = "Keluar",
              tint = Color(0xFFC62828)
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    },
    bottomBar = {
      val navColors = NavigationBarItemDefaults.colors(
        selectedIconColor = GreenPrimary,
        selectedTextColor = GreenPrimary,
        indicatorColor = Color(0xFFE8F5E9),
        unselectedIconColor = Color(0xFF64748B),
        unselectedTextColor = Color(0xFF334155)
      )
      NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        modifier = Modifier.testTag("student_bottom_bar")
      ) {
        NavigationBarItem(
          selected = selectedTab == 0,
          onClick = { selectedTab = 0 },
          icon = { Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(20.dp)) },
          label = { Text("Beranda", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
          colors = navColors,
          modifier = Modifier.testTag("nav_home")
        )
        NavigationBarItem(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          icon = { Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(20.dp)) },
          label = { Text("Cek IMT", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
          colors = navColors,
          modifier = Modifier.testTag("nav_imt")
        )
        NavigationBarItem(
          selected = selectedTab == 2,
          onClick = { selectedTab = 2 },
          icon = { Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(20.dp)) },
          label = { Text("Analisis", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
          colors = navColors,
          modifier = Modifier.testTag("nav_food")
        )
        NavigationBarItem(
          selected = selectedTab == 3,
          onClick = { selectedTab = 3 },
          icon = { Icon(Icons.Default.FitnessCenter, contentDescription = null, modifier = Modifier.size(20.dp)) },
          label = { Text("Harian", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
          colors = navColors,
          modifier = Modifier.testTag("nav_daily")
        )
        NavigationBarItem(
          selected = selectedTab == 4,
          onClick = { selectedTab = 4 },
          icon = { Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(20.dp)) },
          label = { Text("Edukasi", fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
          colors = navColors,
          modifier = Modifier.testTag("nav_edu")
        )
      }
    }
  ) { paddingValues ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
      when (selectedTab) {
        0 -> StudentHomeScreen(
          student = student,
          onNavigateTab = { tabIndex -> selectedTab = tabIndex }
        )
        1 -> ScreeningImtScreen(student = student)
        2 -> FoodAnalysisScreen()
        3 -> DailyNutritionScreen(student = student)
        4 -> NutritionEducationScreen(student = student)
      }
    }
  }
}
