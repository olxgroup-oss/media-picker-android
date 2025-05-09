package com.mediapicker.gallery.domain.entity

enum class Action {
    NONE,
    ADD,
    EDIT,
    REMOVE;

    override fun toString(): String {
        return name
    }

    companion object {
        fun fromName(name: String): Action? {
            for (type in entries) {
                if (type.name == name) {
                    return type
                }
            }
            return null
        }
    }
}