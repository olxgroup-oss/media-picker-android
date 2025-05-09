package com.mediapicker.gallery.presentation.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.mediapicker.gallery.Gallery
import com.mediapicker.gallery.GalleryConfig
import com.mediapicker.gallery.R
import com.mediapicker.gallery.databinding.OssFragmentMainBinding
import com.mediapicker.gallery.domain.entity.PhotoFile
import com.mediapicker.gallery.presentation.activity.GalleryActivity
import com.mediapicker.gallery.presentation.adapters.PagerAdapter
import com.mediapicker.gallery.presentation.utils.DefaultPage
import com.mediapicker.gallery.presentation.utils.getActivityScopedViewModel
import com.mediapicker.gallery.presentation.utils.getFragmentScopedViewModel
import com.mediapicker.gallery.presentation.viewmodels.BridgeViewModel
import com.mediapicker.gallery.presentation.viewmodels.HomeViewModel
import com.mediapicker.gallery.presentation.viewmodels.VideoFile
import com.mediapicker.gallery.utils.SnackbarUtils
import java.io.Serializable

open class HomeFragment : BaseFragment() {

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

    private val ossFragmentMainBinding: OssFragmentMainBinding? by lazy {
        ossFragmentBaseBinding?.baseContainer?.findViewById<ConstraintLayout>(R.id.constraint_layout_parent)
            ?.let { OssFragmentMainBinding.bind(it) }
    }

    override fun getLayoutId() = R.layout.oss_fragment_main

    override fun getScreenTitle() =
        Gallery.galleryConfig.galleryLabels.homeTitle?.ifBlank { getString(R.string.oss_title_home_screen) }
            ?: getString(R.string.oss_title_home_screen)

    override fun setUpViews() {
        ossFragmentMainBinding?.actionButton?.apply {
            setOnClickListener { onActionButtonClicked() }
            text =
                Gallery.galleryConfig.galleryLabels.homeAction?.ifBlank { getString(R.string.oss_posting_next) }
                    ?: getString(R.string.oss_posting_next)
            isSelected = false
        }
        setUpWithOutTabLayout()
    }

    override fun initViewModels() {
        super.initViewModels()
        bridgeViewModel.getActionState().observe(this) { changeActionButtonState(it) }
        bridgeViewModel.getError().observe(this) { showError(it) }
        bridgeViewModel.getClosingSignal().observe(this) { closeIfHostingOnActivity() }
    }

    private fun closeIfHostingOnActivity() {
        if (requireActivity() is GalleryActivity) {
            requireActivity().finish()
        }
    }

    override fun setHomeAsUp() = true

    override fun onBackPressed() {
        closeIfHostingOnActivity()
        bridgeViewModel.onBackPressed()
    }

    private fun changeActionButtonState(state: Boolean) {
        ossFragmentMainBinding?.actionButton?.isSelected = state
    }

    private fun showError(error: String) {
        view?.let { SnackbarUtils.show(it, error, Snackbar.LENGTH_SHORT) }
    }

    private fun setUpWithOutTabLayout() {
        ossFragmentMainBinding?.tabLayout?.visibility = View.GONE
        PagerAdapter(
            childFragmentManager,
            listOf(
                PhotoGridFragment.getInstance(
                    getString(R.string.oss_title_tab_photo),
                    getPhotosFromArguments()
                )
            )
        ).apply {
            ossFragmentMainBinding?.viewPager?.adapter = this
        }
    }

    private fun openPage() {
        if (defaultPageToOpen is DefaultPage.PhotoPage) {
            ossFragmentMainBinding?.viewPager?.currentItem = 0
        } else {
            ossFragmentMainBinding?.viewPager?.currentItem = 1
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
        ).apply { ossFragmentMainBinding?.viewPager?.adapter = this }
        ossFragmentMainBinding?.tabLayout?.setupWithViewPager(ossFragmentMainBinding?.viewPager)
    }


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
        ): HomeFragment {
            return HomeFragment().apply {
                this.arguments = Bundle().apply {
                    putSerializable(EXTRA_SELECTED_PHOTOS, listOfSelectedPhotos as Serializable)
                    putSerializable(EXTRA_SELECTED_VIDEOS, listOfSelectedVideos as Serializable)
                    putSerializable(EXTRA_DEFAULT_PAGE, defaultPageType)
                }
            }
        }
    }
}
