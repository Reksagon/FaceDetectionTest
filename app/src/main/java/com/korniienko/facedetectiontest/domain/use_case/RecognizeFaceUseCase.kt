package com.korniienko.facedetectiontest.domain.use_case

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PointF
import android.graphics.Rect
import android.net.Uri
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.vision.face.Landmark
import com.google.mlkit.vision.common.InputImage
import com.korniienko.facedetectiontest.domain.model.PersonDomain
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.korniienko.facedetectiontest.app.App
import com.korniienko.facedetectiontest.domain.repository.PersonRepository
import com.korniienko.facedetectiontest.utils.FaceDetectionHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

import kotlin.math.pow
import kotlin.math.sqrt

class RecognizeFaceUseCase @Inject constructor(
    private val personRepository: PersonRepository,
    private val faceDetectionHelper: FaceDetectionHelper
) {
    suspend fun execute(imageUri: Uri, context: Context): List<PersonDomain> = withContext(Dispatchers.IO) {
        val bitmap = loadBitmapFromUri(imageUri, context) ?: return@withContext emptyList()
        val detectedFaces = faceDetectionHelper.detectFaces(bitmap) // Тепер отримуємо всі знайдені обличчя

        if (detectedFaces.isEmpty()) return@withContext emptyList()

        val allPersons = personRepository.getAllPersons()
        return@withContext findMatchingPersons(detectedFaces, allPersons, context)
    }

    fun loadBitmapFromUri(uri: Uri, context: Context): Bitmap? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun findMatchingPersons(detectedFaces: List<Rect>, persons: List<PersonDomain>, context: Context): List<PersonDomain> = withContext(Dispatchers.IO) {
        val matchedPersons = mutableListOf<PersonDomain>()

        for (person in persons) {
            val personBitmap = loadBitmapFromUri(Uri.parse(person.faceImagePath), context) ?: continue
            val storedFaces = faceDetectionHelper.detectFaces(personBitmap)

            if (storedFaces.any { storedFace -> detectedFaces.any { it.intersect(storedFace) } }) {
                matchedPersons.add(person)
            }
        }
        return@withContext matchedPersons
    }
}
