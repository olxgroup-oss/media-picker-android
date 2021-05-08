package com.mediapicker.gallery.presentation.carousalview

import com.mediapicker.gallery.domain.entity.PhotoFile

interface CarousalActionListener {
    fun onItemClicked(photoFile: PhotoFile, isSelected: Boolean)
    fun onGalleryFolderSelected()
    fun onGalleryImagePreview()
    fun onGalleryImagePreviewChanged()
}