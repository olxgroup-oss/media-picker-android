package com.mediapicker.gallery.domain.repositories

import com.mediapicker.gallery.domain.entity.PhotoAlbum

interface GalleryRepository {

    fun getAlbums(): HashSet<PhotoAlbum>
}