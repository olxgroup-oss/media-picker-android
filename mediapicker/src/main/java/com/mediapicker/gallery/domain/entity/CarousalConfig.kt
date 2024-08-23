package com.mediapicker.gallery.domain.entity

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class CarousalConfig(
    val showCarousal: Boolean = false,
    @DrawableRes val imageId: Int = 0,
    val addImage: Boolean = false,
    @StringRes val previewText: Int = 0
)