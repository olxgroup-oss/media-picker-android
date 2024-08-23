package com.mediapicker.gallery.presentation.fragments

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import com.mediapicker.gallery.Gallery
import com.mediapicker.gallery.R
import com.mediapicker.gallery.presentation.adapters.OnItemClickListener
import com.mediapicker.gallery.presentation.adapters.SelectVideoAdapter
import com.mediapicker.gallery.presentation.utils.getFragmentScopedViewModel
import com.mediapicker.gallery.presentation.viewmodels.LoadVideoViewModel
import com.mediapicker.gallery.presentation.viewmodels.VideoFile
import com.mediapicker.gallery.presentation.viewmodels.VideoItem
import java.io.Serializable

class VideoGridFragment : BaseViewPagerItemFragment(), OnItemClickListener {

    companion object {
        fun getInstance(title: String, listOfSelectedVideos: List<VideoFile>) = VideoGridFragment().apply {
            this.pageTitle = title
            this.arguments = Bundle().apply { putSerializable(EXTRA_SELECTED_VIDEOS, listOfSelectedVideos as Serializable) }
        }
    }

    private val loadVideoViewModel: LoadVideoViewModel by lazy {
        getFragmentScopedViewModel { LoadVideoViewModel(Gallery.galleryConfig) }
    }

    private val adapter: SelectVideoAdapter by lazy {
        SelectVideoAdapter(requireContext(), listOf(), listOfSelectedVideos, this)
    }

    private val listOfSelectedVideos: MutableList<VideoFile> by lazy {
        getVideosFromArguments().toMutableList()
    }

    private fun updateListItems(listOfItem: List<VideoItem>) {
        adapter.listOfItem = listOfItem
        adapter.notifyDataSetChanged()
    }

    override fun getBaseLoadMediaViewModel() = loadVideoViewModel

    override fun getScreenTitle() = getString(R.string.oss_title_tab_video)

    override fun getMediaAdapter() = adapter

    override fun initViewModels() {
        super.initViewModels()
        loadVideoViewModel.getVideoItem().observe(this) {
            updateListItems(it)
        }
        loadVideoViewModel.loadMedia(this)
        bridgeViewModel.recordVideoWithNativeCamera().observe(this
        ) { recordVideoWithNativeCamera() }
    }

    private fun recordVideoWithNativeCamera() {
        Toast.makeText(context, "Need Native Camera ", Toast.LENGTH_LONG).show()
    }

    override fun recordVideo() {
        bridgeViewModel.shouldRecordVideo()
    }

    override fun onVideoItemClick(videoItem: VideoItem) {
        if (videoItem is VideoFile) {
            videoItem.isSelected = (!videoItem.isSelected)
            when {
                listOfSelectedVideos.contains(videoItem) -> listOfSelectedVideos.remove(videoItem)
                listOfSelectedVideos.size < bridgeViewModel.getMaxVideoSelectionLimit() -> listOfSelectedVideos.add(videoItem)
                else -> {
                    showMsg(bridgeViewModel.getMaxVideoLimitErrorResponse())
                }
            }
            bridgeViewModel.setCurrentSelectedVideos(listOfSelectedVideos)
            adapter.notifyDataSetChanged()
        }
    }

    override fun shouldHideToolBar() = true

    override fun onBackPressed() {

    }


}