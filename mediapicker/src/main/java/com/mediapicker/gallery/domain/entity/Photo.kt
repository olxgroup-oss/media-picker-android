package com.mediapicker.gallery.domain.entity

import java.io.Serializable

data class Photo constructor(val width: Int,  val height: Int, val url: String) : Serializable
