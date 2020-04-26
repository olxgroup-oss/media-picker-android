package com.mediapicker.gallery.presentation.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.mediapicker.gallery.presentation.viewmodels.factory.BaseViewModelFactory

inline fun <reified T : ViewModel> Fragment.getFragmentScopedViewModel(noinline creator: (() -> T)? = null): T {
    return if (creator == null)
        ViewModelProviders.of(this).get(T::class.java)
    else
        ViewModelProviders.of(this, BaseViewModelFactory(creator)).get(T::class.java)
}

inline fun <reified T : ViewModel> Fragment.getActivityScopedViewModel(noinline creator: (() -> T)? = null): T {
    return if (creator == null)
        ViewModelProviders.of(this.activity!!).get(T::class.java)
    else
        ViewModelProviders.of(this.activity!!, BaseViewModelFactory(creator)).get(T::class.java)
}


inline fun <reified T : ViewModel> FragmentActivity.getActivityScopedViewModel(noinline creator: (() -> T)? = null): T {
    return if (creator == null)
        ViewModelProviders.of(this).get(T::class.java)
    else
        ViewModelProviders.of(this, BaseViewModelFactory(creator)).get(T::class.java)
}
