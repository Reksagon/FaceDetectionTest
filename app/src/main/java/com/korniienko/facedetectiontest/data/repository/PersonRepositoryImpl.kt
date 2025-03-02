package com.korniienko.facedetectiontest.data.repository

import com.korniienko.facedetectiontest.data.local.Person
import com.korniienko.facedetectiontest.data.local.PersonDao
import com.korniienko.facedetectiontest.domain.model.PersonDomain
import com.korniienko.facedetectiontest.domain.repository.PersonRepository
import javax.inject.Inject

class PersonRepositoryImpl @Inject constructor(
    private val personDao: PersonDao
) : PersonRepository {

    override suspend fun insertPerson(person: PersonDomain) {
        personDao.insert(Person.fromDomain(person))
    }

    override suspend fun getAllPersons(): List<PersonDomain> {
        return personDao.getAllPersons().map { it.toDomain() }
    }
}
