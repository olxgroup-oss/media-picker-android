package com.mediapicker.gallery

import android.app.Application
import androidx.annotation.LayoutRes
import com.mediapicker.gallery.domain.contract.GalleryCommunicatorDefaultImpl
import com.mediapicker.gallery.domain.contract.IGalleryCommunicator
import com.mediapicker.gallery.domain.entity.Validation

class GalleryConfig(
    val application: Application,
    val clientAuthority: String,
    var galleryCommunicator: IGalleryCommunicator,
    val shouldUsePhotoCamera: Boolean,
    val shouldUseVideoCamera: Boolean,
    val photoViewPlaceHolder: Int,
    val typeOfMediaSupported: MediaType,
    val validation: Validation,
    val mediaScanningCriteria: MediaScanningCriteria
) {


    fun isGalleryInitialize() = application != null && clientAuthority != null


    class GalleryNotInitilizedException(message: String = "Please initialize gallery with application and client authority") : Exception(message)

    class GalleryConfigBuilder(
        private val application: Application,
        private val clientAuthority: String,
        private val galleryCommunicator: IGalleryCommunicator = GalleryCommunicatorDefaultImpl()
    ) {
        private var shouldUsePhotoCamera: Boolean = false
        private var shouldUseVideoCamera: Boolean = false

        @LayoutRes
        private var photoViewPlaceHolder: Int = 0
        private var typeOfMediaSupported: MediaType = MediaType.PhotoWithFolderAndVideo
        private lateinit var validation: Validation
        private var mediaScanningCriteria = MediaScanningCriteria()

        fun useMyPhotoCamera(shouldUseMyCamera: Boolean) = apply { this.shouldUsePhotoCamera = shouldUseMyCamera }
        fun useMyVideoCamera(shouldUseMyCamera: Boolean) = apply { this.shouldUseVideoCamera = shouldUseMyCamera }
        fun photoViewPlaceHolder(layout: Int) = apply { this.photoViewPlaceHolder = layout }
        fun typeOfMediaSupported(mediaType: MediaType) = apply { this.typeOfMediaSupported = mediaType }

        fun validation(validation: Validation): GalleryConfigBuilder {
            this.validation = validation
            return this
        }

        fun mediaScanningCriteria(mediaScanningCriteria: MediaScanningCriteria) = apply { this.mediaScanningCriteria = mediaScanningCriteria }

        fun build() = GalleryConfig(
            application,
            clientAuthority,
            galleryCommunicator,
            shouldUsePhotoCamera,
            shouldUseVideoCamera,
            photoViewPlaceHolder,
            typeOfMediaSupported,
            validation,
            mediaScanningCriteria
        )

    }

    sealed class MediaType {
        object PhotoOnly : MediaType()
        object PhotoWithVideo : MediaType()
        object PhotoWithFolderAndVideo : MediaType()
        object PhotoWithFolderOnly : MediaType()
    }

    data class MediaScanningCriteria(val photoBrowseQuery: String = "", val videoBrowseQuery: String = "") {

        fun hasCustomQueryForPhoto() = photoBrowseQuery.isNotEmpty()

        fun hasCustomQueryForVideo() = videoBrowseQuery.isNotEmpty()
    }

}


