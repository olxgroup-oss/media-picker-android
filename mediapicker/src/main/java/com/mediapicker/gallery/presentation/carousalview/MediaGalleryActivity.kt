package com.mediapicker.gallery.presentation.carousalview

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.mediapicker.gallery.R
import com.mediapicker.gallery.domain.entity.MediaGalleryEntity
import kotlinx.android.synthetic.main.oss_media_gallery_activity.*

class MediaGalleryActivity : AppCompatActivity(), View.OnClickListener, MediaGalleryPagerView.MediaChangeListener {

    private var origin = ""
    private var selectedPhotoIndex = 0
    private var mediaGalleryList: MutableList<MediaGalleryEntity> = mutableListOf()

    companion object {
        fun startActivityForResult(fragment: Fragment,
                                   mediaGalleryList: ArrayList<MediaGalleryEntity>,
                                   mediaIndex : Int,
                                   pageSource: String,
                                   requestCode: Int) {
            fragment.startActivityForResult(Intent(fragment.activity, MediaGalleryActivity::class.java).apply {
                this.putExtra(GALLERY_MEDIA_LIST, mediaGalleryList)
                this.putExtra(GALLERY_MEDIA_INDEX, mediaIndex)
                this.putExtra(MEDIA_GALLERY_SOURCE, pageSource)
            }, requestCode)
        }

        const val GALLERY_MEDIA_LIST = "gallery_media_list"
        const val GALLERY_MEDIA_INDEX = "gallery_media_index"
        const val MEDIA_GALLERY_SOURCE = "source"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.oss_media_gallery_activity)

        if(intent != null) {
            mediaGalleryList = intent.extras?.getSerializable(GALLERY_MEDIA_LIST) as ArrayList<MediaGalleryEntity>
            selectedPhotoIndex = if (intent.extras?.containsKey(GALLERY_MEDIA_INDEX)!!) intent.extras!!.getInt(GALLERY_MEDIA_INDEX) else 0
            origin = intent.extras?.getString(MEDIA_GALLERY_SOURCE)!!
        }
        crossButton.setOnClickListener(this)
        loadImagesInGallery(mediaGalleryList)
    }

    private fun loadImagesInGallery(imageList: MutableList<MediaGalleryEntity>) {
        imagePager.setPinchPanZoomEnabled(true)
        imagePager.setIsGallery(true)
        imagePager.setImages(imageList)
        imagePager.setSelectedPhoto(selectedPhotoIndex)
        imagePager.setOnMediaChangeListener(this)
    }

    override fun onBackPressed() {
        closeActivityWithResult(Activity.RESULT_CANCELED)
    }

    private fun closeActivityWithResult(resultCode : Int) {
        val intent = Intent()
        intent.putExtra(GALLERY_MEDIA_INDEX, imagePager.currentItem)
        setResult(resultCode, intent)
        finish()
    }

    override fun onClick(view: View) {
        val id = view.id

        when {
            id.equals(R.id.crossButton) -> {
                closeActivityWithResult(Activity.RESULT_OK)
            }
        }
    }

    override fun onMediaChanged(mediaPosition: Int) {
    }
}
