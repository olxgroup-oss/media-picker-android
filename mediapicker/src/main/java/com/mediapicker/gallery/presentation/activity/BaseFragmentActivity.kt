package com.mediapicker.gallery.presentation.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mediapicker.gallery.databinding.OssBaseFragmentActivityBinding
import com.mediapicker.gallery.presentation.fragments.BaseFragment

abstract class BaseFragmentActivity : AppCompatActivity() {

    private val binding: OssBaseFragmentActivityBinding by lazy {
        OssBaseFragmentActivityBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    protected fun setFragment(fragment: BaseFragment, addToBackStack: Boolean = true) {
        try {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.add(binding.container.id, fragment, fragment.javaClass.name)
            if (addToBackStack)
                transaction.addToBackStack(fragment.javaClass.name)
            transaction.commitAllowingStateLoss()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

}