package com.mediapicker.gallery.domain.entity

enum class Status {
    PENDING,
    POSTING,
    OK,
    VALIDATION_ERROR,
    NETWORK_ERROR;

    override fun toString(): String {
        return name
    }

    companion object {

        fun fromName(name: String): Status? {
            for (type in entries) {
                if (type.name == name) {
                    return type
                }
            }
            return null
        }
    }
}