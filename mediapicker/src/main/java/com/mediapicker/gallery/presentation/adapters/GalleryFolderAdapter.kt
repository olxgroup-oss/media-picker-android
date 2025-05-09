package com.mediapicker.gallery.presentation.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.CachePolicy
import com.mediapicker.gallery.Gallery
import com.mediapicker.gallery.R
import com.mediapicker.gallery.databinding.OssItemFolderSelectionBinding
import com.mediapicker.gallery.domain.contract.OnItemClickListener
import com.mediapicker.gallery.domain.entity.PhotoAlbum
import com.mediapicker.gallery.domain.entity.PhotoFile
import java.io.File

open class GalleryFolderAdapter(
    val context: Context,
    var listOfFolders: List<PhotoAlbum>,
    val onItemClickListener: OnItemClickListener<PhotoAlbum>? = null
) : RecyclerView.Adapter<FolderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val binding: OssItemFolderSelectionBinding = OssItemFolderSelectionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FolderViewHolder(binding, onItemClickListener)
    }

    override fun getItemCount() = listOfFolders.size


    override fun onBindViewHolder(viewHolder: FolderViewHolder, position: Int) {
        viewHolder.bind(listOfFolders[position])
    }
}


class FolderViewHolder(
    private val binding: OssItemFolderSelectionBinding,
    private val onItemClickListener: OnItemClickListener<PhotoAlbum>?
) :
    RecyclerView.ViewHolder(binding.root) {
    init {
        binding.img.setImageResource(R.drawable.oss_media_ic_folder_icon)
    }

    fun bind(album: PhotoAlbum) {
        binding.root.setOnClickListener { onItemClickListener?.onListItemClick(album) }
        binding.folderName.text = album.name
        binding.folderName.isAllCaps = Gallery.galleryConfig.textAllCaps
        if (album.hasPhotos()) {
            binding.backgroundImage.visibility = View.VISIBLE
            loadImageIntoView(
                album.firstPhoto as PhotoFile,
                binding.backgroundImage
            )
        } else {
            binding.backgroundImage.visibility = View.GONE
        }
    }

    private fun loadImageIntoView(photo: PhotoFile, imageView: ImageView) {
        if (photo.isAlreadyUploaded) {
            photo.path?.let {
                imageView.load(photo.fullPhotoUrl) {
                    diskCachePolicy(CachePolicy.ENABLED)
                }
            }
        } else {
            if (photo.existsPhoto()) {
                photo.path?.let {
                    imageView.load(Uri.fromFile(File(it))) {
                        diskCachePolicy(CachePolicy.ENABLED)
                    }
                }
            } else {
                photo.toString()
            }
        }
    }
}

