package com.mediapicker.gallery.domain.entity

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.mediapicker.gallery.R

data class GalleryUIConfig(
    @ColorRes val tileColor: Int = R.color.primary,
    @DrawableRes val cameraIcon: Int = R.drawable.oss_media_ic_camera,
    @DrawableRes val folderIcon: Int = R.drawable.oss_media_ic_folder_icon,
    @DrawableRes val backIcon: Int = R.drawable.oss_media_ic_back
)