package com.example.drivertracking

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.drivertracking.ui.theme.DriverTrackingTheme


class MainActivity :  ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DriverTrackingTheme {
                DriverApp()
            }
        }
    }
}

//private fun runFaceContourDetection() {
//    val image = InputImage.fromBitmap(mSelectedImage, 0)
//    val options = FaceDetectorOptions.Builder()
//        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
//        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
//        .build()
//    mFaceButton.setEnabled(false)
//    val detector: FaceDetector = FaceDetection.getClient(options)
//    detector.process(image)
//        .addOnSuccessListener(
//            OnSuccessListener<List<Any?>?> { faces ->
//                mFaceButton.setEnabled(true)
//                processFaceContourDetectionResult(faces)
//            })
//        .addOnFailureListener(
//            OnFailureListener { e -> // Task failed with an exception
//                mFaceButton.setEnabled(true)
//                e.printStackTrace()
//            })
//}