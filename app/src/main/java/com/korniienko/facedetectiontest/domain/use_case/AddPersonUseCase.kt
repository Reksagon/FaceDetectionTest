package com.korniienko.facedetectiontest.domain.use_case

import com.korniienko.facedetectiontest.domain.model.PersonDomain
import com.korniienko.facedetectiontest.domain.repository.PersonRepository
import javax.inject.Inject

class AddPersonUseCase @Inject constructor(
    private val repository: PersonRepository
) {
    suspend fun execute(person: PersonDomain) {
        repository.insertPerson(person)
    }
}
