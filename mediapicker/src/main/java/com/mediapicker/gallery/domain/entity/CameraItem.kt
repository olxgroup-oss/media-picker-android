package com.mediapicker.gallery.domain.entity

import java.io.Serializable
import java.util.ArrayList

class CameraItem : IGalleryItem, Serializable {

    var name = "Camera"
    var albumId: String? = "-1"
    var albumEntries: List<PhotoFile> = ArrayList()


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as CameraItem?
        return if (this.albumId == null) {
            that!!.albumId == null
        } else this.albumId == that!!.albumId
    }

    override fun hashCode(): Int {
        return if (this.albumId == null) {
            0
        } else albumId!!.hashCode()
    }
}
