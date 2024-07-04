package com.mediapicker.gallery.presentation.adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.mediapicker.gallery.Gallery
import com.mediapicker.gallery.R
import com.mediapicker.gallery.domain.entity.*
import com.mediapicker.gallery.util.AnimationHelper
import kotlinx.android.synthetic.main.oss_item_camera_selection.view.*
import kotlinx.android.synthetic.main.oss_item_photo_selection.view.*
import java.io.File


class SelectPhotoImageAdapter constructor(
    private var listOfGalleryItems: List<IGalleryItem>,
    var listCurrentPhotos: List<PhotoFile>,
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
        return if (item is PhotoFile) ITEM_TYPE_PHOTO else if (item is PhotoAlbum) ITEM_TYPE_ALBUM else ITEM_TYPE_CAMERA
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val resId =
            if (viewType == ITEM_TYPE_CAMERA || viewType == ITEM_TYPE_ALBUM) R.layout.oss_item_camera_selection else R.layout.oss_item_photo_selection
        val view = LayoutInflater.from(parent.context).inflate(resId, parent, false)
        return if (viewType == ITEM_TYPE_CAMERA || viewType == ITEM_TYPE_ALBUM) CameraViewHolder(
            view
        ) else PhotoViewHolder(view)
    }

    override fun getItemCount() = listOfGalleryItems.size

    private fun getPosition(photo: PhotoFile): Int {
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
                val uiConfig = Gallery.galleryConfig.galleryUiConfig
                val cameraViewHolder = viewHolder as CameraViewHolder

                val tile = cameraViewHolder.itemView.findViewById<ConstraintLayout>(R.id.gridTile)
                tile.setBackgroundColor(ContextCompat.getColor(cameraViewHolder.itemView.context, uiConfig.tileColor))

                cameraViewHolder.itemView.setOnClickListener { v -> onClickCamera() }
                cameraViewHolder.itemView.folderName.isAllCaps = Gallery.galleryConfig.textAllCaps
                cameraViewHolder.itemView.folderName.text =
                    viewHolder.itemView.context.getString(R.string.oss_label_camera)
                cameraViewHolder.itemView.img.setImageResource(uiConfig.cameraIcon)
            }
            viewHolder.itemViewType == ITEM_TYPE_ALBUM -> {
                val cameraViewHolder = viewHolder as CameraViewHolder
                val uiConfig = Gallery.galleryConfig.galleryUiConfig

                val tile = cameraViewHolder.itemView.findViewById<ConstraintLayout>(R.id.gridTile)
                tile.setBackgroundColor(ContextCompat.getColor(cameraViewHolder.itemView.context, uiConfig.tileColor))

                cameraViewHolder.itemView.setOnClickListener { v -> onGalleryItemClickListener.onFolderItemClick() }
                cameraViewHolder.itemView.folderName.isAllCaps = Gallery.galleryConfig.textAllCaps
                cameraViewHolder.itemView.folderName.text =
                    viewHolder.itemView.context.getString(R.string.oss_label_folder)
                cameraViewHolder.itemView.img.setImageResource(uiConfig.folderIcon)
            }
            else -> {
                val photoViewHolder = viewHolder as PhotoViewHolder
                photoViewHolder.photoFile = listOfGalleryItems[position] as PhotoFile
                photoViewHolder.itemView.imgCoverText.visibility = View.GONE
                if (listCurrentPhotos.contains(photoViewHolder.photoFile)) {
                    photoViewHolder.itemView.white_overlay.visibility = View.VISIBLE
                    photoViewHolder.itemView.imgSelectedText.text =
                        getPosition(photoViewHolder.photoFile).toString()
                    photoViewHolder.itemView.imgSelectedText.background =
                        photoViewHolder.itemView.context
                            .resources.getDrawable(R.drawable.oss_circle_photo_indicator_selected)
                    photoViewHolder.itemView.scaleX = AnimationHelper.SELECTED_SCALE
                    photoViewHolder.itemView.scaleY = AnimationHelper.SELECTED_SCALE
                    setSelectedPhoto(photoViewHolder)
                } else {
                    photoViewHolder.itemView.imgSelectedText.text = ""
                    photoViewHolder.itemView.imgSelectedText.background =
                        photoViewHolder.itemView.context
                            .resources.getDrawable(R.drawable.oss_circle_photo_indicator)
                    photoViewHolder.itemView.white_overlay.visibility = View.GONE
                    photoViewHolder.itemView.scaleX = AnimationHelper.UNSELECTED_SCALE
                    photoViewHolder.itemView.scaleY = AnimationHelper.UNSELECTED_SCALE

                }
                if (photoViewHolder.photoFile.imageId != photoViewHolder.itemView.tag) {
                    loadImageIntoView(
                        photoViewHolder.photoFile,
                        photoViewHolder.itemView.cropedImage
                    )
                    photoViewHolder.itemView.tag = photoViewHolder.photoFile.imageId
                }
                photoViewHolder.itemView.setOnClickListener { v ->
                    val path = photoViewHolder.photoFile.path
                    val imageId = photoViewHolder.photoFile.imageId
                    val fullPhotoUrl = photoViewHolder.photoFile.fullPhotoUrl
                    val photo = PhotoFile.Builder()
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

    private fun setSelectedPhoto(photoViewHolder: PhotoViewHolder) {
        if (Gallery.galleryConfig.photoTag.shouldShowPhotoTag) {
            photoViewHolder.itemView.imgCoverText.visibility = View.VISIBLE
            photoViewHolder.itemView.imgCoverText.text = Gallery.galleryConfig.photoTag.photoTagText
        } else if (listCurrentPhotos.indexOf(photoViewHolder.photoFile) == 0 && Gallery.galleryConfig.needToShowCover.shouldShowPhotoTag) {
            photoViewHolder.itemView.imgCoverText.visibility = View.VISIBLE
            photoViewHolder.itemView.imgCoverText.text = Gallery.galleryConfig.needToShowCover.photoTagText
        } else {
            photoViewHolder.itemView.imgCoverText.visibility = View.GONE
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

    private fun handleItemClick(photoFile: PhotoFile, position: Int) {
        onGalleryItemClickListener.onPhotoItemClick(photoFile, position)
    }

    private fun onClickCamera() {
        onGalleryItemClickListener.onCameraIconClick()
    }


    private fun loadImageIntoView(photoFile: PhotoFile, imageView: ImageView) {
        val options = RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .fitCenter()
        if (photoFile.isAlreadyUploaded) {
            Glide.with(imageView.context)
                .load(photoFile.fullPhotoUrl)
                .apply(options)
                .into(imageView)
        } else if (!photoFile.path.isNullOrEmpty()) {
            Glide.with(imageView.context)
                .load(Uri.fromFile(File(photoFile.path!!)))
                .thumbnail(0.1f)
                .apply(options)
                .into(imageView)
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
    lateinit var photoFile: PhotoFile

}

interface IGalleryItemClickListener {
    fun onPhotoItemClick(photoFile: PhotoFile, position: Int)
    fun onFolderItemClick()
    fun onCameraIconClick()
}

