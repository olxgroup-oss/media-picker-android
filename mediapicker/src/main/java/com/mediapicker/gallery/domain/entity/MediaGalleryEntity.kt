package com.mediapicker.gallery.domain.entity

import java.io.Serializable

data class MediaGalleryEntity(
    val fileName: String?,
    val mediaId: Long?,
    val path: String?,
    val isLocalImage: Boolean = false,
    val mediaType: GalleryViewMediaType
) : Serializable

