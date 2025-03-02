package com.korniienko.facedetectiontest.utils

import android.graphics.Bitmap
import android.graphics.PointF
import android.graphics.Rect
import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlin.math.pow
import kotlin.math.sqrt
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceLandmark
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class FaceDetectionHelper @Inject constructor() {


    private val detector = FaceDetection.getClient(
        FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
            .build()
    )

    suspend fun detectFaces(bitmap: Bitmap): List<Rect> = withContext(Dispatchers.IO) {
        val image = InputImage.fromBitmap(bitmap, 0)

        return@withContext try {
            val faces = Tasks.await(detector.process(image))
            faces.map { it.boundingBox }
        } catch (e: Exception) {
            Log.e("FaceDetectionHelper", "Помилка розпізнавання обличчя", e)
            emptyList()
        }
    }



}
