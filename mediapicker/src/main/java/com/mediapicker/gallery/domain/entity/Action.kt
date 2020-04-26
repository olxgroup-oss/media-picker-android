package com.mediapicker.gallery.domain.entity

enum class Action private constructor(name: String) {
    NONE("NONE"),
    ADD("ADD"),
    EDIT("EDIT"),
    REMOVE("REMOVE");

    override fun toString(): String {
        return name
    }

    companion object {
        fun fromName(name: String): Action? {
            for (type in Action.values()) {
                if (type.name == name) {
                    return type
                }
            }
            return null
        }
    }
}