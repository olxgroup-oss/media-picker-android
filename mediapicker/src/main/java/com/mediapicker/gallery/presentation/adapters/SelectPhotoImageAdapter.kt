package com.mediapicker.gallery.presentation.adapters

import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.mediapicker.gallery.R
import com.mediapicker.gallery.domain.entity.*
import com.mediapicker.gallery.util.AnimationHelper
import kotlinx.android.synthetic.main.item_camera_selection.view.*
import kotlinx.android.synthetic.main.item_photo_selection.view.*
import java.io.File


class SelectPhotoImageAdapter constructor(
    private var listOfGalleryItems: List<IGalleryItem>,
    var listCurrentPhotos: List<PostingDraftPhoto>,
    private val onGalleryItemClickListener: IGalleryItemClickListener,
    private val fromGallery: Boolean = true
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val ITEM_TYPE_PHOTO = 0
        const val ITEM_TYPE_CAMERA = 1
        const val ITEM_TYPE_ALBUM = 2
    }

    fun updateGalleryItems(itemList: List<IGalleryItem>) {
        this.listOfGalleryItems = itemList
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        val item = listOfGalleryItems[position]
        return if (item is PostingDraftPhoto) ITEM_TYPE_PHOTO else if (item is PhotoAlbum) ITEM_TYPE_ALBUM else ITEM_TYPE_CAMERA
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val resId =
            if (viewType == ITEM_TYPE_CAMERA || viewType == ITEM_TYPE_ALBUM) R.layout.item_camera_selection else R.layout.item_photo_selection
        val view = LayoutInflater.from(parent.context).inflate(resId, parent, false)
        return if (viewType == ITEM_TYPE_CAMERA || viewType == ITEM_TYPE_ALBUM) CameraViewHolder(
            view
        ) else PhotoViewHolder(view)
    }

    override fun getItemCount() = listOfGalleryItems.size

    private fun getPosition(photo: PostingDraftPhoto): Int {
        var i = 0
        while (i < listCurrentPhotos.size) {
            if (listCurrentPhotos[i] == photo) {
                return ++i
            }
            i++
        }
        return 0
    }


    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when {
            viewHolder.itemViewType == ITEM_TYPE_CAMERA -> {
                val cameraViewHolder = viewHolder as CameraViewHolder
                cameraViewHolder.itemView.setOnClickListener { v -> onClickCamera() }
                cameraViewHolder.itemView.folderName.text = viewHolder.itemView.context.getString(R.string.label_camera)
            }
            viewHolder.itemViewType == ITEM_TYPE_ALBUM -> {
                val cameraViewHolder = viewHolder as CameraViewHolder

                cameraViewHolder.itemView.setOnClickListener { v -> onGalleryItemClickListener.onFolderItemClick() }
                cameraViewHolder.itemView.folderName.text = viewHolder.itemView.context.getString(R.string.label_folder)
                cameraViewHolder.itemView.img.setImageResource(R.drawable.ic_folder_icon)
            }
            else -> {
                val photoViewHolder = viewHolder as PhotoViewHolder
                photoViewHolder.photo = listOfGalleryItems[position] as PostingDraftPhoto
                photoViewHolder.itemView.imgCoverText.visibility = View.GONE
                if (listCurrentPhotos.contains(photoViewHolder.photo)) {
                    photoViewHolder.itemView.white_overlay.visibility = View.VISIBLE
                    photoViewHolder.itemView.imgSelectedText.text =
                        getPosition(photoViewHolder.photo).toString()
                    if (listCurrentPhotos.indexOf(photoViewHolder.photo) == 0) {
                        photoViewHolder.itemView.imgCoverText.visibility = View.VISIBLE
                    }
                    photoViewHolder.itemView.scaleX = AnimationHelper.SELECTED_SCALE
                    photoViewHolder.itemView.scaleY = AnimationHelper.SELECTED_SCALE

                } else {
                    photoViewHolder.itemView.imgSelectedText.text = ""
                    photoViewHolder.itemView.white_overlay.visibility = View.GONE
                    photoViewHolder.itemView.scaleX = AnimationHelper.UNSELECTED_SCALE
                    photoViewHolder.itemView.scaleY = AnimationHelper.UNSELECTED_SCALE
                }

                if (photoViewHolder.photo.imageId != photoViewHolder.itemView.tag) {
                    loadImageIntoView(photoViewHolder.photo, photoViewHolder.itemView.cropedImage)
                    photoViewHolder.itemView.tag = photoViewHolder.photo.imageId
                }
                photoViewHolder.itemView.setOnClickListener { v ->
                    val path = photoViewHolder.photo.path
                    val imageId = photoViewHolder.photo.imageId
                    val fullPhotoUrl = photoViewHolder.photo.fullPhotoUrl
                    val photo = PostingDraftPhoto.Builder()
                        .imageId(imageId)
                        .path(path!!)
                        .smallPhotoUrl("")
                        .fullPhotoUrl(fullPhotoUrl!!)
                        .photoBackendId(0L)
                        .action(Action.ADD)
                        .status(Status.PENDING)
                        .build()
                    trackSelectPhotos()
                    handleItemClick(photo, position)
                }
            }
        }
    }

    private fun trackSelectPhotos() {
        /* if(fromGallery){
             trackingContextRepository.value.pictureOrigin = NinjaParamValues.OPEN_GALLERY
         }else{
             trackingContextRepository.value.pictureOrigin = NinjaParamValues.OPEN_FOLDER
         }
         trackingService.value.postingPictureSelect()*/
    }

    private fun handleItemClick(photo: PostingDraftPhoto, position: Int) {
        onGalleryItemClickListener.onPhotoItemClick(photo, position)
    }

    private fun onClickCamera() {
        onGalleryItemClickListener.onCameraIconClick()
    }

    private fun loadImageIntoView(photo: PostingDraftPhoto, imageView: ImageView) {

        val options = RequestOptions()
            .skipMemoryCache(true)
            .fitCenter()
        if (photo.isAlreadyUploaded) {
            Glide.with(imageView.context)
                .load(photo.fullPhotoUrl)
                .apply(options)
                .into(imageView)
        } else if (!photo.path.isNullOrEmpty()) {
            Glide.with(imageView.context)
                .load(Uri.fromFile(File(photo.path!!)))
                .apply(RequestOptions().override(200, 200))
                .addListener(ImageLoadingCallback())
                .into(imageView)
        }
    }

    private inner class ImageLoadingCallback : RequestListener<Drawable> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>?,
            isFirstResource: Boolean
        ): Boolean {

            return false
        }

        override fun onResourceReady(
            resource: Drawable?,
            model: Any?,
            target: Target<Drawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {

            return false
        }
    }
}


internal class CameraViewHolder(private var root: View) : RecyclerView.ViewHolder(root) {


    /*@BindView(R.id.img)
    lateinit var image: ImageView

    @BindView(R.id.folderName)
    lateinit var folderNameTV: TextView

    init {
        ButterKnife.bind(this, root)
    }*/
}

internal class PhotoViewHolder(private var root: View) : RecyclerView.ViewHolder(root) {

    /* @BindView(R.id.img)
     lateinit var image: ImageView
     @BindView(R.id.imgSelectedText)
     lateinit var txtImageNumber: TextView
     @BindView(R.id.imgCoverText)
     lateinit var txtCoverIndicator: TextView
     @BindView(R.id.white_overlay)
     lateinit var whiteOverlay: View


     init {
         ButterKnife.bind(this, root)
     }*/
    lateinit var photo: PostingDraftPhoto

}

interface IGalleryItemClickListener {
    fun onPhotoItemClick(photo: PostingDraftPhoto, position: Int)
    fun onFolderItemClick()
    fun onCameraIconClick()
}

