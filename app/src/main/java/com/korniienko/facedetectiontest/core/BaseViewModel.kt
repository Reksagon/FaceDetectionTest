package com.korniienko.facedetectiontest.core

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        handleError(throwable)
    }

    protected open fun handleError(error: Throwable) {
       Log.e("errorHandler", error.message.toString())
    }

    protected fun launch(block: suspend () -> Unit) {
        viewModelScope.launch(errorHandler) {
            block()
        }
    }
}