package com.mediapicker.gallery.presentation.carousalview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.mediapicker.gallery.R
import com.mediapicker.gallery.domain.entity.MediaGalleryEntity

class MediaGalleryView(context: Context, attrs: AttributeSet?) : MediaGalleryPagerView(context, attrs), View.OnClickListener,
    MediaGalleryPagerView.MediaChangeListener {

    private var onGalleryItemClickListener: OnGalleryItemClickListener? = null

    private fun initialize() {
        setMediaGalleryViewListeners()

    }

    private fun setMediaGalleryViewListeners() {
        setOnItemClickListener(this)
        setOnMediaChangeListener(this)
    }

    fun setImagesForPager(imagesList: MutableList<MediaGalleryEntity>) {
        setImages(imagesList)
    }

    fun addMediaForPager(media: MediaGalleryEntity) {
        addMedia(media)
    }

    fun removeMediaFromPager(media: MediaGalleryEntity) {
        removeMedia(media)
    }

    fun updateDefaultPhoto(id: Int) {
        val defaultPhoto = findViewById<AppCompatImageView>(R.id.defaultPhoto)
        defaultPhoto.setImageResource(id)
    }

    fun updateDefaultText(id: Int) {
        val tvDefaultText = findViewById<TextView>(R.id.tvDefaultText)
        tvDefaultText.setText(id)
    }

    fun setOnGalleryClickListener(onGalleryItemClickListener: OnGalleryItemClickListener?) {
        this.onGalleryItemClickListener = onGalleryItemClickListener
    }

    interface OnGalleryItemClickListener {
        fun onGalleryItemClick(mediaIndex: Int)
    }

    init {
        initialize()
    }

    override fun onClick(view: View) {
        onGalleryItemClickListener!!.onGalleryItemClick(currentItem)
    }

    override fun onMediaChanged(mediaPosition: Int) {

    }
}