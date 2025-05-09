package com.mediapicker.gallery.presentation.fragments

import android.content.Context
import com.mediapicker.gallery.presentation.activity.GalleryActionListener

abstract class BaseGalleryViewFragment : BaseFragment() {

    protected var galleryActionListener: GalleryActionListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is GalleryActionListener) {
            galleryActionListener = context
        }
    }

    override fun onBackPressed() {
        activity?.onBackPressedDispatcher?.onBackPressed()
    }
}