package com.mambobryan.features.onboarding

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mambobryan.features.onboarding.databinding.FragmentReaderBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding

class ReaderFragment : Fragment(R.layout.fragment_reader) {

    private val binding by viewBinding(FragmentReaderBinding::bind)
    private val viewModel by viewModels<OnboardViewModel>({ requireParentFragment() })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            tvReaderSkip.setOnClickListener {
                viewModel.onFinishOnBoardingClicked()
            }
        }
    }
}