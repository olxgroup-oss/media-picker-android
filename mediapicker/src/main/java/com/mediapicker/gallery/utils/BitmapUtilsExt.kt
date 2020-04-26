package com.mediapicker.gallery.utils

import android.graphics.BitmapFactory


fun getBitmapWidth(filePath : String) : Int {
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeFile(filePath, options)

    return options.outWidth
}

fun getBitmapHeight(filePath: String) : Int {
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeFile(filePath, options)

    return options.outHeight
}