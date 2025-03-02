package com.korniienko.facedetectiontest.domain.repository

import com.korniienko.facedetectiontest.domain.model.PersonDomain

interface PersonRepository {
    suspend fun insertPerson(person: PersonDomain)
    suspend fun getAllPersons(): List<PersonDomain>
}