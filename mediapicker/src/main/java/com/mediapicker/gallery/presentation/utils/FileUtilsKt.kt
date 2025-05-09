package com.mediapicker.gallery.presentation.utils

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

fun Context.saveUriToInternalStorage(uri: Uri): File {
    var inputStream: InputStream? = null
    var outputStream: FileOutputStream? = null
    val file = File(filesDir, getFileNameFromUri(uri))

    try {
        inputStream = contentResolver.openInputStream(uri)
        outputStream = FileOutputStream(file)

        val buffer = ByteArray(1024)
        var length: Int
        while (inputStream?.read(buffer).also { length = it ?: -1 } != -1) {
            outputStream.write(buffer, 0, length)
        }
    } catch (_: IOException) {
    } finally {
        try {
            inputStream?.close()
            outputStream?.close()
        } catch (_: IOException) {
        }
    }

    return file
}


fun Context.getFileNameFromUri(uri: Uri): String {
    val fallbackName = System.currentTimeMillis().toString()
    var fileName = fallbackName
    val contentResolver: ContentResolver = contentResolver
    val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)

    cursor?.use {
        val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (it.moveToFirst()) {
            fileName = it.getString(nameIndex) ?: fallbackName
        }
    }

    if (fileName == fallbackName) {
        val path = uri.lastPathSegment
        fileName = path ?: fallbackName
    }

    return fileName
}

