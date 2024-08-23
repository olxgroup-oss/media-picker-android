package com.mediapicker.gallery.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mediapicker.gallery.R
import com.mediapicker.gallery.databinding.OssItemCameraSelectionBinding
import com.mediapicker.gallery.databinding.OssItemVideoSelectionBinding
import com.mediapicker.gallery.presentation.viewmodels.RecordVideoItem
import com.mediapicker.gallery.presentation.viewmodels.VideoFile
import com.mediapicker.gallery.presentation.viewmodels.VideoItem
import com.mediapicker.gallery.util.AnimationHelper

class SelectVideoAdapter(
    val context: Context,
    var listOfItem: List<VideoItem>,
    private val listOfSelectedVideos: MutableList<VideoFile>,
    private val onItemClickListener: OnItemClickListener? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val ITEM_TYPE_RECORD_VIDEO = 0
        const val ITEM_TYPE_VIDEO = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_TYPE_RECORD_VIDEO) {
            val binding: OssItemCameraSelectionBinding = OssItemCameraSelectionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            RecordVideoViewHolder(binding)
        } else {
            val binding: OssItemVideoSelectionBinding = OssItemVideoSelectionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            VideoViewHolder(
                binding
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = listOfItem[position]
        return if (item is RecordVideoItem) ITEM_TYPE_RECORD_VIDEO else ITEM_TYPE_VIDEO
    }

    override fun getItemCount() = listOfItem.size


    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (viewHolder.itemViewType) {
            ITEM_TYPE_RECORD_VIDEO -> {
                val recordVH = viewHolder as RecordVideoViewHolder
                recordVH.binding.img.setImageResource(R.drawable.oss_media_ic_slow_motion_video_black_24dp)
                recordVH.binding.folderName.text =
                    context.getString(R.string.oss_label_record_video)
                recordVH.itemView.setOnClickListener { onItemClickListener?.recordVideo() }
            }

            ITEM_TYPE_VIDEO -> {
                val videoVH = viewHolder as VideoViewHolder
                videoVH.itemView.setOnClickListener {
                    onItemClickListener?.onVideoItemClick(
                        listOfItem[position]
                    )
                }
                videoVH.setData(
                    (listOfItem[position] as VideoFile).apply {
                        this.isSelected = listOfSelectedVideos.contains(this)
                    },
                    findPositionOfSelectedItems(listOfItem[position] as VideoFile)
                )
            }
        }
    }

    private fun findPositionOfSelectedItems(videoFile: VideoFile): Int {
        return listOfSelectedVideos.indexOf(videoFile) + 1
    }
}

interface OnItemClickListener {
    fun recordVideo()
    fun onVideoItemClick(videoItem: VideoItem)
}


internal class RecordVideoViewHolder(val binding: OssItemCameraSelectionBinding) :
    RecyclerView.ViewHolder(binding.root)

internal class VideoViewHolder(private val binding: OssItemVideoSelectionBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun setData(videoItem: VideoFile, selectViewPosition: Int) {
        binding.croppedImage.setImageBitmap(videoItem.thumbnail)
        binding.durationLabel.text = videoItem.getFormattedDuration()
        if (videoItem.isSelected && selectViewPosition != -1) {
            binding.whiteOverlay.visibility = View.VISIBLE
            binding.imgSelectedText.text = "$selectViewPosition"
            if (selectViewPosition == 0) {
                binding.imgCoverText.visibility = View.VISIBLE
            }
            binding.root.scaleX = AnimationHelper.SELECTED_SCALE
            binding.root.scaleY = AnimationHelper.SELECTED_SCALE
        } else {
            binding.imgSelectedText.text = ""
            binding.whiteOverlay.visibility = View.GONE
            binding.root.scaleX = AnimationHelper.UNSELECTED_SCALE
            binding.root.scaleY = AnimationHelper.UNSELECTED_SCALE
        }

    }
}


