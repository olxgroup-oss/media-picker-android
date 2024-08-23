package com.mediapicker.gallery.presentation.fragments

import android.view.View
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.mediapicker.gallery.Gallery
import com.mediapicker.gallery.R
import com.mediapicker.gallery.databinding.OssFragmentGalleryBinding
import com.mediapicker.gallery.presentation.utils.getActivityScopedViewModel
import com.mediapicker.gallery.presentation.viewmodels.BridgeViewModel
import com.mediapicker.gallery.presentation.viewmodels.StateData
import com.mediapicker.gallery.presentation.viewmodels.factory.BaseLoadMediaViewModel
import com.mediapicker.gallery.util.ItemOffsetDecoration
import com.mediapicker.gallery.utils.SnackbarUtils

abstract class BaseViewPagerItemFragment : BaseFragment() {

    var pageTitle = ""

    protected val bridgeViewModel: BridgeViewModel by lazy {
        getActivityScopedViewModel {
            BridgeViewModel(
                emptyList(),
                emptyList(),
                Gallery.galleryConfig
            )
        }
    }

    private val ossFragmentGalleryBinding: OssFragmentGalleryBinding? by lazy {
        ossFragmentBaseBinding?.baseContainer?.findViewById<ConstraintLayout>(R.id.parent)
            ?.let { OssFragmentGalleryBinding.bind(it) }
    }


    override fun initViewModels() {
        super.initViewModels()
        bridgeViewModel.getMediaStateLiveData().observe(this) { reloadMedia() }
        getBaseLoadMediaViewModel().getLoadingState().observe(this) { handleLoadingState(it) }
    }

    override fun setUpViews() {
        ossFragmentGalleryBinding?.ossRecycleView?.apply {
            val spacing = resources.getDimensionPixelSize(R.dimen.gallery_item_offset)
            val gridLayoutManager = GridLayoutManager(context, 3)
            this.layoutManager = gridLayoutManager
            this.adapter = getMediaAdapter()
            this.clipToPadding = false
            this.addItemDecoration(ItemOffsetDecoration(spacing))
            this.setHasFixedSize(true)
        }
    }

    abstract fun getMediaAdapter(): RecyclerView.Adapter<RecyclerView.ViewHolder>

    override fun getLayoutId() = R.layout.oss_fragment_gallery

    abstract fun getBaseLoadMediaViewModel(): BaseLoadMediaViewModel

    protected open fun reloadMedia() {
        getBaseLoadMediaViewModel().loadMedia(this)
    }

    private fun handleLoadingState(stateData: StateData) {
        when (stateData) {
            StateData.SUCCESS -> hideProgressBar()
            StateData.LOADING -> showProgressBar()
            StateData.ERROR -> {}
        }
    }

    protected open fun hideProgressBar() {
        ossFragmentGalleryBinding?.progressBar?.visibility = View.GONE
        ossFragmentGalleryBinding?.ossRecycleView?.visibility = View.VISIBLE
    }

    protected open fun showProgressBar() {
        ossFragmentGalleryBinding?.progressBar?.visibility = View.VISIBLE
        ossFragmentGalleryBinding?.ossRecycleView?.visibility = View.GONE
    }

    protected open fun showMsg(msg: String) {
        SnackbarUtils.show(view, msg, Snackbar.LENGTH_LONG)
    }

}