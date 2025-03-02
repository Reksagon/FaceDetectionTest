package com.korniienko.facedetectiontest.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.korniienko.facedetectiontest.domain.model.PersonDomain

@Entity(tableName = "persons")
data class Person(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val position: String,
    val faceImage: String
) {
    fun toDomain(): PersonDomain {
        return PersonDomain(
            id = id,
            name = name,
            position = position,
            faceImagePath = faceImage
        )
    }

    companion object {
        fun fromDomain(domain: PersonDomain): Person {
            return Person(
                id = domain.id,
                name = domain.name,
                position = domain.position,
                faceImage = domain.faceImagePath
            )
        }
    }
}
