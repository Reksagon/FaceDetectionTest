package com.korniienko.facedetectiontest.data.di

import android.content.Context
import androidx.room.Room
import com.korniienko.facedetectiontest.data.local.AppDatabase
import com.korniienko.facedetectiontest.data.local.PersonDao
import com.korniienko.facedetectiontest.data.repository.PersonRepositoryImpl
import com.korniienko.facedetectiontest.domain.repository.PersonRepository
import com.korniienko.facedetectiontest.domain.use_case.AddPersonUseCase
import com.korniienko.facedetectiontest.domain.use_case.RecognizeFaceUseCase
import com.korniienko.facedetectiontest.utils.FaceDetectionHelper
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    fun providePersonDao(database: AppDatabase): PersonDao {
        return database.personDao()
    }

    @Provides
    @Singleton
    fun provideFaceDetectionHelper(): FaceDetectionHelper {
        return FaceDetectionHelper()
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindPersonRepository(
        impl: PersonRepositoryImpl
    ): PersonRepository
}
