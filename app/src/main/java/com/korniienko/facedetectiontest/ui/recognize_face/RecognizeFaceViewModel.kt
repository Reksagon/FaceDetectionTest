package com.korniienko.facedetectiontest.ui.recognize_face

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.korniienko.facedetectiontest.core.BaseViewModel
import com.korniienko.facedetectiontest.domain.use_case.RecognizeFaceUseCase
import com.korniienko.facedetectiontest.utils.NotificationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.sqrt
import kotlin.random.Random


@HiltViewModel
class RecognizeFaceViewModel @Inject constructor(
    private val recognizeFaceUseCase: RecognizeFaceUseCase,
    private val notificationHelper: NotificationHelper
) : ViewModel() {

    private val _recognizedPersons = MutableLiveData<String>()
    val recognizedPersons: LiveData<String> = _recognizedPersons

    fun recognizeFace(bitmap: Bitmap, context: Context) {
        viewModelScope.launch {
            try {
                val results = recognizeFaceUseCase.execute(bitmap, context)
                if (results.isNotEmpty()) {
                    val personsText = results.joinToString("\n") { "${it.name}, ${it.position}" }
                    _recognizedPersons.postValue("Знайдено:\n$personsText")
                } else {
                    _recognizedPersons.postValue("Обличчя не розпізнано")
                    notificationHelper.showNotification(
                        context,
                        "Ідентифікація не вдалася",
                        "Не вдалося розпізнати обличчя. Будь ласка, спробуйте ще раз."
                    )
                }
            } catch (e: Exception) {
                _recognizedPersons.postValue("Помилка: ${e.message}")
                notificationHelper.showNotification(
                    context,
                    "Помилка розпізнавання",
                    "Сталася помилка під час розпізнавання: ${e.message}"
                )
            }
        }
    }


}
