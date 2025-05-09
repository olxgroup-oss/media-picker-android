package com.mediapicker.gallery.data.repositories

import android.content.ContentResolver
import android.database.Cursor
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.mediapicker.gallery.domain.entity.PhotoAlbum
import com.mediapicker.gallery.domain.entity.PhotoFile
import com.mediapicker.gallery.domain.repositories.GalleryRepository

open class GalleryService(private val contentResolver: ContentResolver) : GalleryRepository {

    companion object {
        //        fun getInstance(context: Application) = GalleryService(context)
        const val COL_FULL_PHOTO_URL = "fullPhotoUrl"
    }


    @Throws(IllegalArgumentException::class)
    override fun getAlbums(): HashSet<PhotoAlbum> {
        return queryMedia()
    }


    private fun queryMedia(): HashSet<PhotoAlbum> {
        val mutableListOfFolders = hashSetOf<PhotoAlbum>()
        val selection = MediaStore.Images.Media.MIME_TYPE + "!=?"
        val mimeTypeGif = MimeTypeMap.getSingleton().getMimeTypeFromExtension("gif")
        val selectionTypeGifArgs = arrayOf(mimeTypeGif)
        val cursor = MediaStore.Images.Media.query(
            contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, selection, selectionTypeGifArgs,
            MediaStore.Images.Media.DATE_ADDED + " DESC"
        )
        if (cursor != null && cursor.moveToFirst()) {
            do {
                val album = getAlbumEntry(cursor)
                album?.let { item ->
                    val photo = getPhoto(cursor)
                    if (mutableListOfFolders.contains(item)) {
                        mutableListOfFolders.forEach {
                            if (it == item) {
                                it.addEntryToAlbum(photo)
                            }
                        }
                    } else {
                        item.addEntryToAlbum(photo)
                        mutableListOfFolders.add(item)
                    }
                }
            } while (cursor.moveToNext())
        }
        return mutableListOfFolders
    }

    private fun getAlbumEntry(cursor: Cursor): PhotoAlbum? {
        val albumIdIndex = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID)

        val albumNameIndex = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
        if (albumIdIndex != -1 && albumNameIndex != -1) {
            val id = cursor.getInt(albumIdIndex)
            val name = cursor.getString(albumNameIndex)
            return PhotoAlbum(id.toString(), name)
        } else {
            return null
        }

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
            .photoBackendId(0L)
            .mimeType()
            .build()
    }

}
