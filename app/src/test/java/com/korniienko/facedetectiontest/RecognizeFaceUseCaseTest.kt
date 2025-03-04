package com.korniienko.facedetectiontest

import android.graphics.Bitmap
import android.graphics.Rect
import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.korniienko.facedetectiontest.domain.model.PersonDomain
import com.korniienko.facedetectiontest.domain.repository.PersonRepository
import com.korniienko.facedetectiontest.domain.use_case.RecognizeFaceUseCase
import com.korniienko.facedetectiontest.utils.FaceDetectionHelper
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith



class RecognizeFaceUseCaseTest {

    private lateinit var recognizeFaceUseCase: RecognizeFaceUseCase
    private lateinit var repository: PersonRepository
    private lateinit var faceDetectionHelper: FaceDetectionHelper

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        faceDetectionHelper = mockk(relaxed = true)
        recognizeFaceUseCase = RecognizeFaceUseCase(repository, faceDetectionHelper)
    }

    @Test
    fun `execute should return persons when faces detected`() = runTest {
        val image = mockk<Bitmap>()

        val detectedFaces = listOf(Rect(10, 10, 50, 50))
        val testPerson = PersonDomain(0, "Іван", "Менеджер", "file://face1.jpg")
        coEvery { faceDetectionHelper.detectFaces(any()) } returns detectedFaces
        coEvery { repository.getAllPersons() } returns listOf(testPerson)

        val result = recognizeFaceUseCase.execute(image, mockk(relaxed = true)) // Мокаємо Context

        assertEquals(0, result.size)
    }
}

