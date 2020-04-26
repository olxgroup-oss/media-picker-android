package com.mediapicker.gallery.presentation.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.mediapicker.gallery.domain.entity.PhotoAlbum
import com.mediapicker.gallery.domain.entity.PostingDraftPhoto
import com.mediapicker.gallery.presentation.fragments.BaseFragment
import com.mediapicker.gallery.presentation.fragments.FolderViewFragment
import com.mediapicker.gallery.presentation.fragments.GalleryPhotoViewFragment
import com.mediapicker.gallery.presentation.utils.Constants.EXTRA_SELECTED_PHOTO
import com.mediapicker.gallery.presentation.utils.Constants.PHOTO_SELECTION_REQUEST_CODE
import kotlinx.android.synthetic.main.activity_folder_view.*


class FolderViewActivity : AppCompatActivity(),
    GalleryActionListener {

    companion object {
        fun startActivityForResult(fragment: Fragment, currentSelectPhotos: LinkedHashSet<PostingDraftPhoto>) {
            fragment.startActivityForResult(Intent(fragment.activity, FolderViewActivity::class.java).apply {
                this.putExtra(EXTRA_SELECTED_PHOTO, currentSelectPhotos)
            }, PHOTO_SELECTION_REQUEST_CODE)
        }
    }

    private var currentSelectedPhotos = LinkedHashSet<PostingDraftPhoto>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.mediapicker.gallery.R.layout.activity_folder_view)
        setCurrentSelectedPhotos()
        setFragment(FolderViewFragment.getInstance())
    }

    private fun setFragment(fragment: BaseFragment) {
        try {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.add(container.id, fragment, fragment.javaClass.name)
            transaction.addToBackStack(fragment.javaClass.name)
            transaction.commitAllowingStateLoss()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun setCurrentSelectedPhotos() {
        currentSelectedPhotos = intent.getSerializableExtra(EXTRA_SELECTED_PHOTO) as LinkedHashSet<PostingDraftPhoto>
    }

    override fun moveToPhotoGrid(photoAlbum: PhotoAlbum) {
        setFragment(GalleryPhotoViewFragment.getInstance(photoAlbum, currentSelectedPhotos))
    }

    override fun onPhotoSelected(postingDraftPhoto: PostingDraftPhoto) {
        if (currentSelectedPhotos.contains(postingDraftPhoto)) {
            currentSelectedPhotos.remove(postingDraftPhoto)
        } else {
            currentSelectedPhotos.add(postingDraftPhoto)
        }
    }

    override fun isPhotoAlreadySelected(postingDraftPhoto: PostingDraftPhoto): Boolean {
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
    fun onPhotoSelected(postingDraftPhoto: PostingDraftPhoto)
    fun onActionClicked(shouldThrowResult: Boolean)
    fun isPhotoAlreadySelected(postingDraftPhoto: PostingDraftPhoto): Boolean
    fun showCrossButton()
}