package com.mediapicker.gallery.domain.entity

import java.io.Serializable
import java.util.ArrayList

class CameraItem : IGalleryItem, Serializable {

    var name = "Camera"
    var albumId: String? = "-1"
    var albumEntries: List<PostingDraftPhoto> = ArrayList()


    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as CameraItem?
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
