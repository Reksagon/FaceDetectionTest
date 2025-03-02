package com.korniienko.facedetectiontest

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.korniienko.facedetectiontest.data.local.Person
import com.korniienko.facedetectiontest.data.local.PersonDao
import com.korniienko.facedetectiontest.data.repository.PersonRepositoryImpl
import com.korniienko.facedetectiontest.domain.model.PersonDomain
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@ExperimentalCoroutinesApi
class PersonRepositoryImplTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private lateinit var personRepository: PersonRepositoryImpl
    private lateinit var personDao: PersonDao

    @Before
    fun setUp() {
        personDao = mockk(relaxed = true)
        personRepository = PersonRepositoryImpl(personDao)
    }

    @Test
    fun `insertPerson should call insert on dao`() = runTest {
        val person = PersonDomain(0,"Марія", "Дизайнер", "file://face2.jpg")

        personRepository.insertPerson(person)

        coVerify { personDao.insert(any()) }
    }

    @Test
    fun `getAllPersons should return mapped list`() = runTest {
        val testPersons = listOf(Person(0,"Марія", "Дизайнер", "file://face2.jpg"))
        coEvery { personDao.getAllPersons() } returns testPersons

        val result = personRepository.getAllPersons()

        assertEquals(1, result.size)
        assertEquals("Марія", result.first().name)
    }
}
