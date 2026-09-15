package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.data.model.StudentProfile
import com.example.ui.screens.LoginScreen
import com.example.data.repository.NutriMindRepository
import com.example.ui.screens.student.StudentMainScreen
import com.example.ui.screens.uks.UksDashboardScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    NutriMindRepository.initFirestoreSync(applicationContext)
    setContent {
      MyApplicationTheme {
        NutriMindApp()
      }
    }
  }
}

@Composable
fun NutriMindApp() {
  var currentStudent by remember { mutableStateOf<StudentProfile?>(null) }
  var isUksLoggedIn by remember { mutableStateOf(false) }

  Surface(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
    Crossfade(
      targetState = when {
        isUksLoggedIn -> "UKS"
        currentStudent != null -> "STUDENT"
        else -> "LOGIN"
      },
      label = "app_navigation_crossfade"
    ) { screen ->
      when (screen) {
        "UKS" -> {
          UksDashboardScreen(
            onLogout = { isUksLoggedIn = false }
          )
        }
        "STUDENT" -> {
          val student = currentStudent
          if (student != null) {
            StudentMainScreen(
              student = student,
              onLogout = { currentStudent = null }
            )
          }
        }
        else -> {
          LoginScreen(
            onStudentLogin = { profile ->
              currentStudent = profile
            },
            onUksLogin = {
              isUksLoggedIn = true
            }
          )
        }
      }
    }
  }
}

