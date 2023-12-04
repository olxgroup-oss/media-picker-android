package com.mediapicker.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mediapicker.sample.databinding.FragmentStepBinding

class StepFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_step, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(FragmentStepBinding.bind(view)) {
            actionButton.setOnClickListener {
                if (activity is MainActivity) {
                    (activity as MainActivity).jumpToGallery()
                }
            }
        }
    }
}
