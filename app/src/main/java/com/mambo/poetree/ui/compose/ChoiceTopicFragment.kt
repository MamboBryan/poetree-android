package com.mambo.poetree.ui.compose

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mambo.data.Topic
import com.mambo.poetree.R
import com.mambo.poetree.databinding.FragmentChoiceChildBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChoiceTopicFragment : Fragment(R.layout.fragment_choice_child),
    TopicAdapter.OnTopicClickListener {

    private val binding by viewBinding(FragmentChoiceChildBinding::bind)
    private val viewModel by viewModels<ComposeViewModel>({ requireParentFragment() })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeEmotionsRecyclerview()

    }

    private fun initializeEmotionsRecyclerview() {

        val adapter = TopicAdapter(viewModel.topic, this)

        binding.apply {
            rvChoice.setHasFixedSize(true)
            rvChoice.adapter = adapter
        }

        viewModel.topics.observe(viewLifecycleOwner) { topics ->
            adapter.submitList(topics)
        }

    }

    override fun onTopicSelected(topic: Topic) {
        viewModel.onPoemTopicClicked(topic)
    }
}