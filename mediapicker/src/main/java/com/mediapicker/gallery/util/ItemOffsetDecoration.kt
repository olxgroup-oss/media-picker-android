

package com.mediapicker.gallery.util

import android.graphics.Rect
import android.view.View

import androidx.recyclerview.widget.RecyclerView

class ItemOffsetDecoration(private val mItemOffset: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.set(mItemOffset, mItemOffset, mItemOffset, mItemOffset)
    }

}