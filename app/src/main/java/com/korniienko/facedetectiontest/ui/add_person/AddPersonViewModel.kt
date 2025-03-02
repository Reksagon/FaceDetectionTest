package com.korniienko.facedetectiontest.ui.add_person


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.korniienko.facedetectiontest.core.BaseViewModel
import com.korniienko.facedetectiontest.domain.model.PersonDomain
import com.korniienko.facedetectiontest.domain.use_case.AddPersonUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AddPersonViewModel @Inject constructor(
    private val addPersonUseCase: AddPersonUseCase
) : BaseViewModel() {

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _successMessage = MutableLiveData<String>()
    val successMessage: LiveData<String> = _successMessage

    fun addPerson(person: PersonDomain) {
        viewModelScope.launch {
            try {
                addPersonUseCase.execute(person)
                _successMessage.postValue("Людину успішно додано!")
            } catch (e: Exception) {
                _errorMessage.postValue("Помилка збереження: ${e.message}")
            }
        }
    }
}
