package com.korniienko.facedetectiontest.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.Rect
import android.media.ExifInterface
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlin.math.pow
import kotlin.math.sqrt
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceContour
import com.google.mlkit.vision.face.FaceLandmark
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max


@Singleton
class FaceDetectionHelper @Inject constructor() {

    private val detector = FaceDetection.getClient(
        FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
            .build()
    )

    suspend fun detectFaces(bitmap: Bitmap): List<Rect> = withContext(Dispatchers.IO) {
        Log.d("FaceDetection", "Початок розпізнавання обличчя на фото: ${bitmap.width}x${bitmap.height}")

        val image = InputImage.fromBitmap(bitmap, 0)

        return@withContext try {
            val faces = Tasks.await(detector.process(image))
            Log.d("FaceDetection", "Виявлено облич: ${faces.size}")

            if (faces.isNotEmpty()) {
                faces.forEach { face ->
                    Log.d("FaceDetection", "Обличчя знайдено: ${face.boundingBox}")
                }
            }

            faces.map { it.boundingBox }
        } catch (e: Exception) {
            Log.e("FaceDetectionHelper", "Помилка розпізнавання обличчя", e)
            emptyList()
        }
    }



    suspend fun getFaceContours(bitmap: Bitmap): List<PointF>? = withContext(Dispatchers.IO) {
        val image = InputImage.fromBitmap(bitmap, 0)

        return@withContext try {
            val faces = Tasks.await(detector.process(image))

            faces.firstOrNull()?.getContour(FaceContour.FACE)?.points
        } catch (e: Exception) {
            Log.e("FaceDetectionHelper", "Помилка отримання контурів обличчя", e)
            null
        }
    }


    fun compareFaceContours(contour1: List<PointF>, contour2: List<PointF>): Boolean {
        if (contour1.size != contour2.size) return false

        var totalDistance = 0f
        for (i in contour1.indices) {
            val dx = contour1[i].x - contour2[i].x
            val dy = contour1[i].y - contour2[i].y
            totalDistance += sqrt(dx * dx + dy * dy)
        }

        val avgDistance = totalDistance / contour1.size
        Log.d("FaceComparison", "Середня відстань між точками: $avgDistance")

        return avgDistance < 70
    }

    fun scaleBitmap(bitmap: Bitmap, maxSize: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val scaleFactor = maxSize.toFloat() / max(width, height)

        return if (scaleFactor < 1) {
            Bitmap.createScaledBitmap(bitmap, (width * scaleFactor).toInt(), (height * scaleFactor).toInt(), true)
        } else {
            bitmap
        }
    }

    fun getRotationFromExif(uri: Uri, context: Context): Int {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val exif = ExifInterface(inputStream!!)
            when (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> 0
            }
        } catch (e: Exception) {
            Log.e("Exif", "Не вдалося отримати орієнтацію EXIF", e)
            0
        }
    }

    fun rotateBitmap(bitmap: Bitmap, rotation: Int): Bitmap {
        if (rotation == 0) return bitmap
        val matrix = Matrix()
        matrix.postRotate(rotation.toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun base64ToBitmap(base64String: String): Bitmap {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

    fun loadBitmapFromUri(uri: Uri, context: Context): Bitmap? {
        return try {
            Log.d("FaceDetection", "Завантаження фото з URI: $uri")
            val inputStream = context.contentResolver.openInputStream(uri)
            var bitmap = BitmapFactory.decodeStream(inputStream) ?: return null
            Log.d("FaceDetection", "Фото завантажено: ${bitmap.width}x${bitmap.height}")

            val rotation = getRotationFromExif(uri, context)
            Log.d("FaceDetection", "EXIF Орієнтація: $rotation")

            bitmap = rotateBitmap(bitmap, rotation)
            Log.d("FaceDetection", "Після обертання: ${bitmap.width}x${bitmap.height}")

            bitmap = scaleBitmap(bitmap, 1024)
            Log.d("FaceDetection", "Після масштабування: ${bitmap.width}x${bitmap.height}")

            bitmap
        } catch (e: Exception) {
            Log.e("BitmapLoader", "Помилка завантаження зображення", e)
            null
        }
    }

}
