
package com.mediapicker.gallery.util

import android.view.View

import androidx.annotation.FloatRange

object AnimationHelper {

    internal val SELECTED_SCALE = .9f
    internal val UNSELECTED_SCALE = 1f

    fun scaleToUnselected(view: View){
        scaleView(view, UNSELECTED_SCALE)
    }

    fun scaleToSelected(view: View){
        scaleView(view, SELECTED_SCALE)
    }

    fun scaleView(view: View, @FloatRange(from = 0.0, to = 1.0) scale: Float) {
        if (view.scaleX != scale || view.scaleY != scale) {
            val duration =
                view.context.resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
            view.animate().scaleX(scale).scaleY(scale).setDuration(duration).start()
        }
    }

}
