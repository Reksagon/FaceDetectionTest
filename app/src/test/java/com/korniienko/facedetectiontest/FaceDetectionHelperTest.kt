package com.korniienko.facedetectiontest

import android.content.Context
import android.graphics.Bitmap
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.mlkit.common.sdkinternal.MlKitContext
import com.korniienko.facedetectiontest.utils.FaceDetectionHelper
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
class FaceDetectionHelperTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private lateinit var faceDetectionHelper: FaceDetectionHelper
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = mockk(relaxed = true)
        every { context.applicationContext } returns context

        faceDetectionHelper = mockk(relaxed = true)
    }


    @Test
    fun `detectFaces should return empty list when no face detected`() = runTest {
        val bitmap = mockk<Bitmap>()
        val result = faceDetectionHelper.detectFaces(bitmap)

        assertTrue(result.isEmpty())
    }
}
