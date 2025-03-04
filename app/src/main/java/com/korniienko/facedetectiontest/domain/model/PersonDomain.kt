package com.korniienko.facedetectiontest.domain.model

import com.korniienko.facedetectiontest.data.local.Person


data class PersonDomain(
    val id: Int = 0,
    val name: String,
    val position: String,
    val faceImageBase64: String
) {
    fun toEntity(): Person {
        return Person(
            id = id,
            name = name,
            position = position,
            faceImage = faceImageBase64
        )
    }
}
