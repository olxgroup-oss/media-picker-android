package com.mediapicker.gallery

import com.mediapicker.gallery.domain.contract.IGalleryCommunicator

object Gallery{
    internal lateinit var galleryConfig : GalleryConfig

    fun init(galleryConfig: GalleryConfig){
        this.galleryConfig = galleryConfig
    }

    fun updateCommunicator(galleryCommunicator: IGalleryCommunicator){
        galleryConfig.galleryCommunicator = galleryCommunicator
    }

    internal fun getApp() = galleryConfig.applicationContext

    internal fun getClientAuthority() = galleryConfig.clientAuthority

}
