package com.mediapicker.gallery.presentation.utils

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mediapicker.gallery.presentation.viewmodels.factory.BaseViewModelFactory

inline fun <reified T : ViewModel> Fragment.getFragmentScopedViewModel(noinline creator: (() -> T)? = null): T {
    return if (creator == null)
        ViewModelProvider(this)[T::class.java]
    else
        ViewModelProvider(this, BaseViewModelFactory(creator))[T::class.java]
}

inline fun <reified T : ViewModel> Fragment.getActivityScopedViewModel(noinline creator: (() -> T)? = null): T {
    return if (creator == null)
        ViewModelProvider(this.requireActivity())[T::class.java]
    else
        ViewModelProvider(this.requireActivity(), BaseViewModelFactory(creator))[T::class.java]
}


//inline fun <reified T : ViewModel> FragmentActivity.getActivityScopedViewModel(noinline creator: (() -> T)? = null): T {
//    return if (creator == null)
//        ViewModelProvider(this)[T::class.java]
//    else
//        ViewModelProvider(this, BaseViewModelFactory(creator))[T::class.java]
//}
