package com.mediapicker.gallery.presentation.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.mediapicker.gallery.Gallery
import com.mediapicker.gallery.GalleryConfig
import com.mediapicker.gallery.R
import com.mediapicker.gallery.domain.contract.GalleryPagerCommunicator
import com.mediapicker.gallery.domain.entity.GalleryViewMediaType
import com.mediapicker.gallery.domain.entity.MediaGalleryEntity
import com.mediapicker.gallery.domain.entity.PhotoFile
import com.mediapicker.gallery.presentation.activity.GalleryActivity
import com.mediapicker.gallery.presentation.activity.MediaGalleryActivity
import com.mediapicker.gallery.presentation.adapters.PagerAdapter
import com.mediapicker.gallery.presentation.carousalview.CarousalActionListener
import com.mediapicker.gallery.presentation.carousalview.MediaGalleryView
import com.mediapicker.gallery.presentation.utils.DefaultPage
import com.mediapicker.gallery.presentation.utils.getActivityScopedViewModel
import com.mediapicker.gallery.presentation.utils.getFragmentScopedViewModel
import com.mediapicker.gallery.presentation.viewmodels.BridgeViewModel
import com.mediapicker.gallery.presentation.viewmodels.HomeViewModel
import com.mediapicker.gallery.presentation.viewmodels.VideoFile
import com.mediapicker.gallery.utils.SnackbarUtils
import kotlinx.android.synthetic.main.oss_custom_toolbar.*
import kotlinx.android.synthetic.main.oss_fragment_carousal.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnNeverAskAgain
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions
import java.io.Serializable
import java.util.*

