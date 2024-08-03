import android.Manifest
import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.drivertracking.R
import com.example.drivertracking.features.camera.viewmodel.CameraViewModel
import com.example.drivertracking.model.entities.EulerRecord
import com.example.drivertracking.model.entities.EyeOpennessRecord
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.delay
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraPreview(viewModel: CameraViewModel = viewModel()) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
    var useFrontCamera by remember { mutableStateOf(true) }
    var frameRate by remember { mutableStateOf(0) }
    var imageSize by remember { mutableStateOf("") }
    var leftEyeOpenProbability by remember { mutableStateOf<Float?>(null) }
    var rightEyeOpenProbability by remember { mutableStateOf<Float?>(null) }
    var headEulerAngleX by remember { mutableStateOf<Float?>(null) }
    var medianLeftEye by remember { mutableStateOf<Float?>(null) }
    var medianRightEye by remember { mutableStateOf<Float?>(null) }
    var medianHeadEulerAngleX by remember { mutableStateOf<Float?>(null) }
    var recordCount by remember { mutableStateOf<Int?>(null) }
    var calculationTime by remember { mutableStateOf<Long?>(null) }
    var allValuesNotNull =
        leftEyeOpenProbability != null && rightEyeOpenProbability != null && headEulerAngleX != null

    val toastLink = {
        (context as Activity).runOnUiThread {
            Toast.makeText(context, "Uwaga! Zdarzenie zarejestrowane", Toast.LENGTH_LONG).show()

            // Play notification sound
            val mediaPlayer = MediaPlayer.create(context, R.raw.notification_sound)
            mediaPlayer.start()
            mediaPlayer.setOnCompletionListener { it.release() }
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            viewModel.deleteOldRecords()
            delay(1000 * 60 * 10) // Delete old records every 10 minutes
        }
    }
    LaunchedEffect(Unit) {
        while (true) {
            viewModel.calculateMedianForChunk()
            viewModel.checkMedianForNotification(toastLink)
            delay(1000 * 30) // every 30 seconds
        }
    }


    // Save to database when button is clicked
    val onSaveToDatabase = {
        if (leftEyeOpenProbability != null && rightEyeOpenProbability != null && headEulerAngleX != null) {
            viewModel.insert(
                EyeOpennessRecord(
                    timestamp = System.currentTimeMillis(),
                    leftEyeOpenProbability = leftEyeOpenProbability!!,
                    rightEyeOpenProbability = rightEyeOpenProbability!!,
                ),
                EulerRecord(
                    timestamp = System.currentTimeMillis(),
                    headEulerAngleX = headEulerAngleX ?: 0f,
                )
            )
        }
    }

    // Calculate median from records in the last 5 minutes
    val calculateMedian = {
        viewModel.calculateMedianForChunk()
    }

    // Observe median values from ViewModel
    viewModel.medianLeftEyeLiveData.observe(lifecycleOwner) { median ->
        medianLeftEye = median
    }

    viewModel.medianRightEyeLiveData.observe(lifecycleOwner) { median ->
        medianRightEye = median
    }

    viewModel.medianEulerAngleXLiveData.observe(lifecycleOwner) { median ->
        medianHeadEulerAngleX = median
    }

    viewModel.recordCountLiveData.observe(lifecycleOwner) { count ->
        recordCount = count
    }

    viewModel.calculationTimeLiveData.observe(lifecycleOwner) { time ->
        calculationTime = time
    }

    // Handle Camera Preview
    CameraPreviewContent(
        context = context,
        lifecycleOwner = lifecycleOwner,
        cameraPermissionState = cameraPermissionState,
        useFrontCamera = useFrontCamera,
        frameRate = frameRate,
        onFrameRateUpdated = { frameRate = it },
        imageSize = imageSize,
        onImageSizeUpdated = { imageSize = it },
        leftEyeOpenProbability = leftEyeOpenProbability,
        onLeftEyeOpenProbabilityUpdated = { leftEyeOpenProbability = it },
        rightEyeOpenProbability = rightEyeOpenProbability,
        onRightEyeOpenProbabilityUpdated = { rightEyeOpenProbability = it },
        onHeadEulerAngleXUpdated = { headEulerAngleX = it },
        onSaveToDatabase = onSaveToDatabase,
        calculateMedian = calculateMedian  // Pass the calculateMedian function
    )

    // Request Camera Permission if not granted
    LaunchedEffect(Unit) {
        if (cameraPermissionState.status != PermissionStatus.Granted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    // Switch Button and other UI elements
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopEnd,
    ) {
        Icon(
            Icons.Filled.Person,
            contentDescription = "Person",
            modifier = Modifier
                .size(64.dp)
                .padding(all = 10.dp)
                .border(
                    width = 2.dp,
                    color = Color.White,
                    shape = RoundedCornerShape(5.dp)
                )
                .padding(all = 2.dp)
                .background(color = Color.White),
            tint = if (allValuesNotNull) Color.Green else Color.Red
        )
    }
}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraPreviewContent(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    cameraPermissionState: PermissionState,
    useFrontCamera: Boolean,
    frameRate: Int,
    onFrameRateUpdated: (Int) -> Unit,
    imageSize: String,
    onImageSizeUpdated: (String) -> Unit,
    leftEyeOpenProbability: Float?,
    onLeftEyeOpenProbabilityUpdated: (Float?) -> Unit,
    rightEyeOpenProbability: Float?,
    onRightEyeOpenProbabilityUpdated: (Float?) -> Unit,
    onHeadEulerAngleXUpdated: (Float?) -> Unit,
    onSaveToDatabase: () -> Unit,
    calculateMedian: () -> Unit
) {
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val previewView = remember { PreviewView(context) }
    var cameraExecutor: ExecutorService? by remember { mutableStateOf(null) }

    DisposableEffect(Unit) {
        cameraExecutor = Executors.newSingleThreadExecutor()
        onDispose { cameraExecutor?.shutdown() }
    }

    if (cameraPermissionState.status == PermissionStatus.Granted) {
        LaunchedEffect(cameraProviderFuture, useFrontCamera) {
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val cameraSelector = if (useFrontCamera) {
                CameraSelector.DEFAULT_FRONT_CAMERA
            } else {
                CameraSelector.DEFAULT_BACK_CAMERA
            }

            val faceDetectorOptions = FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .build()

            val faceDetector = FaceDetection.getClient(faceDetectorOptions)

            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor!!) { imageProxy ->
                        processImageProxy(
                            faceDetector,
                            imageProxy,
                            onFrameRateUpdated,
                            onImageSizeUpdated,
                            onLeftEyeOpenProbabilityUpdated,
                            onRightEyeOpenProbabilityUpdated,
                            onHeadEulerAngleXUpdated,
                            onSaveToDatabase,
                        )
                    }
                }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalyzer
                )
            } catch (exc: Exception) {
                Log.e("CameraPreview", "Use case binding failed", exc)
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView({ previewView }, modifier = Modifier.fillMaxSize())
        }
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Camera permission is required to use this feature")
        }
    }
}

