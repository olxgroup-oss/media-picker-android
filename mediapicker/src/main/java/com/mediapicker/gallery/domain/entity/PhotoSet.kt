package com.mediapicker.gallery.domain.entity

import java.io.Serializable
import kotlin.math.max
import kotlin.math.min

open class PhotoSet : Serializable {

    var id: String? = null

    var externalId: String? = null

    var imageURL: String? = null

    protected var width: Int = 0

    protected var height: Int = 0

    var backgroundPhoto: Photo? = null

    protected var full: Photo? = null

    var bigPhoto: Photo? = null

    var mediumPhoto: Photo? = null

    var smallPhoto: Photo? = null


    val imageClampedAspectRatio: Float
        get() {
            if (height == 0 || width == 0) {
                return MISSING_ASPECT_RATIO
            }
            val aspectRatio = width.toFloat() / height
            return min(MAX_ASPECT_RATIO, max(MIN_ASPECT_RATIO, aspectRatio))
        }

    constructor()

    constructor(uploadedPhoto: UploadedPhoto) {
        this.id = uploadedPhoto.id
        this.smallPhoto = Photo(0, 0, uploadedPhoto.url)
    }

    constructor(photoProfile: PhotoProfile) {
        backgroundPhoto = Photo(0, 0, photoProfile.backgroundUrl!!)
        smallPhoto = Photo(0, 0, photoProfile.smallUrl!!)
        mediumPhoto = Photo(0, 0, photoProfile.mediumUrl!!)
        bigPhoto = Photo(0, 0, photoProfile.bigUrl!!)
    }

    constructor(smallPhoto: Photo) {
        this.smallPhoto = smallPhoto
    }

    fun getImageURL(size: PhotoSize): String? {
        val photo = getPhoto(size)
        return photo?.url
    }


    fun getPhoto(size: PhotoSize): Photo? {
        var photo: Photo? = when (size) {
            PhotoSize.SMALL -> this.smallPhoto
            PhotoSize.MEDIUM -> this.mediumPhoto
            PhotoSize.BIG -> this.bigPhoto
            PhotoSize.FULL -> if (full == null) {
                this.backgroundPhoto
            } else {
                this.full
            }
        }
        return photo
    }

    fun hasPhoto(): Boolean {
        return (smallPhoto != null && mediumPhoto != null
                && bigPhoto != null)
    }

    companion object {
        internal val serialVersionUID = PhotoSet::class.java.name.hashCode().toLong()
        internal const val MIN_ASPECT_RATIO = 0.5f
        internal const val MAX_ASPECT_RATIO = 2f
        const val MISSING_ASPECT_RATIO = -1f
    }
}

