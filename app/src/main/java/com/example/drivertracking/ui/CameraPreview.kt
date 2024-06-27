import android.Manifest
import android.content.Context
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraPreview() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
    var useFrontCamera by remember { mutableStateOf(true) }
    var frameRate by remember { mutableStateOf(0) }
    var imageSize by remember { mutableStateOf("") }
    var leftEyeOpenProbability by remember { mutableStateOf<Float?>(null) }
    var rightEyeOpenProbability by remember { mutableStateOf<Float?>(null) }
    var drawMesh by remember { mutableStateOf(true) }

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
        drawMesh = drawMesh,
        onDrawMeshUpdated = { drawMesh = it }
    )

    LaunchedEffect(Unit) {
        if (cameraPermissionState.status != PermissionStatus.Granted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = { useFrontCamera = !useFrontCamera }) {
                Text(text = "Switch Camera")
            }
            Button(onClick = { drawMesh = !drawMesh }) {
                Text(text = if (drawMesh) "Hide Mesh" else "Show Mesh")
            }
            Text(text = "Frame Rate: $frameRate fps")
            Text(text = "Image Size: $imageSize")
            leftEyeOpenProbability?.let { probability ->
                Text(text = "Left Eye Open Probability: ${String.format("%.2f", probability * 100)}%")
            }
            rightEyeOpenProbability?.let { probability ->
                Text(text = "Right Eye Open Probability: ${String.format("%.2f", probability * 100)}%")
            }
        }
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
    drawMesh: Boolean,
    onDrawMeshUpdated: (Boolean) -> Unit
) {
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val previewView = remember { PreviewView(context) }
    val faceGridView = remember { FaceGridView(context) }
    faceGridView.setDrawMesh(drawMesh)
    var cameraExecutor by remember { mutableStateOf<ExecutorService?>(null) }

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
                            faceGridView,
                            onFrameRateUpdated,
                            onImageSizeUpdated,
                            onLeftEyeOpenProbabilityUpdated,
                            onRightEyeOpenProbabilityUpdated
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
            AndroidView({ faceGridView }, modifier = Modifier.fillMaxSize())
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
    faceGridView: FaceGridView,
    onFrameRateUpdated: (Int) -> Unit,
    onImageSizeUpdated: (String) -> Unit,
    onLeftEyeOpenProbabilityUpdated: (Float?) -> Unit,
    onRightEyeOpenProbabilityUpdated: (Float?) -> Unit
) {
    @androidx.camera.core.ExperimentalGetImage
    val mediaImage = imageProxy.image
    if (mediaImage != null) {
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        faceDetector.process(image)
            .addOnSuccessListener { faces ->
                if (faces.isNotEmpty()) {
                    val face = faces[0]
                    faceGridView.setFace(face)

                    onLeftEyeOpenProbabilityUpdated(face.leftEyeOpenProbability)
                    onRightEyeOpenProbabilityUpdated(face.rightEyeOpenProbability)
                } else {
                    faceGridView.setFace(null)
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
