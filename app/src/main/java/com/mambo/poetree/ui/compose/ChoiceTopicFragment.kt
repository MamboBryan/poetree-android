package com.mambo.poetree.ui.compose

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.mambo.poetree.R
import com.mambo.poetree.databinding.FragmentChoiceChildBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding

class ChoiceTopicFragment : Fragment(R.layout.fragment_choice_child) {

    private val binding by viewBinding(FragmentChoiceChildBinding::bind)
    private val viewModel by viewModels<ComposeViewModel>({ requireParentFragment() })

    private val adapter = TopicAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeEmotionsRecyclerview()

        viewModel.topics.observe(viewLifecycleOwner) { topics ->
            adapter.submitList(topics)
        }

    }

    private fun initializeEmotionsRecyclerview() {
        binding.apply {
            rvChoice.setHasFixedSize(true)
            rvChoice.adapter = adapter
        }

    }
}