package com.mediapicker.gallery.presentation.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.mediapicker.gallery.domain.entity.PhotoAlbum
import com.mediapicker.gallery.domain.entity.PhotoFile
import com.mediapicker.gallery.presentation.fragments.BaseFragment
import com.mediapicker.gallery.presentation.fragments.FolderViewFragment
import com.mediapicker.gallery.presentation.fragments.GalleryPhotoViewFragment
import com.mediapicker.gallery.presentation.utils.Constants.EXTRA_SELECTED_PHOTO
import com.mediapicker.gallery.presentation.utils.Constants.PHOTO_SELECTION_REQUEST_CODE
import kotlinx.android.synthetic.main.oss_base_fragment_activity.*


class FolderViewActivity : BaseFragmentActivity(), GalleryActionListener {

    companion object {
        fun startActivityForResult(fragment: Fragment, currentSelectPhotos: LinkedHashSet<PhotoFile>) {
            fragment.startActivityForResult(Intent(fragment.activity, FolderViewActivity::class.java).apply {
                this.putExtra(EXTRA_SELECTED_PHOTO, currentSelectPhotos)
            }, PHOTO_SELECTION_REQUEST_CODE)
        }
    }

    private var currentSelectedPhotos = LinkedHashSet<PhotoFile>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCurrentSelectedPhotos()
        setFragment(FolderViewFragment.getInstance())
    }

    private fun setCurrentSelectedPhotos() {
        currentSelectedPhotos = intent.getSerializableExtra(EXTRA_SELECTED_PHOTO) as LinkedHashSet<PhotoFile>
    }

    override fun moveToPhotoGrid(photoAlbum: PhotoAlbum) {
        setFragment(GalleryPhotoViewFragment.getInstance(photoAlbum, currentSelectedPhotos))
    }

    override fun onPhotoSelected(postingDraftPhoto: PhotoFile) {
        if (currentSelectedPhotos.contains(postingDraftPhoto)) {
            currentSelectedPhotos.remove(postingDraftPhoto)
        } else {
            currentSelectedPhotos.add(postingDraftPhoto)
        }
    }

    override fun isPhotoAlreadySelected(postingDraftPhoto: PhotoFile): Boolean {
        if (currentSelectedPhotos.contains(postingDraftPhoto)) {
            currentSelectedPhotos.remove(postingDraftPhoto)
            return true
        }
        return false
    }

    override fun onActionClicked(shouldThrowResult: Boolean) {
        if (shouldThrowResult) {
            setResult(Activity.RESULT_OK, Intent().apply { this.putExtra(EXTRA_SELECTED_PHOTO, currentSelectedPhotos) })
            finish()
        } else {
            onBackPressed()
        }
    }

    override fun showCrossButton() {
        // showCloseButton()
    }

    override fun onBackPressed() {
        val fragments = supportFragmentManager.backStackEntryCount
        when {
            fragments == 1 -> finish()
            fragments > 1 -> supportFragmentManager.popBackStack()
            else -> super.onBackPressed()
        }
    }
}

interface GalleryActionListener {
    fun moveToPhotoGrid(photoAlbum: PhotoAlbum)
    fun onPhotoSelected(postingDraftPhoto: PhotoFile)
    fun onActionClicked(shouldThrowResult: Boolean)
    fun isPhotoAlreadySelected(postingDraftPhoto: PhotoFile): Boolean
    fun showCrossButton()
}