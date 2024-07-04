package com.mediapicker.gallery.presentation.fragments

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
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
import com.mediapicker.gallery.presentation.utils.ValidatePhotos
import com.mediapicker.gallery.presentation.utils.getFragmentScopedViewModel
import com.mediapicker.gallery.presentation.viewmodels.LoadPhotoViewModel
import java.io.Serializable


open class PhotoGridFragment : BaseViewPagerItemFragment() {

    private var photoValidationAction: ValidatePhotos = ValidatePhotos()

    companion object {
        fun getInstance(title: String, listOfSelectedPhotos: List<PhotoFile>) =
            PhotoGridFragment().also {
                it.pageTitle = title
                it.arguments = Bundle().apply {
                    putSerializable(
                        EXTRA_SELECTED_PHOTOS,
                        listOfSelectedPhotos as Serializable
                    )
                }
            }
    }

    private var isSingleSelectionMode = false
    private var numberOfPhotosBeforeCapture: Int = 0
    private val TAKING_PHOTO = 9999
    protected var numberOfPhoto = 0


    private var isExpectingNewPhoto: Boolean = false
    private var lastRequestFileToSavePath = ""

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

    override fun getScreenTitle() = getString(R.string.oss_title_tab_photo)

    override fun getBaseLoadMediaViewModel() = loadPhotoViewModel

    override fun getMediaAdapter() = galleryItemAdapter

    override fun initViewModels() {
        super.initViewModels()
        for (listCurrentPhoto in listCurrentPhotos) {
            loadPhotoViewModel.currentSelectedPhotos.add(listCurrentPhoto)
        }
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
            bridgeViewModel.onFolderSelect()
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
        } else if (checkIfAlreadySelected(photo)) {
            removePhotoFromCurrentSelection(photo, getPosition(photo))
            galleryItemAdapter.notifyDataSetChanged()
            onStepValidate()
            return true
        } else if (currentSelectedPhotos.size < bridgeViewModel.getMaxSelectionLimit()) {
            if (onImageValidate("", photo) && onImageAdded("", photo)) {
                addNewPhotoToCurrentSelection(photo, getPosition(photo))
                onStepValidate()
                return true
            }
        } else {
            bridgeViewModel.getError().postValue(bridgeViewModel.getMaxLimitErrorResponse())
        }
        return false
    }

    private fun checkIfAlreadySelected(newSelection: PhotoFile): Boolean {
        currentSelectedPhotos.forEach { selectedPhoto ->
            if (newSelection == selectedPhoto) {
                return true
            }
        }
        return false
    }


    fun addNewPhotoToCurrentSelection(photo: PhotoFile, position: Int) {
        if (!currentSelectedPhotos.containsPhoto(photo)) {
            currentSelectedPhotos.add(photo)
            listCurrentPhotos.add(photo)
            Gallery.pagerCommunicator?.onItemClicked(photo, true)
            Gallery.carousalActionListener?.onItemClicked(photo, true)
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
        if (currentSelectedPhotos.containsPhoto(photo)) {
            currentSelectedPhotos.removePhoto(photo)
            removeFromList(photo)
            Gallery.pagerCommunicator?.onItemClicked(photo, false)
            Gallery.carousalActionListener?.onItemClicked(photo, false)
            updateData(position)
        }
    }

    private fun removeFromList(photoToRemove: PhotoFile) {
        val iterator = listCurrentPhotos.listIterator()
        while (iterator.hasNext()) {
            val photo = iterator.next()
            if (photo == photoToRemove) {
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
        Gallery.pagerCommunicator?.onItemClicked(photo, true)
        Gallery.carousalActionListener?.onItemClicked(photo, true)
        //trackingService.postingPictureComplete()
        return true
    }

    fun onImageRemoved(fragmentName: String, photo: PhotoFile): Boolean {
        return true
    }

    fun onImageValidate(fragmentName: String, photo: PhotoFile): Boolean {
        val rule = photoValidationAction.complyRulesImages(photo.path)
        var isValid = true
        if (rule != null) {
            showMsg(rule.message)
            isValid = false
        }

        return isValid
    }

    fun addItem(photo: PhotoFile) {
        currentSelectedPhotos.add(photo)
        listCurrentPhotos.add(photo)
    }

    override fun shouldHideToolBar() = true

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == PHOTO_SELECTION_REQUEST_CODE) run {
            val finalSelectionFromFolders =
                data?.getSerializableExtra(EXTRA_SELECTED_PHOTO) as LinkedHashSet<PhotoFile>
            setSelectedFromFolderAndNotify(finalSelectionFromFolders)
        } else if (requestCode == TAKING_PHOTO) {
            if (resultCode == Activity.RESULT_OK) {
                if (lastRequestFileToSavePath.isNotEmpty()) {
                    insertIntoGallery()
                }
                addItem(getPhoto(lastRequestFileToSavePath))
                loadPhotoViewModel.loadMedia(this)
            } else {
                isExpectingNewPhoto = false
            }
        }
    }

    private fun getPhoto(path: String): PhotoFile {
        var fullPhotoUrl = ""
        return PhotoFile.Builder()
            .imageId(0)
            .path(path)
            .smallPhotoUrl("")
            .fullPhotoUrl(fullPhotoUrl)
            .photoBackendId(0L)
            .build()
    }

    private fun insertIntoGallery() {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                put(MediaStore.Images.Media.IS_PENDING, 1)
            } else {
                put(MediaStore.MediaColumns.DATA, lastRequestFileToSavePath)
            }
        }
        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.Q) {
            addImageIntoGalleryQAndAboveDevices(values)
        } else {
            addImageIntoGalleryBelowQDevices(values)
        }
    }

    private fun addImageIntoGalleryBelowQDevices(values: ContentValues) {
        requireContext().contentResolver
            .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    }

    private fun addImageIntoGalleryQAndAboveDevices(values: ContentValues) {
        val collection =
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        requireContext().contentResolver
            .insert(collection, values)
    }

    private fun setSelectedFromFolderAndNotify(photoSet: LinkedHashSet<PhotoFile>) {
        currentSelectedPhotos.clear()
        currentSelectedPhotos.addAll(photoSet)

        listCurrentPhotos.clear()
        listCurrentPhotos.addAll(currentSelectedPhotos)

        galleryItemAdapter.notifyDataSetChanged()
        Gallery.pagerCommunicator?.onPreviewItemsUpdated(listCurrentPhotos)
    }

}

fun LinkedHashSet<PhotoFile>.removePhoto(photo: PhotoFile) {
    val iterator = this.iterator()
    while (iterator.hasNext()) {
        val item = iterator.next()
        if (item == photo) {
            iterator.remove()
        }
    }
}

fun LinkedHashSet<PhotoFile>.containsPhoto(photo: PhotoFile): Boolean {
    this.forEach { selectedPhoto ->
        if (photo == selectedPhoto) {
            return true
        }
    }
    return false
}