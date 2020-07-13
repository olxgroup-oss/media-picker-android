package com.mediapicker.gallery.presentation.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import com.mediapicker.gallery.Gallery
import com.mediapicker.gallery.R
import com.mediapicker.gallery.domain.entity.PhotoFile
import com.mediapicker.gallery.presentation.activity.FolderViewActivity
import com.mediapicker.gallery.presentation.adapters.IGalleryItemClickListener
import com.mediapicker.gallery.presentation.adapters.SelectPhotoImageAdapter
import com.mediapicker.gallery.presentation.utils.Constants.EXTRA_SELECTED_PHOTO
import com.mediapicker.gallery.presentation.utils.Constants.PHOTO_SELECTION_REQUEST_CODE
import com.mediapicker.gallery.presentation.utils.FileUtils
import com.mediapicker.gallery.presentation.utils.getFragmentScopedViewModel
import com.mediapicker.gallery.presentation.viewmodels.LoadPhotoViewModel
import java.io.File
import java.io.Serializable
import java.util.*


open class PhotoGridFragment : BaseViewPagerItemFragment() {

    companion object {
        fun getInstance(title: String, listOfSelectedPhotos: List<PhotoFile>) = PhotoGridFragment().also {
            it.pageTitle = title
            it.arguments = Bundle().apply { putSerializable(EXTRA_SELECTED_PHOTOS, listOfSelectedPhotos as Serializable) }
        }
    }

    private var isSingleSelectionMode = false
    private var numberOfPhotosBeforeCapture: Int = 0
    private val TAKING_PHOTO = 9999
    protected var numberOfPhoto = 0


    private var isExpectingNewPhoto: Boolean = false
    private lateinit var lastRequestFileToSavePath: String

    private val currentSelectedPhotos: LinkedHashSet<PhotoFile> = LinkedHashSet()

    private val listCurrentPhotos: MutableList<PhotoFile> by lazy {
        val i = getPhotosFromArguments().toMutableList()
        currentSelectedPhotos.addAll(i)
        return@lazy i
    }


    private val loadPhotoViewModel: LoadPhotoViewModel by lazy {
        getFragmentScopedViewModel { LoadPhotoViewModel(Gallery.galleryConfig) }
    }

    private val galleryItemAdapter: SelectPhotoImageAdapter by lazy {
        SelectPhotoImageAdapter(
            emptyList(),
            listCurrentPhotos,
            galleryItemSelectHandler,
            true
        )
    }

    override fun getScreenTitle() = getString(R.string.title_tab_photo)

    override fun getBaseLoadMediaViewModel() = loadPhotoViewModel

    override fun getMediaAdapter() = galleryItemAdapter

    override fun initViewModels() {
        super.initViewModels()
        loadPhotoViewModel.getGalleryItems().observe(this, Observer {
            galleryItemAdapter.updateGalleryItems(it)
            onStepValidate()
        })
        loadPhotoViewModel.loadMedia(this)
    }

    private val galleryItemSelectHandler = object :
        IGalleryItemClickListener {
        override fun onPhotoItemClick(photo: PhotoFile, position: Int) {
            if (handleItemClick(photo)) {
                updateData(position)
            }
        }

        override fun onFolderItemClick() {
            //trackingService.postingFolderSelect()
            FolderViewActivity.startActivityForResult(this@PhotoGridFragment, currentSelectedPhotos)
        }

        override fun onCameraIconClick() {
            if (bridgeViewModel.shouldUseMyCamera())
                startTakingPicture()
        }
    }

    private fun startTakingPicture() {
        /*trackingContextRepository.setPictureOrigin(NinjaParamValues.TAKE_PICTURE)
        trackingService.postingPictureSelect()*/

        isExpectingNewPhoto = true
        val lastRequestFileToSave = FileUtils.getNewPhotoFileOnPicturesDirectory()
        val fileUri: Uri
        fileUri = if (android.text.TextUtils.isEmpty(Gallery.getClientAuthority())) {
            Uri.fromFile(lastRequestFileToSave)
        } else {
            FileProvider.getUriForFile(
                context!!,
                Gallery.getClientAuthority(),
                lastRequestFileToSave
            )
        }
        lastRequestFileToSavePath = lastRequestFileToSave.absolutePath
        numberOfPhotosBeforeCapture = galleryItemAdapter.itemCount

        val pickIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        pickIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        pickIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)

