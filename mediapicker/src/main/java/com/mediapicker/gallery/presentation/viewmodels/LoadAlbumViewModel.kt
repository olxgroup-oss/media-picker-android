package com.mediapicker.gallery.presentation.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mediapicker.gallery.domain.entity.PhotoAlbum
import com.mediapicker.gallery.domain.repositories.GalleryRepository

class LoadAlbumViewModel(private val galleryRepository: GalleryRepository) : ViewModel() {

    private val albumLiveData = MutableLiveData<HashSet<PhotoAlbum>>()

    fun getAlbums() = albumLiveData

    fun loadAlbums() {
        albumLiveData.postValue(galleryRepository.getAlbums())
    }

}
