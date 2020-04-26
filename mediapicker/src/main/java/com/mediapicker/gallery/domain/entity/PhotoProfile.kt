package com.mediapicker.gallery.domain.entity

import java.io.Serializable

class PhotoProfile : Serializable {
    var smallUrl: String? = null

    var mediumUrl: String? = null

    var bigUrl: String? = null

    var backgroundUrl: String? = null

    constructor(small: String, big: String) {
        this.smallUrl = small
        this.bigUrl = big
    }

    constructor(photoset: PhotoSet) {
        if (photoset.smallPhoto != null) {
            smallUrl = photoset.smallPhoto!!.url
        }
        if (photoset.mediumPhoto != null) {
            mediumUrl = photoset.mediumPhoto!!.url
        }
        if (photoset.bigPhoto != null) {
            bigUrl = photoset.bigPhoto!!.url
        }
        if (photoset.backgroundPhoto != null) {
            backgroundUrl = photoset.backgroundPhoto!!.url
        }
    }

    companion object {
        internal val serialVersionUID = PhotoProfile::class.java.name.hashCode().toLong()
    }
}