        val chooserIntent = Intent.createChooser(pickIntent, "Capture new Photo")
        pickIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(chooserIntent, TAKING_PHOTO)
    }


    protected fun handleItemClick(photo: PhotoFile): Boolean {
        if (isSingleSelectionMode) {
            onImageAdded("", photo)
            galleryItemAdapter.notifyDataSetChanged()
            return true
        } else if (currentSelectedPhotos.contains(photo)) {
            if (onImageRemoved("", photo)) {
                removePhotoFromCurrentSelection(photo, getPosition(photo))
                galleryItemAdapter.notifyDataSetChanged()
                onStepValidate()
                return true
            }
        } else if (currentSelectedPhotos.size < bridgeViewModel.getMaxSelectionLimit()) {
            if (onImageValidate("", photo) && onImageAdded("", photo)) {
                addNewPhotoToCurrentSelection(photo, getPosition(photo))
                onStepValidate()
                return true
            }
        } else {
            showMsg(bridgeViewModel.getMaxLimitErrorResponse())
        }
        return false
    }


    fun addNewPhotoToCurrentSelection(photo: PhotoFile, position: Int) {
        if (!currentSelectedPhotos.contains(photo)) {
            currentSelectedPhotos.add(photo)
            listCurrentPhotos.add(photo)
            updateData(position)
        }
    }


    protected fun updateData(position: Int?) {
        numberOfPhoto = currentSelectedPhotos.size
        if (position != null) {
            galleryItemAdapter.notifyItemChanged(position)
        } else {
            galleryItemAdapter.notifyDataSetChanged()
        }
    }

    private fun getPosition(photo: PhotoFile): Int {
        var i = 0
        while (i < listCurrentPhotos.size) {
            if (listCurrentPhotos[i].imageId == photo.imageId) {
                return ++i
            }
            i++
        }
        return 0
    }


    private fun removePhotoFromCurrentSelection(photo: PhotoFile, position: Int) {
        if (currentSelectedPhotos.contains(photo)) {
            currentSelectedPhotos.remove(photo)
            removeFromList(photo)
            updateData(position)
        }
    }

    private fun removeFromList(photoToRemove: PhotoFile) {
        val iterator = listCurrentPhotos.listIterator()
        while (iterator.hasNext()) {
            val photo = iterator.next()
            if (photo.imageId == photoToRemove.imageId) {
                iterator.remove()
                return
            }
        }
    }

    private fun onStepValidate() {
        //validate the steps here
        //enable the action_button here when item selection is done
        bridgeViewModel.setCurrentSelectedPhotos(listCurrentPhotos)
    }

    override fun onBackPressed() {

    }

    fun onImageAdded(fragmentName: String, photo: PhotoFile): Boolean {
        addItem(photo)
        //trackingService.postingPictureComplete()
        return true
    }

    fun onImageRemoved(fragmentName: String, photo: PhotoFile): Boolean {
        removeItem(photo)
        return true
    }

    fun onImageValidate(fragmentName: String, photo: PhotoFile): Boolean {
        return true
    }

    fun addItem(photo: PhotoFile) {
        currentSelectedPhotos.add(photo)
        listCurrentPhotos.add(photo)
    }

    fun removeItem(photo: PhotoFile) {
        currentSelectedPhotos.remove(photo)
        removeFromList(photo)
    }

    override fun shouldHideToolBar() = true

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == PHOTO_SELECTION_REQUEST_CODE) run {
            val finalSelectionFromFolders = data?.getSerializableExtra(EXTRA_SELECTED_PHOTO) as LinkedHashSet<PhotoFile>
            setSelectedFromFolderAndNotify(finalSelectionFromFolders)
        } else if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                TAKING_PHOTO -> {
                    if (lastRequestFileToSavePath.isNotEmpty()) {
                        val requestFile = File(lastRequestFileToSavePath)
                        activity?.sendBroadcast(
                            Intent(
                                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                                Uri.fromFile(requestFile)
                            )
                        )
                    }
                    loadPhotoViewModel.loadMedia(this)
                }
            }
        }
    }

    private fun setSelectedFromFolderAndNotify(photoSet: LinkedHashSet<PhotoFile>) {
        currentSelectedPhotos.clear()
        currentSelectedPhotos.addAll(photoSet)

        listCurrentPhotos.clear()
        listCurrentPhotos.addAll(currentSelectedPhotos)

        galleryItemAdapter.notifyDataSetChanged()
    }


}