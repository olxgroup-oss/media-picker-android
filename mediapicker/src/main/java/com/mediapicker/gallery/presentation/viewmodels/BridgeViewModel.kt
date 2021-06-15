package com.mediapicker.gallery.presentation.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mediapicker.gallery.GalleryConfig
import com.mediapicker.gallery.domain.action.RuleAction
import com.mediapicker.gallery.domain.entity.PhotoFile

class BridgeViewModel(
    private var listOfSelectedPhotos: List<PhotoFile>,
    private var listOfSelectedVideos: List<VideoFile>,
    private val galleryConfig: GalleryConfig
) : ViewModel() {

    private val ruleAction: RuleAction = RuleAction(galleryConfig.validation)

    private val reloadMediaLiveData = MutableLiveData<Unit>()

    private val recordVideoLiveData = MutableLiveData<Unit>()

    private val actionButtonStateLiveData = MutableLiveData<Boolean>()

    private val errorStateLiveData = MutableLiveData<String>()

    private val closeHostingViewLiveData = MutableLiveData<Boolean>()

    fun recordVideoWithNativeCamera() = recordVideoLiveData

    fun getActionState() = actionButtonStateLiveData

    fun getMediaStateLiveData() = reloadMediaLiveData

    fun getError() = errorStateLiveData

    fun getClosingSignal() = closeHostingViewLiveData

    fun setCurrentSelectedPhotos(listOfSelectedPhotos: List<PhotoFile>) {
        this.listOfSelectedPhotos = listOfSelectedPhotos
        shouldEnableActionButton()
    }

    fun setCurrentSelectedVideos(listOfSelectedVideos: List<VideoFile>) {
        this.listOfSelectedVideos = listOfSelectedVideos
        shouldEnableActionButton()
    }

    fun getSelectedPhotos(): List<PhotoFile> = listOfSelectedPhotos

    private fun shouldEnableActionButton() {
        if(galleryConfig.shouldOnlyValidatePhoto()){
            val status = ruleAction.shouldEnableActionButton(listOfSelectedPhotos.size)
            actionButtonStateLiveData.postValue(status)
        }else{
            val status = ruleAction.shouldEnableActionButton(Pair(listOfSelectedPhotos.size, listOfSelectedVideos.size))
            actionButtonStateLiveData.postValue(status)
        }
    }

    private fun onActionButtonClick() {
        galleryConfig.galleryCommunicator?.actionButtonClick(listOfSelectedPhotos, listOfSelectedVideos)
    }


    fun shouldRecordVideo() {
        if (galleryConfig.shouldUseVideoCamera) {
            recordVideoLiveData.postValue(Unit)
        } else {
            galleryConfig.galleryCommunicator?.recordVideo()
        }
    }

    fun onBackPressed() {
        galleryConfig.galleryCommunicator?.onCloseMainScreen()
    }

    fun getMaxSelectionLimit() = galleryConfig.validation.getMaxPhotoSelectionRule().maxSelectionLimit

    fun getMaxVideoSelectionLimit() = galleryConfig.validation.getMaxVideoSelectionRule().maxSelectionLimit

    fun getMaxLimitErrorResponse() = galleryConfig.validation.getMaxPhotoSelectionRule().message

    fun reloadMedia() {
        reloadMediaLiveData.postValue(Unit)
    }

    fun shouldUseMyCamera(): Boolean {
        galleryConfig.galleryCommunicator?.captureImage()
        return galleryConfig.shouldUsePhotoCamera
    }

    fun onFolderSelect(){
        galleryConfig.galleryCommunicator?.onFolderSelect()
    }

    fun getMaxVideoLimitErrorResponse() = galleryConfig.validation.getMaxVideoSelectionRule().message

    fun complyRules() {
        val error = ruleAction.getFirstFailingMessage(Pair(listOfSelectedPhotos.size, listOfSelectedVideos.size))
        if (error.isEmpty()) {
            onActionButtonClick()
            closeHostingViewLiveData.postValue(true)
        } else {
            errorStateLiveData.postValue(error)
        }
    }

}