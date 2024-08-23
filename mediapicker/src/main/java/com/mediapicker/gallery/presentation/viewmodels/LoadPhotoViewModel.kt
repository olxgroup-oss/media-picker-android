package com.mediapicker.gallery.presentation.viewmodels

//import android.database.DataSetObserver
import android.database.Cursor
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.lifecycle.MutableLiveData
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import com.mediapicker.gallery.GalleryConfig
import com.mediapicker.gallery.domain.entity.CameraItem
import com.mediapicker.gallery.domain.entity.IGalleryItem
import com.mediapicker.gallery.domain.entity.PhotoAlbum
import com.mediapicker.gallery.domain.entity.PhotoFile
import com.mediapicker.gallery.presentation.viewmodels.factory.BaseLoadMediaViewModel

class LoadPhotoViewModel(val galleryConfig: GalleryConfig) :
    BaseLoadMediaViewModel(galleryConfig) {

    companion object {
        private const val COL_FULL_PHOTO_URL = "fullPhotoUrl"
    }

//    private var isObserverRegistered = false
//
//    private var lastLoadedCursor: Cursor? = null

    var currentSelectedPhotos: LinkedHashSet<PhotoFile> = LinkedHashSet()

    private val galleryItemsLiveData = MutableLiveData<List<IGalleryItem>>()

    fun getGalleryItems() = galleryItemsLiveData

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

    override fun prepareDataForAdapterAndPost(cursor: Cursor) {
        val listOfGalleryItems: MutableList<IGalleryItem> = ArrayList()
        if (cursor.moveToFirst()) {
            val photos = ArrayList<IGalleryItem>()
            do {
                val photo = getPhoto(cursor)
                photos.add(photo)
            } while (cursor.moveToNext())
            listOfGalleryItems.clear()
            if (needToAddCameraView())
                listOfGalleryItems.add(CameraItem())
            if (needToAddFolderView())
                listOfGalleryItems.add(PhotoAlbum.dummyInstance)
            listOfGalleryItems.addAll(getFinalListOfGalleryItems(photos))
        }
        galleryItemsLiveData.postValue(listOfGalleryItems)
    }

//    private fun unregisterDataSetObserver() {
//        if (lastLoadedCursor != null && isObserverRegistered) {
//            lastLoadedCursor?.unregisterDataSetObserver(dataObserver)
//            isObserverRegistered = false
//        }
//    }
//
//    private fun registerDataSetObserver() {
//        if (!isObserverRegistered) {
//            if (lastLoadedCursor != null) {
//                lastLoadedCursor?.registerDataSetObserver(dataObserver)
//                isObserverRegistered = true
//            }
//        }
//    }
//
//    private val dataObserver = object : DataSetObserver() {
//        override fun onChanged() {
//            super.onChanged()
//            getNewlyAddedMedia()
//        }
//    }

//    private fun getNewlyAddedMedia() {
//
//    }

    private fun needToAddFolderView(): Boolean {
        return (galleryConfig.typeOfMediaSupported == GalleryConfig.MediaType.PhotoWithFolderOnly
                || galleryConfig.typeOfMediaSupported == GalleryConfig.MediaType.PhotoWithFolderAndVideo
                || galleryConfig.typeOfMediaSupported == GalleryConfig.MediaType.PhotoWithoutCameraFolderOnly)
    }

    private fun needToAddCameraView(): Boolean {
        return (galleryConfig.typeOfMediaSupported != GalleryConfig.MediaType.PhotoWithoutCameraFolderOnly)
    }


    private fun getFinalListOfGalleryItems(photos: List<IGalleryItem>): Collection<IGalleryItem> {
        val finalSetOfPhotos = ArrayList<IGalleryItem>()
        if (currentSelectedPhotos.isNotEmpty()) {
            for (currentSelectedPhoto in currentSelectedPhotos) {
                if (!photos.contains(currentSelectedPhoto)) {
                    val photoBackendId = currentSelectedPhoto.photoBackendId
                    if (photoBackendId != null) {
                        currentSelectedPhoto.path = "" + photoBackendId
                    }
                    finalSetOfPhotos.add(currentSelectedPhoto)
                }
            }
        }
        finalSetOfPhotos.addAll(photos)
        return finalSetOfPhotos
    }


    private fun getPhoto(cursor: Cursor): PhotoFile {
        val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
        val path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
        val mimeType =
            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE))
        val col = cursor.getColumnIndex(COL_FULL_PHOTO_URL)
        var fullPhotoUrl = ""
        if (col != -1) {
            fullPhotoUrl = cursor.getString(col)

        }
        return PhotoFile.Builder()
            .imageId(id)
            .path(path)
            .smallPhotoUrl("")
            .fullPhotoUrl(fullPhotoUrl)
            .mimeType()
            .photoBackendId(0L)
            .build()
    }

//    override fun onCleared() {
//        super.onCleared()
//        // unregisterDataSetObserver()
//    }
}
