package com.example.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.view.Surface
import androidx.camera.core.Camera
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.util.concurrent.Executor

/**
 * Komponen Kamera Langsung menggunakan CameraX:
 * - CameraSelector (Kamera Belakang / Depan)
 * - ImageCapture (Pemotretan foto makanan beresolusi optimal untuk Gemini AI)
 * - Preview & PreviewView untuk live viewfinder
 */
@Composable
fun CameraCaptureComponent(
  modifier: Modifier = Modifier,
  onImageCaptured: (Bitmap) -> Unit,
  onError: (String) -> Unit,
  onClose: () -> Unit
) {
  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current
  val mainExecutor: Executor = remember { ContextCompat.getMainExecutor(context) }

  var cameraSelector by remember { mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA) }
  var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
  var camera by remember { mutableStateOf<Camera?>(null) }
  var isTorchOn by remember { mutableStateOf(false) }
  var isCapturing by remember { mutableStateOf(false) }
  var previewView by remember { mutableStateOf<PreviewView?>(null) }

  // Bind CameraX lifecycle
  LaunchedEffect(cameraSelector, previewView) {
    val currentPreviewView = previewView ?: return@LaunchedEffect
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

    cameraProviderFuture.addListener({
      try {
        val cameraProvider = cameraProviderFuture.get()

        // 1. Inisialisasi Preview
        val preview = Preview.Builder()
          .build()
          .also {
            it.setSurfaceProvider(currentPreviewView.surfaceProvider)
          }

        // 2. Inisialisasi ImageCapture
        val newImageCapture = ImageCapture.Builder()
          .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
          .setTargetRotation(currentPreviewView.display?.rotation ?: Surface.ROTATION_0)
          .build()

        imageCapture = newImageCapture

        // Unbind semua sebelum bind ulang
        cameraProvider.unbindAll()

        // Periksa ketersediaan kamera fisik untuk mencegah exception di emulator
        val hasBack = cameraProvider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)
        val hasFront = cameraProvider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA)
        val resolvedSelector = when {
          cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA && hasBack -> CameraSelector.DEFAULT_BACK_CAMERA
          cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA && hasFront -> CameraSelector.DEFAULT_FRONT_CAMERA
          hasBack -> CameraSelector.DEFAULT_BACK_CAMERA
          hasFront -> CameraSelector.DEFAULT_FRONT_CAMERA
          else -> null
        }

        if (resolvedSelector == null) {
          onError("Kamera fisik tidak terdeteksi pada perangkat/emulator ini. Silakan gunakan tombol 'Kamera HP' atau 'Galeri Foto'.")
          return@addListener
        }

        // 3. Bind ke Lifecycle dengan CameraSelector terpilih
        camera = cameraProvider.bindToLifecycle(
          lifecycleOwner,
          resolvedSelector,
          preview,
          newImageCapture
        )

        // Reset torch saat kamera berganti
        isTorchOn = false
      } catch (e: Exception) {
        onError("Gagal menginisialisasi kamera: ${e.message}")
      }
    }, mainExecutor)
  }

  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(340.dp)
      .clip(RoundedCornerShape(16.dp))
      .background(Color.Black)
      .testTag("camerax_capture_container")
  ) {
    // Live Viewfinder CameraX
    AndroidView(
      factory = { ctx ->
        PreviewView(ctx).apply {
          implementationMode = PreviewView.ImplementationMode.COMPATIBLE
          scaleType = PreviewView.ScaleType.FILL_CENTER
          previewView = this
        }
      },
      modifier = Modifier.fillMaxSize()
    )

    // Panduan Framing Makanan (Overlay Pembidik Piring Makan)
    Box(
      modifier = Modifier
        .size(200.dp)
        .align(Alignment.Center)
        .border(2.dp, Color.White.copy(alpha = 0.6f), CircleShape)
    )

    // Top Controls Bar (Tutup, Flip Lens, Flash)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp)
        .align(Alignment.TopCenter),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Tombol Tutup Kamera
      IconButton(
        onClick = onClose,
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(Color.Black.copy(alpha = 0.5f))
          .testTag("camerax_close_button")
      ) {
        Icon(
          imageVector = Icons.Default.Close,
          contentDescription = "Tutup Kamera",
          tint = Color.White,
          modifier = Modifier.size(20.dp)
        )
      }

      // Indikator Posisi Makanan
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(12.dp))
          .background(Color.Black.copy(alpha = 0.6f))
          .padding(horizontal = 10.dp, vertical = 4.dp)
      ) {
        Text(
          text = "Arahkan ke Piring / Makanan",
          style = MaterialTheme.typography.labelSmall.copy(
            color = Color.White,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp
          )
        )
      }

      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        // Toggle Flash / Torch
        IconButton(
          onClick = {
            val cam = camera ?: return@IconButton
            if (cam.cameraInfo.hasFlashUnit()) {
              val nextState = !isTorchOn
              cam.cameraControl.enableTorch(nextState)
              isTorchOn = nextState
            }
          },
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.5f))
            .testTag("camerax_flash_toggle")
        ) {
          Icon(
            imageVector = if (isTorchOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
            contentDescription = "Lampu Kilat",
            tint = if (isTorchOn) Color(0xFFFFD54F) else Color.White,
            modifier = Modifier.size(18.dp)
          )
        }

        // Switch CameraSelector (Back <-> Front)
        IconButton(
          onClick = {
            cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
              CameraSelector.DEFAULT_FRONT_CAMERA
            } else {
              CameraSelector.DEFAULT_BACK_CAMERA
            }
          },
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.5f))
            .testTag("camerax_switch_camera_button")
        ) {
          Icon(
            imageVector = Icons.Default.Cameraswitch,
            contentDescription = "Ganti Lensa Kamera",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }

    // Bottom Controls Bar (Shutter Button)
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 16.dp)
        .align(Alignment.BottomCenter),
      contentAlignment = Alignment.Center
    ) {
      if (isCapturing) {
        Box(
          modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.8f)),
          contentAlignment = Alignment.Center
        ) {
          CircularProgressIndicator(
            color = Color(0xFF2E7D32),
            modifier = Modifier.size(32.dp),
            strokeWidth = 3.dp
          )
        }
      } else {
        // Shutter Button
        Box(
          modifier = Modifier
            .size(68.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.35f))
            .padding(5.dp)
            .clip(CircleShape)
            .background(Color.White)
            .clickable {
              val capture = imageCapture
              if (capture != null) {
                isCapturing = true
                capture.takePicture(
                  mainExecutor,
                  object : ImageCapture.OnImageCapturedCallback() {
                    override fun onCaptureSuccess(imageProxy: ImageProxy) {
                      try {
                        val rotation = imageProxy.imageInfo.rotationDegrees
                        val rawBitmap = try {
                          imageProxy.toBitmap()
                        } catch (e: Exception) {
                          val buffer = imageProxy.planes[0].buffer
                          val bytes = ByteArray(buffer.remaining())
                          buffer.get(bytes)
                          android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        }

                        val finalBitmap = if (rotation != 0 && rawBitmap != null) {
                          val matrix = Matrix().apply { postRotate(rotation.toFloat()) }
                          Bitmap.createBitmap(rawBitmap, 0, 0, rawBitmap.width, rawBitmap.height, matrix, true)
                        } else {
                          rawBitmap
                        }

                        isCapturing = false
                        if (finalBitmap != null) {
                          onImageCaptured(finalBitmap)
                        } else {
                          onError("Gagal mengolah foto kamera.")
                        }
                      } catch (e: Exception) {
                        isCapturing = false
                        onError("Gagal mengolah foto: ${e.message}")
                      } finally {
                        imageProxy.close()
                      }
                    }

                    override fun onError(exception: ImageCaptureException) {
                      isCapturing = false
                      onError("Gagal mengambil foto: ${exception.message}")
                    }
                  }
                )
              } else {
                onError("Kamera belum siap, silakan coba beberapa saat lagi.")
              }
            }
            .testTag("camerax_shutter_button"),
          contentAlignment = Alignment.Center
        ) {
          Box(
            modifier = Modifier
              .size(50.dp)
              .clip(CircleShape)
              .border(2.dp, Color(0xFF2E7D32), CircleShape)
          )
        }
      }
    }
  }
}
