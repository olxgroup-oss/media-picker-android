package com.mediapicker.gallery.presentation.utils

import java.io.Serializable


sealed class DefaultPage : Serializable {
    object PhotoPage : DefaultPage()
    object VideoPage : DefaultPage()
}