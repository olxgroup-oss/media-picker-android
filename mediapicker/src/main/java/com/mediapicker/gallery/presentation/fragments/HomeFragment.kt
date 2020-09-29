package com.mediapicker.gallery.presentation.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.mediapicker.gallery.GalleryConfig
import com.mediapicker.gallery.Gallery
import com.mediapicker.gallery.R
import com.mediapicker.gallery.domain.entity.PhotoFile
import com.mediapicker.gallery.presentation.activity.GalleryActivity
import com.mediapicker.gallery.presentation.utils.getActivityScopedViewModel
import com.mediapicker.gallery.presentation.utils.getFragmentScopedViewModel
import com.mediapicker.gallery.presentation.viewmodels.BridgeViewModel
import com.mediapicker.gallery.presentation.viewmodels.HomeViewModel
import com.mediapicker.gallery.presentation.viewmodels.VideoFile
import com.mediapicker.gallery.utils.SnackbarUtils
import kotlinx.android.synthetic.main.oss_fragment_main.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions
import java.io.Serializable

@RuntimePermissions
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


    override fun getLayoutId() = R.layout.oss_fragment_main

    override fun getScreenTitle() = getString(R.string.oss_title_home_screen)

    override fun setUpViews() {
        checkPermissionsWithPermissionCheck()
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
            GalleryConfig.MediaType.PhotoWithFolderAndVideo -> {
                setUpWithTabLayout()
            }
            GalleryConfig.MediaType.PhotoWithVideo -> {
                setUpWithTabLayout()
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
        activity?.supportFragmentManager?.popBackStack()
        Gallery.galleryConfig.galleryCommunicator.onPermissionDenied()
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

    override fun onBackPressed() {
        closeIfHostingOnActivity()
        bridgeViewModel.onBackPressed()
    }

    private fun changeActionButtonState(state: Boolean) {
        action_button.isSelected = state
    }

    private fun showError(error: String) {
        view?.let { SnackbarUtils.show(it, error, Snackbar.LENGTH_SHORT) }
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


sealed class DefaultPage : Serializable {
    object PhotoPage : DefaultPage()
    object VideoPage : DefaultPage()
}

class PagerAdapter(fm: FragmentManager, private val fragmentList: List<BaseViewPagerItemFragment>) :
    FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount() = fragmentList.size

    override fun getItem(i: Int) = fragmentList[i]

    override fun getPageTitle(position: Int) = fragmentList[position].pageTitle
}
