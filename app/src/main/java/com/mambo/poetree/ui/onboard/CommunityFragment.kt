package com.mambo.poetree.ui.onboard

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mambo.poetree.R
import com.mambo.poetree.databinding.FragmentCommunityBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CommunityFragment : Fragment(R.layout.fragment_community) {

    private val binding by viewBinding(FragmentCommunityBinding::bind)
    private val viewModel by viewModels<OnboardViewModel>({ requireParentFragment() })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnCommunityFinish.setOnClickListener {
                viewModel.onFinishOnBoardingClicked()
            }
        }
    }
}