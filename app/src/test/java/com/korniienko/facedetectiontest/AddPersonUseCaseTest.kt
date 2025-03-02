package com.korniienko.facedetectiontest

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.korniienko.facedetectiontest.domain.model.PersonDomain
import com.korniienko.facedetectiontest.domain.repository.PersonRepository
import com.korniienko.facedetectiontest.domain.use_case.AddPersonUseCase
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
class AddPersonUseCaseTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private lateinit var addPersonUseCase: AddPersonUseCase
    private lateinit var repository: PersonRepository

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        addPersonUseCase = AddPersonUseCase(repository)
    }

    @Test
    fun `execute should call insertPerson on repository`() = runTest {
        val person = PersonDomain(0, "Іван", "Менеджер", "file://face1.jpg")

        addPersonUseCase.execute(person)

        coVerify { repository.insertPerson(person) }
    }
}
