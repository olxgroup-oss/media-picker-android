package com.mediapicker.gallery.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.mediapicker.gallery.GalleryConfig

class HomeViewModel(private val galleryConfig: GalleryConfig) : ViewModel()  {

    fun getMediaType() = galleryConfig.typeOfMediaSupported

}