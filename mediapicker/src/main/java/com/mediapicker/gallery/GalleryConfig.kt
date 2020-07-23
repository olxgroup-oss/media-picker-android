package com.mediapicker.gallery

import android.content.Context
import androidx.annotation.LayoutRes
import com.mediapicker.gallery.domain.contract.GalleryCommunicatorDefaultImpl
import com.mediapicker.gallery.domain.contract.IGalleryCommunicator
import com.mediapicker.gallery.domain.entity.Validation

class GalleryConfig(
    val applicationContext: Context,
    val clientAuthority: String,
    var galleryCommunicator: IGalleryCommunicator,
    val shouldUsePhotoCamera: Boolean,
    val shouldUseVideoCamera: Boolean,
    val needToShowCover: Boolean,
    val photoViewPlaceHolder: Int,
    val typeOfMediaSupported: MediaType,
    val validation: Validation,
    val mediaScanningCriteria: MediaScanningCriteria
) {


    fun shouldOnlyValidatePhoto() = typeOfMediaSupported == MediaType.PhotoWithFolderOnly || typeOfMediaSupported == MediaType.PhotoOnly

    fun isGalleryInitialize() = applicationContext != null && clientAuthority != null


    class GalleryNotInitilizedException(message: String = "Please initialize gallery with application and client authority") : Exception(message)

    class GalleryConfigBuilder(
        private val applicationContext: Context,
        private val clientAuthority: String,
        private val galleryCommunicator: IGalleryCommunicator = GalleryCommunicatorDefaultImpl()
    ) {
        private var shouldUsePhotoCamera: Boolean = false
        private var shouldUseVideoCamera: Boolean = false
        private var needToShowCover: Boolean = true

        @LayoutRes
        private var photoViewPlaceHolder: Int = 0
        private var typeOfMediaSupported: MediaType = MediaType.PhotoWithFolderAndVideo
        private lateinit var validation: Validation
        private var mediaScanningCriteria = MediaScanningCriteria()

        fun useMyPhotoCamera(shouldUseMyCamera: Boolean) = apply { this.shouldUsePhotoCamera = shouldUseMyCamera }
        fun useMyVideoCamera(shouldUseMyCamera: Boolean) = apply { this.shouldUseVideoCamera = shouldUseMyCamera }
        fun needToShowCover(needToShowCover: Boolean) = apply { this.needToShowCover = needToShowCover }
        fun photoViewPlaceHolder(layout: Int) = apply { this.photoViewPlaceHolder = layout }
        fun typeOfMediaSupported(mediaType: MediaType) = apply { this.typeOfMediaSupported = mediaType }

        fun validation(validation: Validation): GalleryConfigBuilder {
            this.validation = validation
            return this
        }

        fun mediaScanningCriteria(mediaScanningCriteria: MediaScanningCriteria) = apply { this.mediaScanningCriteria = mediaScanningCriteria }

        fun build() = GalleryConfig(
            applicationContext,
            clientAuthority,
            galleryCommunicator,
            shouldUsePhotoCamera,
            shouldUseVideoCamera,
            needToShowCover,
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


