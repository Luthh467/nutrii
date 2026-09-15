package com.example.data.firebase

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.example.data.model.StudentProfile
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

object FirebaseAuthService {

  private const val TAG = "FirebaseAuthService"

  fun getAuth(context: Context? = null): FirebaseAuth? {
    return try {
      if (context != null && FirebaseApp.getApps(context).isEmpty()) {
        Log.w(TAG, "FirebaseApp is not initialized yet.")
        null
      } else {
        FirebaseAuth.getInstance()
      }
    } catch (e: Exception) {
      Log.e(TAG, "Failed to get FirebaseAuth: ${e.message}", e)
      null
    }
  }

  fun getCurrentUser(context: Context? = null): FirebaseUser? {
    return getAuth(context)?.currentUser
  }

  fun getDefaultWebClientId(context: Context): String? {
    return try {
      val resId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
      if (resId != 0) context.getString(resId) else null
    } catch (e: Exception) {
      null
    }
  }

  suspend fun signInWithGoogle(
    context: Context,
    onSuccess: (StudentProfile) -> Unit,
    onError: (String) -> Unit
  ) {
    val credentialManager = CredentialManager.create(context)
    val serverClientId = getDefaultWebClientId(context)

    if (serverClientId.isNullOrBlank()) {
      Log.w(TAG, "default_web_client_id not found in resources. Attempting Firebase anonymous fallback.")
      signInAnonymously(context, onSuccess, onError)
      return
    }

    try {
      val googleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(serverClientId)
        .setAutoSelectEnabled(false)
        .build()

      val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

      val result = credentialManager.getCredential(request = request, context = context)
      val credential = result.credential

      if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
        val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
        val firebaseAuth = getAuth(context)
        if (firebaseAuth != null) {
          val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
          val authResult = firebaseAuth.signInWithCredential(authCredential).await()
          val user = authResult.user

          val displayName = user?.displayName ?: "Siswa Google"
          val email = user?.email ?: ""
          val profile = StudentProfile(
            id = user?.uid ?: "std-${System.currentTimeMillis()}",
            name = displayName,
            nisn = "G-${(user?.uid?.take(8) ?: "00000000").hashCode().coerceAtLeast(10000000)}",
            className = "Kelas X",
            gender = "L",
            age = 16
          )
          NutriMindFirestoreService.saveStudentProfile(profile, context)
          onSuccess(profile)
        } else {
          onError("Firebase Auth belum terhubung. Pastikan konfigurasi Firebase tersedia.")
        }
      } else {
        onError("Tipe kredensial Google tidak sesuai.")
      }
    } catch (e: GetCredentialCancellationException) {
      Log.d(TAG, "Pengguna membatalkan login Google.")
    } catch (e: GetCredentialException) {
      Log.e(TAG, "GetCredentialException: ${e.message}", e)
      // Fallback seamlessly so users can test on emulators
      signInAnonymously(context, onSuccess, onError)
    } catch (e: Exception) {
      Log.e(TAG, "Google Sign-In Error: ${e.message}", e)
      signInAnonymously(context, onSuccess, onError)
    }
  }

  suspend fun signInAnonymously(
    context: Context,
    onSuccess: (StudentProfile) -> Unit,
    onError: (String) -> Unit
  ) {
    try {
      val auth = getAuth(context)
      if (auth != null) {
        val authResult = auth.signInAnonymously().await()
        val user = authResult.user
        val profile = StudentProfile(
          id = user?.uid ?: "std-anon-${System.currentTimeMillis()}",
          name = "Siswa (Akun Cloud)",
          nisn = "0068" + (100000..999999).random(),
          className = "XI MIPA 2",
          gender = "L",
          age = 16
        )
        NutriMindFirestoreService.saveStudentProfile(profile, context)
        onSuccess(profile)
      } else {
        // Fallback local profile
        val profile = StudentProfile(
          id = "std-local-${System.currentTimeMillis()}",
          name = "Siswa Mandiri",
          nisn = "0068" + (100000..999999).random(),
          className = "XI MIPA 2",
          gender = "L",
          age = 16
        )
        onSuccess(profile)
      }
    } catch (e: Exception) {
      Log.e(TAG, "Anonymous sign-in error: ${e.message}", e)
      // Create local profile so student can always proceed
      val fallbackProfile = StudentProfile(
        id = "std-fallback-${System.currentTimeMillis()}",
        name = "Siswa Uji",
        nisn = "0068123456",
        className = "XI MIPA 2",
        gender = "L",
        age = 16
      )
      onSuccess(fallbackProfile)
    }
  }

  fun signOut(context: Context? = null) {
    try {
      getAuth(context)?.signOut()
    } catch (e: Exception) {
      Log.e(TAG, "Sign out error: ${e.message}")
    }
  }
}
