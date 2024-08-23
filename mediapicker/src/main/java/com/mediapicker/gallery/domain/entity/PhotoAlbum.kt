package com.mediapicker.gallery.domain.entity

import java.io.Serializable
import java.util.*

class PhotoAlbum(var albumId: String?, var name: String?) : IGalleryItem, Serializable {
    private var albumEntries: MutableList<IGalleryItem> = ArrayList()

    val firstPhoto: IGalleryItem
        get() = albumEntries[0]

    fun getAlbumEntries(): List<IGalleryItem> {
        return albumEntries
    }

    fun hasPhotos(): Boolean {
        return albumEntries.isNotEmpty()
    }

    fun setAlbumEntries(albumEntries: MutableList<IGalleryItem>) {
        this.albumEntries = albumEntries
    }

    fun addEntryToAlbum(photo: PhotoFile) {
        albumEntries.add(photo)
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as PhotoAlbum?
        return if (this.albumId == null) {
            that!!.albumId == null
        } else this.albumId == that!!.albumId
    }

    override fun hashCode(): Int {
        return if (this.albumId == null) {
            0
        } else albumId!!.hashCode()
    }

    companion object {
        val dummyInstance: PhotoAlbum
            get() = PhotoAlbum("Folders", "")
    }
}
