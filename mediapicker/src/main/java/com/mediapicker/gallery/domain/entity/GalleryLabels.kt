package com.mediapicker.gallery.domain.entity

import android.view.Gravity

data class GalleryLabels(
    val homeTitle: String = "",
    val homeAction: String = "",
    val galleryFolderAction: String = "",
    val titleAlignment: Int = Gravity.START or Gravity.CENTER_VERTICAL
)
