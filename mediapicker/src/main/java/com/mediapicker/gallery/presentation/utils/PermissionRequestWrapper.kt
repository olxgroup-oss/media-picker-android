package com.mediapicker.gallery.presentation.utils

import permissions.dispatcher.PermissionRequest

class PermissionRequestWrapper (private val permissionRequest: PermissionRequest) {
    fun proceed() {
        permissionRequest.proceed()
    }

    fun cancel() {
        permissionRequest.cancel()
    }
}