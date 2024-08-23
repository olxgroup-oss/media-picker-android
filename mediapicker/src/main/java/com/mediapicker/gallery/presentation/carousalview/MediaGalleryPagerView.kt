package com.mediapicker.gallery.presentation.carousalview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.media.ExifInterface.ORIENTATION_NORMAL
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import com.github.chrisbanes.photoview.PhotoViewAttacher
import com.mediapicker.gallery.databinding.OssMediaGalleryPagerViewBinding
import com.mediapicker.gallery.domain.entity.MediaGalleryEntity
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.Locale
import kotlin.math.floor

open class MediaGalleryPagerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) :
    FrameLayout(context, attrs, defStyleAttr) {

    private val maxImageSize: Int = 1080
    private val imageCompressionPercentage = 75
    private lateinit var adapter: ImagePageAdapter
    private var mediaList = mutableListOf<MediaGalleryEntity>()
    private var onItemClickListener: OnClickListener? = null
    private var pinchPanZoomEnabled = false
    private var isGallery = false
    private var mediaChangeListener: MediaChangeListener? = null

    val binding: OssMediaGalleryPagerViewBinding by lazy {
        OssMediaGalleryPagerViewBinding.inflate(LayoutInflater.from(context), this)
    }

    private val pageChangeListener: ViewPager.OnPageChangeListener = object :
        ViewPager.OnPageChangeListener {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
        }

        override fun onPageSelected(position: Int) {
            val resolvedPosition = adapter.resolveItemPosition(position)
            loadDataBasedOnPosition(resolvedPosition)
            if (mediaChangeListener != null) {
                mediaChangeListener!!.onMediaChanged(resolvedPosition)
            }
        }

        override fun onPageScrollStateChanged(state: Int) {}
    }

    private val leftArrowClickListener = OnClickListener {
        if (currentItem > 0) {
            setSelectedPhoto(currentItem - 1)
        }
    }

    private val rightArrowClickListener = OnClickListener {
        if (currentItem < mediaList.size - 1) {
            setSelectedPhoto(currentItem + 1)
        }
    }

    private fun getLocalImage(pagerImage: MediaGalleryEntity): Bitmap? {
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        var bmp = BitmapFactory.decodeFile(pagerImage.path, options)
        if (bmp != null) {
            bmp = resizeBitmap(bmp, maxImageSize, maxImageSize)
            val bos = ByteArrayOutputStream()
            bmp.compress(Bitmap.CompressFormat.JPEG, imageCompressionPercentage, bos)
        }
        return bmp
    }

    fun resizeBitmap(bitmap: Bitmap?, targetW: Int, targetH: Int): Bitmap? {
        if (targetW > 0 || targetH > 0 && bitmap != null) {
            val photoW = bitmap!!.width
            val photoH = bitmap.height
            val widthRatio = targetW.toFloat() / photoW.toFloat()
            val heightRatio = targetH.toFloat() / photoH.toFloat()
            var finalWidth = floor(photoW * widthRatio.toDouble()).toInt()
            var finalHeight = floor(photoH * widthRatio.toDouble()).toInt()
            if (finalWidth > targetW || finalHeight > targetH) {
                finalWidth = floor(photoW * heightRatio.toDouble()).toInt()
                finalHeight = floor(photoH * heightRatio.toDouble()).toInt()
            }
            return Bitmap.createScaledBitmap(bitmap, finalWidth, finalHeight, true)
        }
        return bitmap
    }

    fun setImages(mediaList: MutableList<MediaGalleryEntity>) {
        this.mediaList = mediaList
        resolvePlaceHolder()
        adapter.notifyDataSetChanged()
        //RTL SUPPORT needed.
        setSelectedPhoto(0)
        if (mediaList != null) {
            loadDataBasedOnPosition(0)
        }
        setViewBasedOnMediaList()
        setNavigationIconVisibilityBasedOnPosition(0)
    }

    fun addMedia(media: MediaGalleryEntity) {
        mediaList.add(media)
        val resolvedPosition = adapter.resolveItemPosition(currentItem)
        setPhotoCount(resolvedPosition + 1, mediaList.size)
        setViewBasedOnMediaList()
        setNavigationIconVisibilityBasedOnPosition(resolvedPosition)
        adapter.notifyDataSetChanged()
    }

    fun removeMedia(media: MediaGalleryEntity) {
        mediaList =
            mediaList.filter { it.mediaId != media.mediaId } as MutableList<MediaGalleryEntity>
        val resolvedPosition = adapter.resolveItemPosition(currentItem)
        setPhotoCount(resolvedPosition + 1, mediaList.size)
        setViewBasedOnMediaList()
        setNavigationIconVisibilityBasedOnPosition(resolvedPosition)
        adapter.notifyDataSetChanged()
    }

    private fun loadDataBasedOnPosition(position: Int) {
        binding.imageLabel.text = ""
        setPhotoCount(position + 1, mediaList.size)
        setNavigationIconVisibilityBasedOnPosition(position)
    }

    private fun setNavigationIconVisibilityBasedOnPosition(position: Int) {
        if (mediaList.size == 0) {
            binding.leftArrow.visibility = View.GONE
            binding.rightArrow.visibility = View.GONE
            return
        }
        if (position == 0) {
            binding.leftArrow.visibility = View.GONE
        } else {
            binding.leftArrow.visibility = View.VISIBLE
        }

        if (position == mediaList.size - 1) {
            binding.rightArrow.visibility = View.GONE
        } else {
            binding.rightArrow.visibility = View.VISIBLE
        }
    }

    private fun setPhotoCount(currentImage: Int, size: Int) {
        if (binding.photoCount != null) {
            if (size > 1) {
                binding.photoCount.visibility = View.VISIBLE
                binding.photoCount.text =
                    String.format(Locale.ENGLISH, " %1\$d / %2\$d ", currentImage, size)
            } else {
                binding.photoCount.visibility = View.GONE
            }
        }
    }

    private fun resolvePlaceHolder() {
        if (isGallery) {
            binding.imagePlaceholder.visibility = View.GONE
        }
    }

    fun setPinchPanZoomEnabled(pinchPanZoom: Boolean) {
        pinchPanZoomEnabled = pinchPanZoom
    }

    fun setOnItemClickListener(listener: OnClickListener?) {
        onItemClickListener = listener
    }

    fun setOnMediaChangeListener(mediaChangeListener: MediaChangeListener?) {
        this.mediaChangeListener = mediaChangeListener
    }

    fun isGallery(): Boolean {
        return isGallery
    }

    fun setIsGallery(isGallery: Boolean) {
        this.isGallery = isGallery
    }

    fun setSelectedPhoto(position: Int) {
        binding.itemImages.currentItem = adapter.resolveItemPosition(position)
    }

    val currentItem: Int
        get() = adapter.resolveItemPosition(binding.itemImages.currentItem)

    val mediaListSize: Int
        get() = mediaList.size

    private inner class ImagePageAdapter : PagerAdapter() {
        private var img: ImageView? = null
        private var attacher: PhotoViewAttacher? = null
        fun resolveItemPosition(position: Int): Int {
            val configuration = resources.configuration
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 &&
                configuration.layoutDirection == ViewCompat.LAYOUT_DIRECTION_RTL
            ) {
                getRtlPosition(position)
            } else {
                position
            }
        }

        private fun getRtlPosition(position: Int): Int {
            return mediaList.size - (position + 1)
        }

        override fun getCount(): Int {
            return if (mediaList == null) 0 else mediaList.size
        }

        override fun getItemPosition(`object`: Any): Int {
            if (`object` == null || (`object` as View).tag !is MediaGalleryEntity) {
                return POSITION_NONE
            }

            val item = `object`.tag as MediaGalleryEntity
            val position: Int = mediaList.indexOf(item)
            return if (position >= 0) {
                position
            } else {
                POSITION_NONE
            }
        }

        override fun instantiateItem(container: ViewGroup, position: Int): View {
            fixImageScaleType(container.context)
            val resolvedPosition = resolveItemPosition(position)
            val pagerImage: MediaGalleryEntity = mediaList[resolvedPosition]
            loadImage(pagerImage)
            resolvePitchPanel()
            container.addView(img, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            return img!!
        }

        private fun resolvePitchPanel() {
            if (pinchPanZoomEnabled) {
                attacher = PhotoViewAttacher(img)
            }
        }

        private fun loadImage(media: MediaGalleryEntity) {
            val imageUrl: String = media.path!!
            binding.imageProgress.visibility = View.VISIBLE
            img?.setOnClickListener(onItemClickListener)
            if (media.isLocalImage) {
                img?.tag = media
                val bmp = getLocalImage(media)
                if (bmp != null) {
                    img?.setImageBitmap(bmp)
                    img?.rotation = getRotationInDegrees(imageUrl)
                }
            } else {
                img?.let {
                    it.setTag(img?.id ?: 0, media)
                    Glide.with(context)
                        .load(imageUrl)
                        .into(it)
                }
            }
        }

        fun getRotationInDegrees(imageUrl: String): Float {
            var exif: ExifInterface? = null
            var rotationInDegrees = 0
            try {
                exif = ExifInterface(imageUrl)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            if (exif != null) {
                val rotation: Int =
                    exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ORIENTATION_NORMAL)
                rotationInDegrees = exifToDegrees(rotation)
            }
            return rotationInDegrees.toFloat()
        }

        private fun exifToDegrees(exifOrientation: Int): Int {
            if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
                return 90
            } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
                return 180
            } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
                return 270
            }
            return 0
        }

        private fun fixImageScaleType(context: Context) {
            if (!isGallery) {
                img = ImageView(context, null)
                img?.scaleType = ImageView.ScaleType.FIT_CENTER
            } else {
                img = PhotoView(context, null)
                img?.scaleType = ImageView.ScaleType.FIT_CENTER
            }
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            val view = `object` as View?
            binding.itemImages.removeView(view)
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }
    }

    interface MediaChangeListener {
        fun onMediaChanged(mediaPosition: Int)
    }

    init {
//        View.inflate(context, R.layout.oss_media_gallery_pager_view, this)
        adapter = ImagePageAdapter()
        binding.itemImages.addOnPageChangeListener(pageChangeListener)
        binding.itemImages.adapter = adapter
        binding.leftArrow.setOnClickListener(leftArrowClickListener)
        binding.rightArrow.setOnClickListener(rightArrowClickListener)
        setViewBasedOnMediaList()
    }

    private fun setViewBasedOnMediaList() {
        navigationArrowVisibility()
        if (mediaList.size > 0) {
            binding.defaultContainer.visibility = View.GONE
            binding.blackBackground.visibility = View.VISIBLE
            binding.galleryPagerGradient.visibility = View.GONE
            binding.itemImages.visibility = View.VISIBLE
        } else {
            binding.defaultContainer.visibility = View.VISIBLE
            binding.blackBackground.visibility = View.GONE
            binding.galleryPagerGradient.visibility = View.GONE
            binding.imageProgress.visibility = View.GONE
            binding.itemImages.visibility = View.GONE
        }
    }

    private fun navigationArrowVisibility() {
        if (mediaList.size > 1) {
            binding.leftArrow.visibility = View.VISIBLE
            binding.rightArrow.visibility = View.VISIBLE
        } else {
            binding.leftArrow.visibility = View.GONE
            binding.rightArrow.visibility = View.GONE
        }
    }
}