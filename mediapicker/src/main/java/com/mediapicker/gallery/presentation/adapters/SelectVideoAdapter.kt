package com.mediapicker.gallery.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mediapicker.gallery.R
import com.mediapicker.gallery.presentation.viewmodels.RecordVideoItem
import com.mediapicker.gallery.presentation.viewmodels.VideoFile
import com.mediapicker.gallery.presentation.viewmodels.VideoItem
import com.mediapicker.gallery.util.AnimationHelper
import kotlinx.android.synthetic.main.oss_item_camera_selection.view.*
import kotlinx.android.synthetic.main.oss_item_video_selection.view.*

class SelectVideoAdapter constructor(
    val context: Context,
    var listOfItem: List<VideoItem>,
    val listOfSelectedVideos: MutableList<VideoFile>,
    private val onItemClickListener: OnItemClickListener? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val ITEM_TYPE_RECORD_VIDEO = 0
        const val ITEM_TYPE_VIDEO = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val resId = if (viewType == ITEM_TYPE_RECORD_VIDEO) R.layout.oss_item_camera_selection else R.layout.oss_item_video_selection
        val view = LayoutInflater.from(parent.context).inflate(resId, parent, false)
        return if (viewType == ITEM_TYPE_RECORD_VIDEO) RecordVideoViewHolder(view) else VideoViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        val item = listOfItem[position]
        return if (item is RecordVideoItem) ITEM_TYPE_RECORD_VIDEO else ITEM_TYPE_VIDEO
    }

    override fun getItemCount() = listOfItem.size


    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when {
            viewHolder.itemViewType == ITEM_TYPE_RECORD_VIDEO -> {
                val recordVH = viewHolder as RecordVideoViewHolder
                recordVH.itemView.img.setImageResource(R.drawable.oss_media_ic_slow_motion_video_black_24dp)
                recordVH.itemView.folderName.text = context.getString(R.string.oss_label_record_video)
                recordVH.itemView.setOnClickListener { onItemClickListener?.recordVideo() }
            }
            viewHolder.itemViewType == ITEM_TYPE_VIDEO -> {
                val videoVH = viewHolder as VideoViewHolder
                videoVH.itemView.setOnClickListener { onItemClickListener?.onVideoItemClick(listOfItem[position]) }
                videoVH.setData(
                    (listOfItem[position] as VideoFile).apply { this.isSelected = listOfSelectedVideos.contains(this) },
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


internal class RecordVideoViewHolder(private val root: View) : RecyclerView.ViewHolder(root) {

}

internal class VideoViewHolder(private val root: View) : RecyclerView.ViewHolder(root) {

    fun setData(videoItem: VideoFile, selectViewPosition: Int) {
        root.croppedImage.setImageBitmap(videoItem.thumbnail)
        root.durationLabel.text = videoItem.getFormatedDuration()
        if (videoItem.isSelected && selectViewPosition != -1) {
            root.white_overlay.visibility = View.VISIBLE
            root.imgSelectedText.text = "$selectViewPosition"
            if (selectViewPosition == 0) {
                root.imgCoverText.visibility = View.VISIBLE
            }
            root.scaleX = AnimationHelper.SELECTED_SCALE
            root.scaleY = AnimationHelper.SELECTED_SCALE
        } else {
            root.imgSelectedText.text = ""
            root.white_overlay.visibility = View.GONE
            root.scaleX = AnimationHelper.UNSELECTED_SCALE
            root.scaleY = AnimationHelper.UNSELECTED_SCALE
        }

    }
}


