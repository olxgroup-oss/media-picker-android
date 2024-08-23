package com.mediapicker.gallery.domain.entity

import android.text.TextUtils

import java.io.File
import java.io.Serializable

class PhotoFile : Serializable, IGalleryItem {
    var imageId: Long = 0L
        private set
    var path: String? = null

    var smallPhotoUrl: String? = null
        private set

    var fullPhotoUrl: String? = null
        private set

    var photoBackendId: Long? = null
        private set

    var action: Action? = null
    var apolloKey: String? = null
    var status: Status? = null
    var error: String? = null
    var adId: String? = null
    var mimeType: String? = null

    val isAlreadyUploaded: Boolean
        get() = !TextUtils.isEmpty(fullPhotoUrl)

    private constructor(builder: Builder) {
        this.imageId = builder.imageId
        this.path = builder.path
        this.smallPhotoUrl = builder.smallPhotoUrl
        this.fullPhotoUrl = builder.fullPhotoUrl
        this.photoBackendId = builder.photoBackendId
        this.action = builder.action
        this.apolloKey = builder.apolloKey
        this.status = builder.status
        this.error = builder.error
        this.adId = builder.adId
        this.mimeType = builder.mimeType
    }

    constructor(photoSet: PhotoSet) {
        fullPhotoUrl = photoSet.getImageURL(PhotoSize.FULL)
        smallPhotoUrl = photoSet.getImageURL(PhotoSize.SMALL)
        if (photoSet.id != null) {
            imageId = java.lang.Long.parseLong(photoSet.id!!)
            photoBackendId = java.lang.Long.parseLong(photoSet.id!!)
        }
        apolloKey = photoSet.externalId
        status = Status.OK
        action = Action.NONE
    }

    private fun existsFile(path: String): Boolean {
        return File(path).exists()
    }

    fun existsPhoto(): Boolean {
        var existPhoto = true
        path?.isEmpty()?.let { existPhoto = existsFile(path!!) }
        return existPhoto
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as PhotoFile?
        if (this.imageId == 0L || that!!.imageId == 0L) {
            return this.path == that!!.path
        }
        return this.imageId == that.imageId
    }

    override fun hashCode(): Int {
        return imageId.hashCode()
    }

    class Builder {
        var imageId: Long = 0L
        var path: String = ""
        var smallPhotoUrl: String = ""
        internal var fullPhotoUrl = ""
        var photoBackendId: Long? = null
        var action: Action? = null
        var apolloKey: String? = null
        var status: Status? = null
        var error: String? = null
        var adId: String? = null
        var mimeType: String? = null

        fun imageId(imageId: Long): Builder {
            this.imageId = imageId
            return this
        }

        fun path(path: String): Builder {
            this.path = path
            return this
        }

        fun smallPhotoUrl(smallPhotoUrl: String): Builder {
            this.smallPhotoUrl = smallPhotoUrl
            return this
        }

        fun fullPhotoUrl(fullPhotoUrl: String): Builder {
            this.fullPhotoUrl = fullPhotoUrl
            return this
        }

        fun photoBackendId(photoBackendId: Long?): Builder {
            this.photoBackendId = photoBackendId
            return this
        }

        fun action(action: Action): Builder {
            this.action = action
            return this
        }

        fun apolloKey(apolloKey: String): Builder {
            this.apolloKey = apolloKey
            return this
        }

        fun status(status: Status): Builder {
            this.status = status
            return this
        }

        fun error(error: String): Builder {
            this.error = error
            return this
        }

        fun adId(adId: String): Builder {
            this.adId = adId
            return this
        }

        fun mimeType(): Builder {
            return this
        }

        fun build(): PhotoFile {
            return PhotoFile(this)
        }
    }
}