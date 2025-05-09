package com.mediapicker.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mediapicker.sample.databinding.FragmentStepBinding

class StepFragment : Fragment() {

    private lateinit var binding: FragmentStepBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStepBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.actionButton.setOnClickListener {
            if (activity is MainActivity) {
                (activity as MainActivity).jumpToGallery()
            }
        }
    }
}
