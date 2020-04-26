package com.mediapicker.gallery.domain.contract

interface OnItemClickListener<T>{
    fun onListItemClick(photo:T)
}
