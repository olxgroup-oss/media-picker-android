package com.mediapicker.gallery.domain.contract

import com.mediapicker.gallery.domain.entity.PhotoFile
import com.mediapicker.gallery.presentation.utils.PermissionRequestWrapper
import com.mediapicker.gallery.presentation.viewmodels.VideoFile
import permissions.dispatcher.PermissionRequest
import java.io.File


interface IGalleryCommunicator{

    fun captureImage()

    fun onImageCaptured(capturedImage: File)

    fun recordVideo()

    fun onVideoRecorded(file : File)

    fun onCloseMainScreen()

    fun actionButtonClick(listOfSelectedPhotos: List<PhotoFile>, listofSelectedVideos: List<VideoFile>)

    fun onFolderSelect()

    fun onPermissionDenied()

    fun onNeverAskPermissionAgain()

    fun onShowPermissionRationale(permissionRequest: PermissionRequestWrapper)

    fun onStepValidate(isValid: Boolean)
}

internal interface GalleryPagerCommunicator {
    fun onItemClicked(photoFile: PhotoFile, isSelected: Boolean)

    fun onPreviewItemsUpdated(listOfSelectedPhotos: List<PhotoFile>)
}


internal class GalleryCommunicatorDefaultImpl : IGalleryCommunicator{

    override fun onCloseMainScreen() {

    }

    override fun actionButtonClick(listOfSelectedPhotos: List<PhotoFile>, listofSelectedVideos: List<VideoFile>) {

    }

    override fun onFolderSelect() {
        //Need Implementation
    }

    override fun captureImage() {
        //Need Implementation
    }

    override fun onImageCaptured(capturedImage: File) {
        //Need Implementation
    }

    override fun recordVideo() {
        //Need Implementation
    }

    override fun onVideoRecorded(file: File) {
        //Need Implementation
    }

    override fun onPermissionDenied() {
        ////Need Implementation
    }

    override fun onNeverAskPermissionAgain() {
      //Need Implementation
    }

    override fun onShowPermissionRationale(permissionRequest: PermissionRequestWrapper) {
        //Need Implementation
    }

    override fun onStepValidate(isValid: Boolean) {
        //Need Implementation
    }
}