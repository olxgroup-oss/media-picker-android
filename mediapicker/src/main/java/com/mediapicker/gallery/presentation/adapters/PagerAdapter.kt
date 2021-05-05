package com.mediapicker.gallery.presentation.adapters

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.mediapicker.gallery.presentation.fragments.BaseViewPagerItemFragment

class PagerAdapter(fm: FragmentManager, private val fragmentList: List<BaseViewPagerItemFragment>) :
    FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount() = fragmentList.size

    override fun getItem(i: Int) = fragmentList[i]

    override fun getPageTitle(position: Int) = fragmentList[position].pageTitle
}