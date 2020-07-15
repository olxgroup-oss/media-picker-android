package com.mediapicker.gallery.presentation.activity

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.mediapicker.gallery.R
import com.mediapicker.gallery.presentation.fragments.BaseFragment
import kotlinx.android.synthetic.main.oss_base_fragment_activity.*

abstract class BaseFragmentActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayout())
    }

    @LayoutRes
    fun getLayout() = R.layout.oss_base_fragment_activity

    protected fun setFragment(fragment: BaseFragment, addToBackStack: Boolean = true) {
        try {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.add(container.id, fragment, fragment.javaClass.name)
            if (addToBackStack)
                transaction.addToBackStack(fragment.javaClass.name)
            transaction.commitAllowingStateLoss()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

}