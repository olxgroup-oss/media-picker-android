package com.mediapicker.gallery.presentation.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.mediapicker.gallery.Gallery
import com.mediapicker.gallery.R
import com.mediapicker.gallery.databinding.OssItemCameraSelectionBinding
import com.mediapicker.gallery.databinding.OssItemPhotoSelectionBinding
import com.mediapicker.gallery.domain.entity.*
import com.mediapicker.gallery.util.AnimationHelper
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
                val cameraViewHolder = viewHolder as CameraViewHolder
                with(OssItemCameraSelectionBinding.bind(cameraViewHolder.itemView)) {
                    root.setOnClickListener { v -> onClickCamera() }
                    folderName.isAllCaps = Gallery.galleryConfig.textAllCaps
                    folderName.text =
                        viewHolder.itemView.context.getString(R.string.oss_label_camera)
                }

            }

            viewHolder.itemViewType == ITEM_TYPE_ALBUM -> {
                val cameraViewHolder = viewHolder as CameraViewHolder
                with(OssItemCameraSelectionBinding.bind(cameraViewHolder.itemView)) {
                    root.setOnClickListener { v -> onGalleryItemClickListener.onFolderItemClick() }
                    folderName.isAllCaps =
                        Gallery.galleryConfig.textAllCaps
                    folderName.text =
                        viewHolder.itemView.context.getString(R.string.oss_label_folder)
                    img.setImageResource(R.drawable.oss_media_ic_folder_icon)
                }
            }

            else -> {
                val photoViewHolder = viewHolder as PhotoViewHolder

                with(OssItemPhotoSelectionBinding.bind(photoViewHolder.itemView)) {
                    photoViewHolder.photoFile = listOfGalleryItems[position] as PhotoFile
                    imgCoverText.visibility = View.GONE
                    if (listCurrentPhotos.contains(photoViewHolder.photoFile)) {
                        whiteOverlay.visibility = View.VISIBLE
                        imgSelectedText.text =
                            getPosition(photoViewHolder.photoFile).toString()
                        imgSelectedText.background =
                            root.context
                                .resources.getDrawable(R.drawable.oss_circle_photo_indicator_selected)
                        root.scaleX = AnimationHelper.SELECTED_SCALE
                        root.scaleY = AnimationHelper.SELECTED_SCALE
                        setSelectedPhoto(photoViewHolder.photoFile, this)
                    } else {
                        imgSelectedText.text = ""
                        imgSelectedText.background =
                            root.context
                                .resources.getDrawable(R.drawable.oss_circle_photo_indicator)
                        whiteOverlay.visibility = View.GONE
                        root.scaleX = AnimationHelper.UNSELECTED_SCALE
                        root.scaleY = AnimationHelper.UNSELECTED_SCALE

                    }
                    if (photoViewHolder.photoFile.imageId != root.tag) {
                        loadImageIntoView(
                            photoViewHolder.photoFile,
                            this.cropedImage
                        )
                        root.tag = photoViewHolder.photoFile.imageId
                    }
                    root.setOnClickListener { v ->
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
    }

    private fun setSelectedPhoto(
        photoFile: PhotoFile,
        ossItemPhotoSelectionBinding: OssItemPhotoSelectionBinding
    ) {
        if (Gallery.galleryConfig.photoTag.shouldShowPhotoTag) {
            ossItemPhotoSelectionBinding.imgCoverText.visibility = View.VISIBLE
            ossItemPhotoSelectionBinding.imgCoverText.text = Gallery.galleryConfig.photoTag.photoTagText
        } else if (listCurrentPhotos.indexOf(photoFile) == 0 && Gallery.galleryConfig.needToShowCover.shouldShowPhotoTag) {
            ossItemPhotoSelectionBinding.imgCoverText.visibility = View.VISIBLE
            ossItemPhotoSelectionBinding.imgCoverText.text =
                Gallery.galleryConfig.needToShowCover.photoTagText
        } else {
            ossItemPhotoSelectionBinding.imgCoverText.visibility = View.GONE
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

