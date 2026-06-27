package com.example.video

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

private const val TAG = "CusbuyCamera"

/**
 * Écran d'enregistrement vidéo avec CameraX.
 * Remplace le simulateur de caméra dans le PublishWizard.
 *
 * @param onVideoRecorded Callback quand la vidéo est enregistrée (retourne le chemin)
 * @param onDismiss Fermer l'écran
 */
@Composable
fun CusbuyCamera(
    onVideoRecorded: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var isRecording by remember { mutableStateOf(false) }
    var recordingDuration by remember { mutableStateOf(0) }
    var videoCapture by remember { mutableStateOf<VideoCapture<Recorder>?>(null) }
    var activeRecording by remember { mutableStateOf<Recording?>(null) }
    var cameraFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
    var flashEnabled by remember { mutableStateOf(false) }
    var camera by remember { mutableStateOf<Camera?>(null) }

    val previewView = remember { PreviewView(context) }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    // Timer d'enregistrement
    LaunchedEffect(isRecording) {
        if (isRecording) {
            recordingDuration = 0
            while (isRecording) {
                delay(1000)
                recordingDuration++
                if (recordingDuration >= 180) { // Max 3 minutes
                    // Auto-stop à 3min
                    activeRecording?.stop()
                    isRecording = false
                }
            }
        }
    }

    // Setup CameraX
    LaunchedEffect(cameraFacing) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider
            }

            val recorder = Recorder.Builder()
                .setQualitySelector(QualitySelector.from(Quality.HD))
                .build()

            videoCapture = VideoCapture.withOutput(recorder)

            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(cameraFacing)
                .build()

            try {
                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    videoCapture
                )
            } catch (e: Exception) {
                Log.e(TAG, "Erreur CameraX: ${e.message}")
            }
        }, ContextCompat.getMainExecutor(context))
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {

        // Prévisualisation caméra
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )

        // Overlay supérieur
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(top = 32.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Bouton fermer
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                Icon(Icons.Default.Close, "Fermer", tint = Color.White)
            }

            // Timer d'enregistrement
            if (isRecording) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Red.copy(alpha = 0.9f))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = String.format(
                            "%02d:%02d",
                            recordingDuration / 60,
                            recordingDuration % 60
                        ),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            // Flash
            IconButton(
                onClick = {
                    flashEnabled = !flashEnabled
                    camera?.cameraControl?.enableTorch(flashEnabled)
                },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                Icon(
                    imageVector = if (flashEnabled) Icons.Default.FlashOn else Icons.Default.FlashOff,
                    contentDescription = "Flash",
                    tint = if (flashEnabled) Color.Yellow else Color.White
                )
            }
        }

        // Contrôles bas
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Barre de progression (durée max 3min)
            LinearProgressIndicator(
                progress = { recordingDuration / 180f },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = Color.Red,
                trackColor = Color.White.copy(alpha = 0.3f)
            )

            Text(
                text = "Max 3 minutes",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Retourner caméra
                IconButton(
                    onClick = {
                        cameraFacing = if (cameraFacing == CameraSelector.LENS_FACING_BACK)
                            CameraSelector.LENS_FACING_FRONT
                        else CameraSelector.LENS_FACING_BACK
                    },
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f))
                ) {
                    Icon(Icons.Default.FlipCameraAndroid, "Retourner", tint = Color.White)
                }

                // Bouton enregistrement
                IconButton(
                    onClick = {
                        if (!isRecording) {
                            startRecording(
                                context = context,
                                videoCapture = videoCapture,
                                executor = cameraExecutor,
                                onRecordingStarted = { recording ->
                                    activeRecording = recording
                                    isRecording = true
                                },
                                onVideoSaved = { uri ->
                                    isRecording = false
                                    onVideoRecorded(uri)
                                }
                            )
                        } else {
                            activeRecording?.stop()
                            isRecording = false
                        }
                    },
                    modifier = Modifier.size(80.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(if (isRecording) Color.Red else Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isRecording) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color.White)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(Color.Red)
                            )
                        }
                    }
                }

                // Espace symétrique
                Box(modifier = Modifier.size(50.dp))
            }
        }
    }
}

private fun startRecording(
    context: Context,
    videoCapture: VideoCapture<Recorder>?,
    executor: java.util.concurrent.Executor,
    onRecordingStarted: (Recording) -> Unit,
    onVideoSaved: (String) -> Unit
) {
    val capture = videoCapture ?: return

    val name = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.getDefault())
        .format(System.currentTimeMillis())

    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, "CUSBUY_$name")
        put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/Cusbuy")
        }
    }

    val mediaStoreOutputOptions = MediaStoreOutputOptions.Builder(
        context.contentResolver,
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    ).setContentValues(contentValues).build()

    val recording = capture.output
        .prepareRecording(context, mediaStoreOutputOptions)
        .apply { withAudioEnabled() }
        .start(executor) { event ->
            when (event) {
                is VideoRecordEvent.Start -> {
                    Log.d(TAG, "Enregistrement démarré")
                }
                is VideoRecordEvent.Finalize -> {
                    if (!event.hasError()) {
                        val uri = event.outputResults.outputUri.toString()
                        Log.d(TAG, "Vidéo sauvegardée: $uri")
                        onVideoSaved(uri)
                    } else {
                        Log.e(TAG, "Erreur enregistrement: ${event.error}")
                    }
                }
            }
        }

    onRecordingStarted(recording)
}
