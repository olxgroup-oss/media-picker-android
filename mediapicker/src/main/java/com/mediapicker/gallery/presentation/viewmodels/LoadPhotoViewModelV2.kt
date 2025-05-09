package com.mediapicker.gallery.presentation.viewmodels

import android.app.Application
import android.database.Cursor
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import com.mediapicker.gallery.GalleryConfig
import com.mediapicker.gallery.domain.entity.Action
import com.mediapicker.gallery.domain.entity.CameraItem
import com.mediapicker.gallery.domain.entity.IGalleryItem
import com.mediapicker.gallery.domain.entity.PhotoAlbum
import com.mediapicker.gallery.domain.entity.PhotoFile
import com.mediapicker.gallery.domain.entity.Status
import com.mediapicker.gallery.presentation.viewmodels.factory.BaseLoadMediaViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class LoadPhotoViewModelV2(application: Application) : BaseLoadMediaViewModel(application) {

    private val _galleryItems = MutableLiveData<List<IGalleryItem>>()
    val galleryItems: LiveData<List<IGalleryItem>> get() = _galleryItems

    private lateinit var config: GalleryConfig

    fun loadData(config: GalleryConfig, preSelectedItems: List<IGalleryItem>) {
        this.config = config
        initializeGalleryItems(preSelectedItems)
    }

    fun addPhotos(photos: List<File>) {
        viewModelScope.launch(Dispatchers.IO) {
            val updatedGalleryItems = _galleryItems.value?.toMutableList() ?: mutableListOf()
            val newItems = photos.map { photo -> createPhotoItem(photo) }
            updatedGalleryItems.addAll(newItems)
            _galleryItems.postValue(updatedGalleryItems)
        }
    }

    private fun createPhotoItem(photo: File): PhotoFile {
        val imageId = photo.nameWithoutExtension.toLongOrNull() ?: 0L
        return PhotoFile.Builder()
            .imageId(imageId)
            .path(photo.path)
            .smallPhotoUrl("")
            .photoBackendId(0L)
            .action(Action.ADD)
            .status(Status.PENDING)
            .build()
    }


    private fun initializeGalleryItems(preSelectedItems: List<IGalleryItem>) {
        val initialItems = mutableListOf<IGalleryItem>()
        if (needToAddCameraView())
            initialItems.add(CameraItem())
        if (needToAddFolderView())
            initialItems.add(PhotoAlbum.dummyInstance)
        initialItems.addAll(preSelectedItems)
        _galleryItems.postValue(initialItems)
    }

    private fun needToAddFolderView(): Boolean {
        return (config.typeOfMediaSupported == GalleryConfig.MediaType.PhotoWithFolderOnly
                || config.typeOfMediaSupported == GalleryConfig.MediaType.PhotoWithFolderAndVideo
                || config.typeOfMediaSupported == GalleryConfig.MediaType.PhotoWithoutCameraFolderOnly)
    }

    private fun needToAddCameraView(): Boolean {
        return (config.typeOfMediaSupported != GalleryConfig.MediaType.PhotoWithoutCameraFolderOnly)
    }

    override fun getCursorLoader(): Loader<Cursor> {
        val selection = MediaStore.Images.Media.MIME_TYPE + "!=?"
        val mimeTypeGif = MimeTypeMap.getSingleton().getMimeTypeFromExtension("gif")
        val selectionTypeGifArgs = arrayOf(mimeTypeGif)
        return CursorLoader(
            getApplication(),
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, selection,
            selectionTypeGifArgs, MediaStore.Images.Media.DATE_ADDED + " DESC"
        )
    }

    override fun getUniqueLoaderId() = 1

    override fun prepareDataForAdapterAndPost(cursor: Cursor) {}
}