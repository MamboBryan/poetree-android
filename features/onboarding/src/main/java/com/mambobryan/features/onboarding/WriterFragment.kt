package com.mambobryan.features.onboarding

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mambobryan.features.onboarding.databinding.FragmentWriterBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding

class WriterFragment : Fragment(R.layout.fragment_writer) {

    private val binding by viewBinding(FragmentWriterBinding::bind)
    private val viewModel by viewModels<OnboardViewModel>({ requireParentFragment() })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            tvWriterSkip.setOnClickListener {
                viewModel.onFinishOnBoardingClicked()
            }
        }
    }
}