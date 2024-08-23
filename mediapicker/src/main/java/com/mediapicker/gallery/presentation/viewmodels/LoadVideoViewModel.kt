package com.mediapicker.gallery.presentation.viewmodels

import android.content.ContentUris
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.lifecycle.MutableLiveData
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import com.mediapicker.gallery.GalleryConfig
import com.mediapicker.gallery.presentation.viewmodels.factory.BaseLoadMediaViewModel
import java.io.Serializable

class LoadVideoViewModel(val galleryConfig: GalleryConfig) : BaseLoadMediaViewModel(galleryConfig) {

    private val videoItemLiveData = MutableLiveData<List<VideoItem>>()

    private val loadingStateLiveData = MutableLiveData<StateData>()

    fun getVideoItem() = videoItemLiveData

    private fun getFolderCriteria(): Pair<String, String> {
        if (galleryConfig.mediaScanningCriteria.hasCustomQueryForVideo()) {
            return Pair(
                " AND ${MediaStore.Video.VideoColumns.DATA} like ? ",
                "%${galleryConfig.mediaScanningCriteria.videoBrowseQuery}%"
            )
        }
        return Pair("", "")
    }

    override fun getCursorLoader(): Loader<Cursor> {
        var selection = MediaStore.Images.Media.MIME_TYPE + "!=? "
        val mimeTypeGif = MimeTypeMap.getSingleton().getMimeTypeFromExtension("gif")
        val projection = mutableListOf<String?>(mimeTypeGif)
        val folderCriteria = getFolderCriteria()
        if (folderCriteria.first.isNotEmpty()) {
            selection += folderCriteria.first
            projection.add(folderCriteria.second)
        }
        return CursorLoader(
            galleryConfig.applicationContext,
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, selection,
            projection.toTypedArray(), MediaStore.Images.Media.DATE_TAKEN + " DESC"
        )
    }

    override fun getUniqueLoaderId() = 2

    override fun prepareDataForAdapterAndPost(cursor: Cursor) {
        val videoList = mutableListOf<VideoItem>()
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
        val nameColumn =
            cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
        val durationColumn =
            cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
        val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)

        videoList.add(RecordVideoItem())
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val duration = cursor.getInt(durationColumn)
                val size = cursor.getInt(sizeColumn)
                val contentUri: Uri =
                    ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)
                val thumbnail = MediaStore.Video.Thumbnails.getThumbnail(
                    galleryConfig.applicationContext.contentResolver,
                    id, MediaStore.Video.Thumbnails.MICRO_KIND, null
                )
                if (!name.isNullOrBlank() && duration > 0)
                    videoList += VideoFile(id, contentUri, name, duration, size, thumbnail)
            } while (cursor.moveToNext())
        }
        loadingStateLiveData.postValue(StateData.SUCCESS)
        videoItemLiveData.postValue(videoList)
    }
}

interface VideoItem

data class VideoFile(
    val id: Long, @Transient val uri: Uri, val name: String, val duration: Int, val size: Int,
    @Transient val thumbnail: Bitmap?
) : VideoItem,
    Serializable {

    var isSelected: Boolean = false

    fun getFormattedDuration(): String {
        val durationINSec = duration / 1000
        val hours = durationINSec / 3600
        val secondsLeft = durationINSec - hours * 3600
        val minutes = secondsLeft / 60
        val seconds = secondsLeft - minutes * 60

        var formattedTime = ""
        if (hours < 10)
            formattedTime += "0"
        formattedTime += "$hours : "

        if (minutes < 10)
            formattedTime += "0"
        formattedTime += "$minutes : "

        if (seconds < 10)
            formattedTime += "0"
        formattedTime += seconds

        return formattedTime
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as VideoFile?
        return this.id == that!!.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

data class RecordVideoItem(val id: String = "") : VideoItem