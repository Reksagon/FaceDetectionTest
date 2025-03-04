package com.korniienko.facedetectiontest.domain.use_case

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PointF
import android.net.Uri
import com.korniienko.facedetectiontest.domain.model.PersonDomain
import com.korniienko.facedetectiontest.domain.repository.PersonRepository
import com.korniienko.facedetectiontest.utils.FaceDetectionHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RecognizeFaceUseCase @Inject constructor(
    private val personRepository: PersonRepository,
    private val faceDetectionHelper: FaceDetectionHelper
) {
    suspend fun execute(bitmap: Bitmap, context: Context): List<PersonDomain> = withContext(Dispatchers.IO) {
        val detectedContour = faceDetectionHelper.getFaceContours(bitmap) ?: return@withContext emptyList()

        val allPersons = personRepository.getAllPersons()
        return@withContext findMatchingPersons(detectedContour, allPersons, context)
    }

    private suspend fun findMatchingPersons(
        detectedContour: List<PointF>,
        persons: List<PersonDomain>,
        context: Context
    ): List<PersonDomain> = withContext(Dispatchers.IO) {
        val matchedPersons = mutableListOf<PersonDomain>()

        for (person in persons) {
            val personBitmap = faceDetectionHelper.base64ToBitmap(person.faceImageBase64)
            val storedContour = faceDetectionHelper.getFaceContours(personBitmap) ?: continue

            if (faceDetectionHelper.compareFaceContours(detectedContour, storedContour)) {
                matchedPersons.add(person)
            }
        }
        return@withContext matchedPersons
    }


}
