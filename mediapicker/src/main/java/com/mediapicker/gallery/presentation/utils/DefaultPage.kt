package com.mediapicker.gallery.presentation.utils

import java.io.Serializable


sealed class DefaultPage : Serializable {
    data object PhotoPage : DefaultPage()
    data object VideoPage : DefaultPage()
}