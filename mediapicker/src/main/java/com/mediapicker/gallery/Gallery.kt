package com.mediapicker.gallery

import android.graphics.drawable.Drawable
import com.mediapicker.gallery.domain.contract.IGalleryCommunicator
import java.io.Serializable

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



interface IGalleryButton : Serializable {
    fun setText(text : String)
    fun setButtonBackground(drawable: Drawable)
    fun setSelected(state : Boolean)
    fun onClick()
}
