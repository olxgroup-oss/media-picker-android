package com.mediapicker.gallery.presentation.utils

import com.mediapicker.gallery.Gallery
import com.mediapicker.gallery.domain.entity.PostingDraftPhoto
import com.mediapicker.gallery.domain.entity.Rule
import com.mediapicker.gallery.utils.getBitmapHeight
import com.mediapicker.gallery.utils.getBitmapWidth


class ValidatePhotos {

    fun canAddThisToList(currentNoOfAdded: Int, postingDraftPhoto: PostingDraftPhoto): ValidationResult {
        var canAddThisToList = true
        var exception: Throwable = InValidPhotoException("")

        if (currentNoOfAdded < Gallery.galleryConfig.validation.getMaxPhotoSelectionRule().maxSelectionLimit) {
            val pair = checkIfValidImage(postingDraftPhoto, canAddThisToList)
            canAddThisToList = pair.first
            exception = pair.second
        } else {
            canAddThisToList = false
        }

        return when {
            canAddThisToList -> ValidationResult.Success
            else -> ValidationResult.Failure(exception.message ?: "Error adding Photo to the List.")
        }
    }

    private fun checkIfValidImage(postingDraftPhoto: PostingDraftPhoto, canAddThisToList: Boolean): Pair<Boolean, Throwable> {
        var canAddThisToList1 = canAddThisToList
        var exception: Throwable = InValidPhotoException("")
        val failedRule = complyRulesImages(postingDraftPhoto.path)
        if (failedRule != null) {
            canAddThisToList1 = false
            exception = InValidPhotoException(failedRule.message)
        }
        return Pair(canAddThisToList1, exception)
    }

    private fun complyRulesImages(path: String?): Rule? {
        Gallery.galleryConfig.validation.rules.forEach { rules ->
            if (!complyImageRule(path, rules)) {
                return rules
            }
        }
        return null
    }

    private fun complyImageRule(photoPath: String?, rules: Rule): Boolean {
        var comply = true
        if (photoPath != null) {
            comply = when (rules) {
                is Rule.MinWidth -> getBitmapWidth(photoPath) >= Integer.parseInt(rules.value)
                is Rule.MinHeight -> getBitmapHeight(photoPath) >= Integer.parseInt(rules.value)
                is Rule.MaxRatio -> complyAspectRatio(
                    getBitmapWidth(photoPath),
                    getBitmapHeight(photoPath),
                    java.lang.Double.parseDouble(rules.value)
                )
                else -> true
            }
        }
        return comply
    }

    private fun complyAspectRatio(photoWidth: Int, photoHeight: Int, ruleValue: Double): Boolean {
        var complyAspectRatio = false
        if (photoWidth != 0 && photoHeight != 0) {
            val ratio = if (photoWidth > photoHeight) photoWidth.toDouble() / photoHeight else photoHeight.toDouble() / photoWidth
            complyAspectRatio = ratio <= ruleValue
        }
        return complyAspectRatio
    }

    fun shouldAddThisToList(currentNoOfAdded: Int, postingDraftPhoto: PostingDraftPhoto): Boolean {
        return (currentNoOfAdded <= Gallery.galleryConfig.validation.getMaxPhotoSelectionRule().maxSelectionLimit && validatePhoto(
            postingDraftPhoto
        ))
    }

    private fun validatePhoto(postingDraftPhoto: PostingDraftPhoto) = !postingDraftPhoto.path.isNullOrBlank()

    fun checkIfMimeTypeSupported(mimeType: String): Boolean {
        return mimeType.isEmpty() || listOf("image/png", "image/jpeg", "image/webp").contains(mimeType)
    }
}

class InValidPhotoException(message: String) : Throwable(message)

class MimeTypeNotSupportedException(message: String) : Throwable(message)

sealed class ValidationResult {
    object Success : ValidationResult()

    class Failure(val msg: String) : ValidationResult()
}
