package com.mediapicker.gallery.domain.entity

enum class Status private constructor(name: String) {
    PENDING("PENDING"),
    POSTING("POSTING"),
    OK("OK"),
    VALIDATION_ERROR("VALIDATION_ERROR"),
    NETWORK_ERROR("NETWORK_ERROR");

    override fun toString(): String {
        return name
    }

    companion object {

        fun fromName(name: String): Status? {
            for (type in Status.values()) {
                if (type.name == name) {
                    return type
                }
            }
            return null
        }
    }
}