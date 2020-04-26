package com.mediapicker.gallery.domain.contract

import com.mediapicker.gallery.domain.entity.PostingDraftPhoto
import com.mediapicker.gallery.presentation.viewmodels.VideoFile
import java.io.File


interface IGalleryCommunicator{

    fun captureImage()

    fun onImageCaptured(capturedImage: File)

    fun recordVideo()

    fun onVideoRecorded(file : File)

    fun onCloseMainScreen()

    fun actionButtonClick(listOfSelectedPhotos: List<PostingDraftPhoto>, listofSelectedVideos: List<VideoFile>)
}


internal class GalleryCommunicatorDefaultImpl : IGalleryCommunicator{

    override fun onCloseMainScreen() {

    }

    override fun actionButtonClick(listOfSelectedPhotos: List<PostingDraftPhoto>, listofSelectedVideos: List<VideoFile>) {

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
}