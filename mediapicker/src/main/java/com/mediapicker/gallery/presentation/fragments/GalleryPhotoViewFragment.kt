package com.mediapicker.gallery.presentation.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.mediapicker.gallery.Gallery
import com.mediapicker.gallery.R
import com.mediapicker.gallery.domain.entity.PhotoAlbum
import com.mediapicker.gallery.domain.entity.PhotoFile
import com.mediapicker.gallery.presentation.adapters.IGalleryItemClickListener
import com.mediapicker.gallery.presentation.adapters.SelectPhotoImageAdapter
import com.mediapicker.gallery.presentation.utils.Constants.EXTRA_SELECTED_ALBUM
import com.mediapicker.gallery.presentation.utils.Constants.EXTRA_SELECTED_PHOTO
import com.mediapicker.gallery.presentation.utils.ItemDecorationAlbumColumns
import com.mediapicker.gallery.presentation.utils.ValidatePhotos
import com.mediapicker.gallery.presentation.utils.ValidationResult
import com.mediapicker.gallery.utils.SnackbarUtils
import kotlinx.android.synthetic.main.oss_custom_toolbar.*
import kotlinx.android.synthetic.main.oss_fragment_carousal.*
import kotlinx.android.synthetic.main.oss_fragment_folder_view.*

const val COLUMNS_COUNT = 3

class GalleryPhotoViewFragment : BaseGalleryViewFragment() {

    lateinit var adapter: SelectPhotoImageAdapter

    private var photoValidationAction: ValidatePhotos = ValidatePhotos()

    private val photoAlbum: PhotoAlbum by lazy {
        arguments?.getSerializable(EXTRA_SELECTED_ALBUM) as PhotoAlbum
    }

    private val currentSelectedPhotos: LinkedHashSet<PhotoFile> by lazy {
        arguments?.getSerializable(EXTRA_SELECTED_PHOTO) as LinkedHashSet<PhotoFile>
    }

    private fun removePhotoFromSelection(photo: PhotoFile, position: Int) {
        currentSelectedPhotos.removePhoto(photo)
        Gallery.carousalActionListener?.onItemClicked(photo, false)
        adapter.listCurrentPhotos = currentSelectedPhotos.toList()
        adapter.notifyDataSetChanged()
    }

    private fun showError(msg: String?) {
        SnackbarUtils.show(view, msg, Snackbar.LENGTH_SHORT)
    }

    override fun getScreenTitle() = photoAlbum.name ?: ""

    override fun getLayoutId() = R.layout.oss_fragment_folder_view

    override fun setUpViews() {
        super.setUpViews()
        photoAlbum.let { album ->
            adapter = SelectPhotoImageAdapter(album.getAlbumEntries(), currentSelectedPhotos.toList(), galleryItemClickListener, fromGallery = false)
        }

        folderRV.apply {
            this.addItemDecoration(ItemDecorationAlbumColumns(resources.getDimensionPixelSize(R.dimen.module_base), COLUMNS_COUNT))
            this.layoutManager = GridLayoutManager(activity, COLUMNS_COUNT)
            this.adapter = this@GalleryPhotoViewFragment.adapter
        }

        if (Gallery.galleryConfig.galleryLabels.galleryFolderAction.isNotBlank()) {
            actionButton.text = Gallery.galleryConfig.galleryLabels.galleryFolderAction
        }

        toolbarTitle.isAllCaps = Gallery.galleryConfig.textAllCaps
        actionButton.isAllCaps = Gallery.galleryConfig.textAllCaps

        toolbarTitle.gravity = Gallery.galleryConfig.galleryLabels.titleAlignment
    }

    @SuppressLint("CheckResult")
    private fun handleItemClickListener(photo: PhotoFile, position: Int) {
        if (currentSelectedPhotos.containsPhoto(photo)) {
            removePhotoFromSelection(photo, position)
        } else {
            validateNewPhoto(photo, position)
        }
    }

    private fun validateNewPhoto(photo: PhotoFile, position: Int) {
        when(val validationResult = photoValidationAction.canAddThisToList(currentSelectedPhotos.size, photo)){
            is ValidationResult.Success -> {
                galleryActionListener?.onPhotoSelected(photo)
                Gallery.carousalActionListener?.onItemClicked(photo, true)
                adapter.listCurrentPhotos = currentSelectedPhotos.toList()
                adapter.notifyDataSetChanged()
            }
            is ValidationResult.Failure -> {
                var msg = validationResult.msg
                showError(msg)
            }
        }
    }

    override fun onActionButtonClick() {
        super.onActionButtonClick()
        galleryActionListener?.onActionClicked(false)
    }

    override fun setHomeAsUp() = true

    private val galleryItemClickListener = object : IGalleryItemClickListener {

        override fun onPhotoItemClick(photo: PhotoFile, position: Int) {
            handleItemClickListener(photo, position)
        }

        override fun onFolderItemClick() {

        }

        override fun onCameraIconClick() {

        }
    }

    companion object {
        fun getInstance(photoAlbum: PhotoAlbum, currentSelectedPhotos: java.util.LinkedHashSet<PhotoFile>) = GalleryPhotoViewFragment().apply {
            arguments = Bundle().apply {
                this.putSerializable(EXTRA_SELECTED_ALBUM, photoAlbum)
                this.putSerializable(EXTRA_SELECTED_PHOTO, currentSelectedPhotos)
            }
        }
    }
}