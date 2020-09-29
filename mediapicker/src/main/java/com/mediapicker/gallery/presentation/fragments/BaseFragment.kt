package com.mediapicker.gallery.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.mediapicker.gallery.R
import com.mediapicker.gallery.domain.entity.PhotoFile
import com.mediapicker.gallery.presentation.viewmodels.VideoFile
import kotlinx.android.synthetic.main.oss_custom_toolbar.*
import kotlinx.android.synthetic.main.oss_fragment_base.*
import kotlinx.android.synthetic.main.oss_fragment_base.view.*

open abstract class BaseFragment : Fragment() {

    companion object{
        const val EXTRA_SELECTED_PHOTOS = "selected_photos"
        const val EXTRA_SELECTED_VIDEOS = "selected_videos"
        const val EXTRA_DEFAULT_PAGE = "extra_default_page"
    }

    @Suppress("UNCHECKED_CAST")
    protected fun getPhotosFromArguments() : List<PhotoFile>{
        this.arguments?.let {
            if(it.containsKey(EXTRA_SELECTED_PHOTOS)){
                return it.getSerializable(EXTRA_SELECTED_PHOTOS) as List<PhotoFile>
            }
        }
        return emptyList()
    }

    @Suppress("UNCHECKED_CAST")
    protected fun getVideosFromArguments() : List<VideoFile>{
       this.arguments?.let {
            if(it.containsKey(EXTRA_SELECTED_VIDEOS)){
                return it.getSerializable(EXTRA_SELECTED_VIDEOS) as List<VideoFile>
            }
        }
        return  emptyList()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.oss_fragment_base, container, false).apply {
            baseContainer.addView(inflater.inflate(getLayoutId(),null))
        }
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
        toolbarTitle.text = getScreenTitle()
        toolbarTitle.setTextColor(context!!.resources!!.getColor(R.color.oss_toolbar_text))
        if(setHomeAsUp()){
            toolbarBackButton.visibility = View.VISIBLE
            toolbarBackButton.setImageResource(getHomeAsUpIcon())
        }else{
            toolbarBackButton.visibility = View.GONE
        }
        if(shouldHideToolBar()){
            toolbarView.visibility = View.GONE
        }
        toolbarBackButton.setOnClickListener { onBackPressed() }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if(item.itemId == android.R.id.home){
            onBackPressed()
            return true
        }else{
            super.onOptionsItemSelected(item)
        }

    }

    abstract fun onBackPressed()

    open fun getHomeAsUpIcon()  = R.drawable.oss_media_ic_back

    open fun setHomeAsUp()  = false

    abstract fun getScreenTitle() : String

    open fun shouldHideToolBar()  = false

    abstract fun setUpViews()

    @CallSuper
    open fun initViewModels(){}

    override fun onDestroyView() {
        super.onDestroyView()
        baseToolbar.visibility = View.VISIBLE
    }
}