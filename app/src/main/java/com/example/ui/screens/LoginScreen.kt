package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.firebase.FirebaseAuthService
import com.example.data.model.StudentProfile
import com.example.data.model.UserRole
import com.example.data.repository.NutriMindRepository
import com.example.ui.components.DisclaimerCard
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.OrangeSecondary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
  onStudentLogin: (StudentProfile) -> Unit,
  onUksLogin: () -> Unit
) {
  var selectedTab by remember { mutableIntStateOf(0) } // 0: Siswa, 1: Petugas UKS

  // Form Siswa State
  var studentName by remember { mutableStateOf("Ahmad Fauzi") }
  var studentNisn by remember { mutableStateOf("0068123456") }
  var studentClass by remember { mutableStateOf("XI MIPA 2") }
  var studentGender by remember { mutableStateOf("L") }
  var studentAge by remember { mutableIntStateOf(16) }
  var studentError by remember { mutableStateOf("") }

  // Form UKS State
  var uksUsername by remember { mutableStateOf("") }
  var uksPassword by remember { mutableStateOf("") }
  var showPassword by remember { mutableStateOf(false) }
  var uksError by remember { mutableStateOf("") }

  val scrollState = rememberScrollState()
  val scope = rememberCoroutineScope()
  val context = LocalContext.current
  val syncStatus by NutriMindRepository.syncStatus.collectAsState()
  var isGoogleSigningIn by remember { mutableStateOf(false) }

  Surface(
    modifier = Modifier.fillMaxSize(),
    color = MaterialTheme.colorScheme.background
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
        .padding(horizontal = 20.dp, vertical = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Spacer(modifier = Modifier.height(12.dp))

      // Cloud Status Pill
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(20.dp))
          .background(Color(0xFFE8F5E9))
          .padding(horizontal = 12.dp, vertical = 6.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.CloudDone,
            contentDescription = null,
            tint = GreenPrimary,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = syncStatus,
            style = MaterialTheme.typography.labelSmall.copy(
              color = Color(0xFF1B5E20),
              fontWeight = FontWeight.SemiBold
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Header Brand
      Box(
        modifier = Modifier
          .size(80.dp)
          .clip(CircleShape)
          .background(
            Brush.linearGradient(
              listOf(Color(0xFFE8F5E9), Color(0xFFFFE0B2))
            )
          ),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.HealthAndSafety,
          contentDescription = "NutriMind Logo",
          tint = GreenPrimary,
          modifier = Modifier.size(46.dp)
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = "NutriMind AI",
        style = MaterialTheme.typography.headlineMedium.copy(
          fontWeight = FontWeight.Bold,
          color = GreenPrimary
        )
      )

      Text(
        text = "Deteksi Dini Masalah Gizi Remaja Madrasah & SMA",
        style = MaterialTheme.typography.bodyMedium.copy(
          color = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
      )

      Spacer(modifier = Modifier.height(20.dp))

      // Disclaimer Banner Wajib
      DisclaimerCard(compact = true)

      Spacer(modifier = Modifier.height(20.dp))

      // Role Tabs
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "PILIHAN PERAN MASUK:",
            style = MaterialTheme.typography.titleSmall.copy(
              fontWeight = FontWeight.ExtraBold,
              color = Color(0xFF0F172A),
              letterSpacing = 0.5.sp
            )
          )
          Spacer(modifier = Modifier.height(10.dp))

          // 2 Card Role Buttons yang Sangat Jelas & Berkontras Tinggi
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            // Option 1: Siswa / Remaja
            val isStudentSelected = selectedTab == 0
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(14.dp))
                .background(if (isStudentSelected) GreenPrimary else Color(0xFFF1F5F9))
                .border(
                  width = if (isStudentSelected) 2.dp else 1.dp,
                  color = if (isStudentSelected) GreenPrimary else Color(0xFFCBD5E1),
                  shape = RoundedCornerShape(14.dp)
                )
                .clickable { selectedTab = 0 }
                .padding(vertical = 12.dp, horizontal = 10.dp)
                .testTag("role_option_student"),
              contentAlignment = Alignment.Center
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    tint = if (isStudentSelected) Color.White else GreenPrimary,
                    modifier = Modifier.size(22.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "Siswa",
                    style = MaterialTheme.typography.titleMedium.copy(
                      fontWeight = FontWeight.Bold,
                      color = if (isStudentSelected) Color.White else Color(0xFF0F172A)
                    )
                  )
                  if (isStudentSelected) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                      imageVector = Icons.Default.CheckCircle,
                      contentDescription = null,
                      tint = Color(0xFF69F0AE),
                      modifier = Modifier.size(16.dp)
                    )
                  }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "Skrining IMT & Kamera",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isStudentSelected) Color.White.copy(alpha = 0.9f) else Color(0xFF475569)
                  )
                )
              }
            }

            // Option 2: Petugas UKS
            val isUksSelected = selectedTab == 1
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(14.dp))
                .background(if (isUksSelected) OrangeSecondary else Color(0xFFF1F5F9))
                .border(
                  width = if (isUksSelected) 2.dp else 1.dp,
                  color = if (isUksSelected) OrangeSecondary else Color(0xFFCBD5E1),
                  shape = RoundedCornerShape(14.dp)
                )
                .clickable { selectedTab = 1 }
                .padding(vertical = 12.dp, horizontal = 10.dp)
                .testTag("role_option_uks"),
              contentAlignment = Alignment.Center
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.HealthAndSafety,
                    contentDescription = null,
                    tint = if (isUksSelected) Color.White else OrangeSecondary,
                    modifier = Modifier.size(22.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "Petugas UKS",
                    style = MaterialTheme.typography.titleMedium.copy(
                      fontWeight = FontWeight.Bold,
                      color = if (isUksSelected) Color.White else Color(0xFF0F172A)
                    )
                  )
                  if (isUksSelected) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                      imageVector = Icons.Default.CheckCircle,
                      contentDescription = null,
                      tint = Color(0xFFFFE082),
                      modifier = Modifier.size(16.dp)
                    )
                  }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "Dashboard & Pantau Gizi",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isUksSelected) Color.White.copy(alpha = 0.9f) else Color(0xFF475569)
                  )
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(20.dp))

          if (selectedTab == 0) {
            // LOGIN SISWA
            Text(
              text = "Pilih Cepat Akun Siswa (Demo Langsung):",
              style = MaterialTheme.typography.labelMedium.copy(color = GreenPrimary, fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              items(NutriMindRepository.demoStudents) { demo ->
                val isSelected = studentNisn == demo.nisn
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) GreenPrimary else Color(0xFFE8F5E9))
                    .border(
                      width = if (isSelected) 2.dp else 1.dp,
                      color = if (isSelected) GreenPrimary else Color(0xFFA5D6A7),
                      shape = RoundedCornerShape(12.dp)
                    )
                    .clickable {
                      studentName = demo.name
                      studentNisn = demo.nisn
                      studentClass = demo.className
                      studentGender = demo.gender
                      studentAge = demo.age
                      studentError = ""
                    }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isSelected) {
                      Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                      Spacer(modifier = Modifier.width(6.dp))
                    }
                    Column {
                      Text(
                        text = demo.name,
                        style = MaterialTheme.typography.labelMedium.copy(
                          color = if (isSelected) Color.White else Color(0xFF1B5E20),
                          fontWeight = FontWeight.Bold
                        )
                      )
                      Text(
                        text = "${demo.className} • Usia ${demo.age} th",
                        style = MaterialTheme.typography.labelSmall.copy(
                          color = if (isSelected) Color.White.copy(alpha = 0.85f) else Color(0xFF2E7D32),
                          fontSize = 10.sp
                        )
                      )
                    }
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
              text = "Atau Isi / Ubah Data Siswa Manual:",
              style = MaterialTheme.typography.labelMedium.copy(
                color = Color(0xFF334155),
                fontWeight = FontWeight.SemiBold
              )
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
              value = studentName,
              onValueChange = { studentName = it; studentError = "" },
              label = { Text("Nama Lengkap Siswa") },
              leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
              modifier = Modifier.fillMaxWidth().testTag("student_name_input"),
              singleLine = true,
              shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
              value = studentNisn,
              onValueChange = { studentNisn = it; studentError = "" },
              label = { Text("NISN (Nomor Induk Siswa)") },
              leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              modifier = Modifier.fillMaxWidth().testTag("student_nisn_input"),
              singleLine = true,
              shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
              value = studentClass,
              onValueChange = { studentClass = it; studentError = "" },
              label = { Text("Kelas (contoh: XI MIPA 2 / X-1)") },
              leadingIcon = { Icon(Icons.Default.School, contentDescription = null) },
              modifier = Modifier.fillMaxWidth().testTag("student_class_input"),
              singleLine = true,
              shape = RoundedCornerShape(12.dp)
            )

            if (studentError.isNotBlank()) {
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = studentError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
              )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
              onClick = {
                if (studentName.isBlank() || studentNisn.isBlank() || studentClass.isBlank()) {
                  studentError = "Mohon lengkapi Nama, NISN, dan Kelas."
                } else {
                  val profile = StudentProfile(
                    id = "std-${studentNisn.hashCode()}",
                    name = studentName.trim(),
                    nisn = studentNisn.trim(),
                    className = studentClass.trim(),
                    gender = studentGender,
                    age = studentAge
                  )
                  NutriMindRepository.saveStudentProfile(profile, context)
                  onStudentLogin(profile)
                }
              },
              modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("student_login_button"),
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
            ) {
              Text("Masuk Sebagai Siswa", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFCBD5E1))
              Text(
                text = "  ATAU  ",
                style = MaterialTheme.typography.labelSmall.copy(
                  color = Color(0xFF64748B),
                  fontWeight = FontWeight.Bold
                )
              )
              HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFCBD5E1))
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Google Sign-In Button with Firebase Auth
            OutlinedButton(
              onClick = {
                isGoogleSigningIn = true
                studentError = ""
                scope.launch {
                  FirebaseAuthService.signInWithGoogle(
                    context = context,
                    onSuccess = { profile ->
                      isGoogleSigningIn = false
                      onStudentLogin(profile)
                    },
                    onError = { err ->
                      isGoogleSigningIn = false
                      studentError = err
                    }
                  )
                }
              },
              modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("google_signin_button"),
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF1E293B))
            ) {
              if (isGoogleSigningIn) {
                CircularProgressIndicator(
                  modifier = Modifier.size(22.dp),
                  strokeWidth = 2.dp,
                  color = GreenPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Menghubungkan Google...", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
              } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Google",
                    tint = GreenPrimary,
                    modifier = Modifier.size(22.dp)
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text("Masuk dengan Akun Google", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
              }
            }

          } else {
            // LOGIN PETUGAS UKS
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFFFF3E0))
                .border(1.dp, Color(0xFFFFCC80), RoundedCornerShape(12.dp))
                .padding(12.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.HealthAndSafety,
                  contentDescription = null,
                  tint = OrangeSecondary,
                  modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    text = "Akses Khusus Petugas UKS / Guru Pembina",
                    style = MaterialTheme.typography.titleSmall.copy(
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFFE65100)
                    )
                  )
                  Text(
                    text = "Gunakan akun UKS sekolah untuk memantau data IMT & gizi siswa seluruh madrasah.",
                    style = MaterialTheme.typography.bodySmall.copy(
                      color = Color(0xFF5D4037),
                      fontSize = 12.sp
                    )
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick demo login button for reviewer convenience - Highlighted
            Button(
              onClick = {
                uksUsername = NutriMindRepository.UKS_DEFAULT_USER
                uksPassword = NutriMindRepository.UKS_DEFAULT_PASS
                uksError = ""
              },
              modifier = Modifier.fillMaxWidth().testTag("uks_autofill_button"),
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFFE0B2),
                contentColor = Color(0xFFE65100)
              )
            ) {
              Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text("⚡ Klik Di Sini: Isi Akun Demo (petugas_uks / uks123)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
              value = uksUsername,
              onValueChange = { uksUsername = it; uksError = "" },
              label = { Text("Username Petugas UKS") },
              leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
              modifier = Modifier.fillMaxWidth().testTag("uks_user_input"),
              singleLine = true,
              shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
              value = uksPassword,
              onValueChange = { uksPassword = it; uksError = "" },
              label = { Text("Password") },
              leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
              trailingIcon = {
                IconButton(onClick = { showPassword = !showPassword }) {
                  Icon(
                    imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = null
                  )
                }
              },
              visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
              modifier = Modifier.fillMaxWidth().testTag("uks_pass_input"),
              singleLine = true,
              shape = RoundedCornerShape(12.dp)
            )

            if (uksError.isNotBlank()) {
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = uksError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
              )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
              onClick = {
                if (uksUsername == NutriMindRepository.UKS_DEFAULT_USER &&
                    uksPassword == NutriMindRepository.UKS_DEFAULT_PASS) {
                  onUksLogin()
                } else {
                  uksError = "Username atau password salah. Gunakan: petugas_uks / uks123"
                }
              },
              modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("uks_login_button"),
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(containerColor = OrangeSecondary)
            ) {
              Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text("Masuk Dashboard UKS", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(30.dp))
      Text(
        text = "NutriMind AI • Versi 1.0 • Standar Permenkes No. 2 Th 2020",
        style = MaterialTheme.typography.labelSmall.copy(
          color = Color(0xFF475569),
          fontWeight = FontWeight.Medium
        ),
        textAlign = TextAlign.Center
      )
    }
  }
}
