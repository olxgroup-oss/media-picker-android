package com.mediapicker.gallery.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.mediapicker.gallery.R
import com.mediapicker.gallery.databinding.OssFragmentBaseBinding
import com.mediapicker.gallery.domain.entity.PhotoFile
import com.mediapicker.gallery.presentation.viewmodels.VideoFile

abstract class BaseFragment : Fragment() {
    //    protected var binding: T? = null
//    <T : ViewBinding>(contentLayoutId: Int)
    companion object {
        const val EXTRA_SELECTED_PHOTOS = "selected_photos"
        const val EXTRA_SELECTED_VIDEOS = "selected_videos"
        const val EXTRA_DEFAULT_PAGE = "extra_default_page"
    }

    lateinit var childView: View

    @Suppress("UNCHECKED_CAST")
    protected fun getPhotosFromArguments(): List<PhotoFile> {
        this.arguments?.let {
            if (it.containsKey(EXTRA_SELECTED_PHOTOS)) {
                return it.getSerializable(EXTRA_SELECTED_PHOTOS) as List<PhotoFile>
            }
        }
        return emptyList()
    }

//    protected abstract fun inflateBiding(inflater: LayoutInflater, container: ViewGroup?): T

    @Suppress("UNCHECKED_CAST")
    protected fun getVideosFromArguments(): List<VideoFile> {
        this.arguments?.let {
            if (it.containsKey(EXTRA_SELECTED_VIDEOS)) {
                return it.getSerializable(EXTRA_SELECTED_VIDEOS) as List<VideoFile>
            }
        }
        return emptyList()
    }

    var ossFragmentBaseBinding: OssFragmentBaseBinding? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ossFragmentBaseBinding = OssFragmentBaseBinding.inflate(inflater, container, false).apply {
            childView = inflater.inflate(getLayoutId(), null)

            baseContainer.addView(childView)
        }
        return ossFragmentBaseBinding?.root
    }

    @LayoutRes
    abstract fun getLayoutId(): Int

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar()
        initViewModels()
        setUpViews()
    }

    @CallSuper
    private fun setToolbar() {
        ossFragmentBaseBinding?.ossCustomTool?.toolbarTitle?.text = getScreenTitle()
        context?.let {
            ossFragmentBaseBinding?.ossCustomTool?.toolbarTitle?.setTextColor(
                ContextCompat.getColor(
                    it,
                    R.color.oss_toolbar_text
                )
            )
        }
        if (setHomeAsUp()) {
            ossFragmentBaseBinding?.ossCustomTool?.toolbarBackButton?.visibility = View.VISIBLE
            ossFragmentBaseBinding?.ossCustomTool?.toolbarBackButton?.setImageResource(
                getHomeAsUpIcon()
            )
        } else {
            ossFragmentBaseBinding?.ossCustomTool?.toolbarBackButton?.visibility = View.GONE
        }
        if (shouldHideToolBar()) {
            ossFragmentBaseBinding?.ossCustomTool?.root?.visibility = View.GONE
        }
        ossFragmentBaseBinding?.ossCustomTool?.toolbarBackButton?.setOnClickListener { onBackPressed() }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    protected fun showToolbar() {
        ossFragmentBaseBinding?.ossCustomTool?.root?.visibility = View.VISIBLE
    }

    protected fun hideToolbar() {
        ossFragmentBaseBinding?.ossCustomTool?.root?.visibility = View.GONE
    }

    abstract fun onBackPressed()

    open fun getHomeAsUpIcon() = R.drawable.oss_media_ic_back

    open fun setHomeAsUp() = false

    abstract fun getScreenTitle(): String

    open fun shouldHideToolBar() = false

    abstract fun setUpViews()

    @CallSuper
    open fun initViewModels() {
    }

    override fun onDestroyView() {
        super.onDestroyView()
        ossFragmentBaseBinding?.baseToolbar?.visibility = View.VISIBLE
        ossFragmentBaseBinding = null
    }
}