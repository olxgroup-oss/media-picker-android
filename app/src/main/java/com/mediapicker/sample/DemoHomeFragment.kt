package com.mediapicker.sample

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.mediapicker.gallery.IGalleryButton
import com.mediapicker.gallery.domain.entity.PhotoFile
import com.mediapicker.gallery.presentation.fragments.DefaultPage
import com.mediapicker.gallery.presentation.fragments.HomeFragment
import com.mediapicker.gallery.presentation.viewmodels.VideoFile
import kotlinx.android.synthetic.main.demo_fragment_main.*
import java.io.Serializable

class DemoHomeFragment: HomeFragment() {

    override fun getLayoutId(): Int {
        return R.layout.demo_fragment_main
    }

    override fun setHomeAsUp(): Boolean =false

    override fun shouldHideToolBar(): Boolean =true

    companion object {
        fun getInstance(
                listOfSelectedPhotos: List<PhotoFile> = emptyList(),
                listOfSelectedVideos: List<VideoFile> = emptyList(),
                defaultPageType: DefaultPage = DefaultPage.PhotoPage
        ): DemoHomeFragment {
            return DemoHomeFragment().apply {
                this.arguments = Bundle().apply {
                    putSerializable(EXTRA_SELECTED_PHOTOS, listOfSelectedPhotos as Serializable)
                    putSerializable(EXTRA_SELECTED_VIDEOS, listOfSelectedVideos as Serializable)
                    putSerializable(EXTRA_DEFAULT_PAGE, defaultPageType)
                }
            }
        }
    }

    override fun getActionButton(): IGalleryButton? = myActionButton as IGalleryButton?
}

class MyGalleryButton : AppCompatButton, IGalleryButton{

    @JvmOverloads
    constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr) {

    }

    override fun setText(text: String) {
       context?.let {
           Toast.makeText(it,"setText()",Toast.LENGTH_SHORT).show()
       }
    }

    override fun setButtonBackground(drawable: Drawable) {
        context?.let {
            Toast.makeText(it,"setButtonBackground()",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onClick() {
        context?.let {
            Toast.makeText(it,"onClick()",Toast.LENGTH_SHORT).show()
        }
    }

    override fun setSelected(selected: Boolean) {
        context?.let {
            Toast.makeText(it,"setSelected() $selected",Toast.LENGTH_SHORT).show()
        }
    }
}