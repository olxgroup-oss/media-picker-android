package com.mediapicker.gallery.domain.action

import com.mediapicker.gallery.domain.entity.Validation

class RuleAction(private val mediaValidation: Validation) {


    fun getFirstFailingMessage(selectedMediaSizes: Pair<Int, Int>): String {
        val photoRulePass = validationPhotoRules(selectedSize = selectedMediaSizes.first)
        val videoRulePass = validationVideoRules(selectedSize = selectedMediaSizes.second)
        return if (photoRulePass.isNotEmpty()) photoRulePass else videoRulePass
    }

    fun shouldEnableActionButton(selectedMediaSizes: Pair<Int, Int>): Boolean {
        val photoRulePass = validationPhotoRules(selectedSize = selectedMediaSizes.first)
        val videoRulePass = validationVideoRules(selectedSize = selectedMediaSizes.second)
        return (photoRulePass.isEmpty() && videoRulePass.isEmpty())
    }

    fun shouldEnableActionButton(selectedMediaSizes: Int): Boolean {
        val photoRulePass = validationPhotoRules(selectedSize = selectedMediaSizes)
        return (photoRulePass.isEmpty())
    }

    private fun validationPhotoRules(selectedSize: Int): String {
        val photoMinRule = mediaValidation.getMinPhotoSelectionRule()
        val photoMaxRule = mediaValidation.getMaxPhotoSelectionRule()
        return when {
            photoMinRule.minSelectionLimit > selectedSize -> photoMinRule.message
            photoMaxRule.maxSelectionLimit < selectedSize -> photoMaxRule.message
            else -> ""
        }
    }


    private fun validationVideoRules(selectedSize: Int): String {
        val videoMinRule = mediaValidation.getMinVideoSelectionRule()
        val videoMaxRule = mediaValidation.getMaxVideoSelectionRule()
        return when {
            videoMinRule.minSelectionLimit > selectedSize -> videoMinRule.message
            videoMaxRule.maxSelectionLimit < selectedSize -> videoMaxRule.message
            else -> ""
        }
    }

}