private fun processImageProxy(
    faceDetector: com.google.mlkit.vision.face.FaceDetector,
    imageProxy: ImageProxy,
    onFrameRateUpdated: (Int) -> Unit,
    onImageSizeUpdated: (String) -> Unit,
    onLeftEyeOpenProbabilityUpdated: (Float?) -> Unit,
    onRightEyeOpenProbabilityUpdated: (Float?) -> Unit,
    onHeadEulerAngleXUpdated: (Float?) -> Unit,
    onSaveToDatabase: () -> Unit
) {
    @androidx.camera.core.ExperimentalGetImage
    val mediaImage = imageProxy.image
    if (mediaImage != null) {
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        faceDetector.process(image)
            .addOnSuccessListener { faces ->
                if (faces.isNotEmpty()) {
                    val face = faces[0]
                    onHeadEulerAngleXUpdated(face.headEulerAngleX)
                    onLeftEyeOpenProbabilityUpdated(face.leftEyeOpenProbability)
                    onRightEyeOpenProbabilityUpdated(face.rightEyeOpenProbability)
                    onSaveToDatabase()
                } else {
                    onHeadEulerAngleXUpdated(null)
                    onLeftEyeOpenProbabilityUpdated(null)
                    onRightEyeOpenProbabilityUpdated(null)
                }

                val imageSize = "${mediaImage.width}x${mediaImage.height}"
                onImageSizeUpdated(imageSize)

                updateFrameRate(onFrameRateUpdated)

                imageProxy.close()
            }
            .addOnFailureListener { e ->
                Log.e("CameraPreview", "Face detection failed", e)
                imageProxy.close()
            }
    } else {
        imageProxy.close()
    }
}

private var lastFrameTime = 0L
private var frameCount = 0

private fun updateFrameRate(onFrameRateUpdated: (Int) -> Unit) {
    val currentTime = System.currentTimeMillis()
    frameCount++

    if (currentTime - lastFrameTime >= 1000) {
        val frameRate = frameCount
        frameCount = 0
        lastFrameTime = currentTime
        onFrameRateUpdated(frameRate)
    }
}