@RuntimePermissions
open class PhotoCarousalFragment : BaseFragment(), GalleryPagerCommunicator,
    MediaGalleryView.OnGalleryItemClickListener {

    private val PHOTO_PREVIEW = 43475

    private val homeViewModel: HomeViewModel by lazy {
        getFragmentScopedViewModel { HomeViewModel(Gallery.galleryConfig) }
    }

    private val bridgeViewModel: BridgeViewModel by lazy {
        getActivityScopedViewModel {
            BridgeViewModel(
                getPhotosFromArguments(),
                getVideosFromArguments(),
                Gallery.galleryConfig
            )
        }
    }

    private val defaultPageToOpen: DefaultPage by lazy {
        getPageFromArguments()
    }


    override fun getLayoutId() = R.layout.oss_fragment_carousal

    override fun getScreenTitle() = if(Gallery.galleryConfig.galleryLabels.homeTitle.isNotBlank())
        Gallery.galleryConfig.galleryLabels.homeTitle
    else
        getString(R.string.oss_title_home_screen)

    override fun setUpViews() {
        checkPermissionsWithPermissionCheck()
        Gallery.pagerCommunicator = this

        if(Gallery.galleryConfig.showPreviewCarousal.showCarousal) {
            mediaGalleryViewContainer.visibility = View.VISIBLE
            mediaGalleryView.setOnGalleryClickListener(this)
            if (Gallery.galleryConfig.showPreviewCarousal.imageId != 0) {
                mediaGalleryView.updateDefaultPhoto(Gallery.galleryConfig.showPreviewCarousal.imageId)
            }
            if (Gallery.galleryConfig.showPreviewCarousal.previewText != 0) {
                mediaGalleryView.updateDefaultText(Gallery.galleryConfig.showPreviewCarousal.previewText)
            }
        }

        toolbarTitle.isAllCaps = Gallery.galleryConfig.textAllCaps
        action_button.isAllCaps = Gallery.galleryConfig.textAllCaps
        action_button.text = if(Gallery.galleryConfig.galleryLabels.homeAction.isNotBlank())
            Gallery.galleryConfig.galleryLabels.homeAction
        else
            getString(R.string.oss_posting_next)
    }

    @NeedsPermission(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun checkPermissions() {
        when (homeViewModel.getMediaType()) {
            GalleryConfig.MediaType.PhotoOnly -> {
                setUpWithOutTabLayout()
            }
            GalleryConfig.MediaType.PhotoWithFolderOnly -> {
                setUpWithOutTabLayout()
            }
            GalleryConfig.MediaType.PhotoWithoutCameraFolderOnly -> {
                setUpWithOutTabLayout()
            }
            else -> {
                setUpWithOutTabLayout()
            }
        }
        openPage()
        action_button.isSelected = false
        action_button.setOnClickListener { onActionButtonClicked() }
    }


    //todo change this logic
    @OnPermissionDenied(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun onPermissionDenied() {
       // activity?.supportFragmentManager?.popBackStack()
        Gallery.galleryConfig.galleryCommunicator?.onPermissionDenied()
    }

    fun addMediaForPager(mediaGalleryEntity: MediaGalleryEntity) {
        mediaGalleryView.addMediaForPager(mediaGalleryEntity)
    }

    fun removeMediaFromPager(mediaGalleryEntity: MediaGalleryEntity) {
        mediaGalleryView.removeMediaFromPager(mediaGalleryEntity)
    }

    @OnNeverAskAgain(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun showNeverAskAgainPermission() {
       //. Toast.makeText(context, R.string.oss_permissions_denied_attach_image, Toast.LENGTH_LONG).show()
        Gallery.galleryConfig.galleryCommunicator?.onNeverAskPermissionAgain()
    }

    @SuppressLint("NeedOnRequestPermissionsResult")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    override fun initViewModels() {
        super.initViewModels()
        bridgeViewModel.getActionState().observe(this, Observer { changeActionButtonState(it) })
        bridgeViewModel.getError().observe(this, Observer { showError(it) })
        bridgeViewModel.getClosingSignal().observe(this, Observer { closeIfHostingOnActivity() })
    }

    private fun closeIfHostingOnActivity() {
        if(requireActivity() is GalleryActivity){
           requireActivity().finish()
        }
    }

    override fun setHomeAsUp() = true

    fun setActionButtonLabel(label: String) {
        action_button.text = label
    }

    fun setCarousalActionListener(carousalActionListener: CarousalActionListener?) {
        Gallery.carousalActionListener = carousalActionListener
    }

    override fun onBackPressed() {
        closeIfHostingOnActivity()
        bridgeViewModel.onBackPressed()
    }

    private fun changeActionButtonState(state: Boolean) {
        action_button.isSelected = state
    }

    private fun showError(error: String) {
        view?.let { SnackbarUtils.show(it, error, Snackbar.LENGTH_LONG) }
    }

    private fun setUpWithOutTabLayout() {
        tabLayout.visibility = View.GONE
        PagerAdapter(
            childFragmentManager,
            listOf(
                PhotoGridFragment.getInstance(
                    getString(R.string.oss_title_tab_photo),
                    getPhotosFromArguments()
                )
            )
        ).apply {
            viewPager.adapter = this
        }
    }

    private fun openPage() {
        if (defaultPageToOpen is DefaultPage.PhotoPage) {
            viewPager.currentItem = 0
        } else {
            viewPager.currentItem = 1
        }
    }

    private fun onActionButtonClicked() {
        bridgeViewModel.complyRules()
    }

    private fun setUpWithTabLayout() {
        PagerAdapter(
            childFragmentManager, listOf(
                PhotoGridFragment.getInstance(
                    getString(R.string.oss_title_tab_photo),
                    getPhotosFromArguments()
                ),
                VideoGridFragment.getInstance(
                    getString(R.string.oss_title_tab_video),
                    getVideosFromArguments()
                )
            )
        ).apply { viewPager.adapter = this }
        tabLayout.setupWithViewPager(viewPager)
    }


    @Suppress("UNCHECKED_CAST")
    private fun getPageFromArguments(): DefaultPage {
        this.arguments?.let {
            if (it.containsKey(EXTRA_DEFAULT_PAGE)) {
                return it.getSerializable(EXTRA_DEFAULT_PAGE) as DefaultPage
            }
        }
        return DefaultPage.PhotoPage
    }

    fun reloadMedia() {
        bridgeViewModel.reloadMedia()
    }

    companion object {
        fun getInstance(
            listOfSelectedPhotos: List<PhotoFile> = emptyList(),
            listOfSelectedVideos: List<VideoFile> = emptyList(),
            defaultPageType: DefaultPage = DefaultPage.PhotoPage
        ): PhotoCarousalFragment {
            return PhotoCarousalFragment().apply {
                this.arguments = Bundle().apply {
                    putSerializable(EXTRA_SELECTED_PHOTOS, listOfSelectedPhotos as Serializable)
                    putSerializable(EXTRA_SELECTED_VIDEOS, listOfSelectedVideos as Serializable)
                    putSerializable(EXTRA_DEFAULT_PAGE, defaultPageType)
                }
            }
        }
    }

    override fun onItemClicked(photoFile: PhotoFile, isSelected: Boolean) {
        if (isSelected) {
            if(Gallery.galleryConfig.showPreviewCarousal.addImage) {
                addMediaForPager(getMediaEntity(photoFile))
            }
        } else {
            if(Gallery.galleryConfig.showPreviewCarousal.addImage) {
                removeMediaFromPager(getMediaEntity(photoFile))
            }
        }
    }

    private fun getMediaEntity(photo: PhotoFile): MediaGalleryEntity {
        var path: String? = photo.fullPhotoUrl
        var isLocalImage = false
        if (!TextUtils.isEmpty(photo.path) && photo.path?.contains("/")!!) {
            path = photo.path
            isLocalImage = true
        }
        return MediaGalleryEntity(
            photo.path,
            photo.imageId,
            path,
            isLocalImage,
            GalleryViewMediaType.IMAGE
        )
    }

    private fun convertPhotoFileToMediaGallery(photoList: List<PhotoFile>): ArrayList<MediaGalleryEntity> {
        val mediaList = ArrayList<MediaGalleryEntity>()
        for (photo in photoList) {
            mediaList.add(getMediaEntity(photo))
        }
        return mediaList
    }

    override fun onPreviewItemsUpdated(listOfSelectedPhotos: List<PhotoFile>) {
        if(Gallery.galleryConfig.showPreviewCarousal.addImage) {
            mediaGalleryView.setImagesForPager(convertPhotoFileToMediaGallery(listOfSelectedPhotos))
        }
    }

    override fun onGalleryItemClick(mediaIndex: Int) {
        Gallery.carousalActionListener?.onGalleryImagePreview()
        MediaGalleryActivity.startActivityForResult(
            this, convertPhotoFileToMediaGallery(
                bridgeViewModel.getSelectedPhotos()
            ), mediaIndex, "", PHOTO_PREVIEW
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PHOTO_PREVIEW && view != null) {
            var index = 0
            if (data != null) {
                val bundle = data.extras
                index = bundle!!.getInt("gallery_media_index", 0)
            }
            mediaGalleryView.setSelectedPhoto(index)
        }
    }
}
