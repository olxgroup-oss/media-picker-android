package com.mediapicker.gallery.presentation.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//import com.bumptech.glide.request.RequestOptions
import com.mediapicker.gallery.R
import com.mediapicker.gallery.domain.contract.OnItemClickListener
import com.mediapicker.gallery.domain.entity.PhotoAlbum
import com.mediapicker.gallery.domain.entity.PhotoFile
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.oss_item_folder_selection.view.*
import kotlinx.android.synthetic.main.oss_list_item_gallery_media.view.*
import java.io.File

open class GalleryFolderAdapter constructor(val context: Context, var listOfFolders: List<PhotoAlbum>, val onItemClickListener: OnItemClickListener<PhotoAlbum>?=null) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.oss_item_folder_selection, parent, false)
        return FolderViewHolder(view)
    }

    override fun getItemCount() = listOfFolders.size


    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val vH = viewHolder as FolderViewHolder
        vH.root.setOnClickListener { v -> onItemClickListener?.onListItemClick(listOfFolders[position]) }
        vH.root.folderName.text = listOfFolders[position].name
        val album = listOfFolders[position]
        if (album.hasPhotos()) {
            vH.root.backgroundImage.visibility = View.VISIBLE
            loadImageIntoView(album.firstPhoto as PhotoFile, vH.root.backgroundImage)  //todo shalini: change the type inference
        } else {
            vH.root.backgroundImage.visibility = View.GONE
        }
    }

    private fun loadImageIntoView(photo: PhotoFile, imageView: ImageView) {
      //  val options = RequestOptions()
        if (photo.isAlreadyUploaded) {
            photo.path?.let {
                Picasso.get().load(photo.fullPhotoUrl)
                    .fit()
                    .centerCrop().into(imageView) //  Glide.with(imageView.context).load(photo.fullPhotoUrl).apply(options).into(imageView)
            }
        } else {
            if (photo.existsPhoto()) {
                photo.path?.let {
                    Picasso.get().load(Uri.fromFile(File(it)))
                        .fit()
                        .centerCrop().into(imageView)
                    //  Glide.with(imageView.context).load(Uri.fromFile(File(it))).apply(options).into(imageView)
                }
            } else {
                photo.toString()
            }
        }
    }

}


internal class FolderViewHolder(var root: View) : RecyclerView.ViewHolder(root) {
    init {
        root.img.setImageResource(R.drawable.ic_folder_icon)
    }
}

