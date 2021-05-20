package com.mambo.poetree.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.mambo.poetree.R
import com.mambo.poetree.data.model.HaikuEmotion
import com.mambo.poetree.data.model.HaikuTopic
import com.mambo.poetree.databinding.FragmentDiscoverBinding
import com.mambo.poetree.ui.adapter.HaikuEmotionAdapter
import com.mambo.poetree.ui.adapter.HaikuTopicAdapter
import com.mambo.poetree.ui.discover.DiscoverViewModel
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DiscoverFragment : Fragment(R.layout.fragment_discover),
    HaikuTopicAdapter.OnHaikuTopicClickListener, HaikuEmotionAdapter.OnHaikuEmotionClickListener {

    private val binding by viewBinding(FragmentDiscoverBinding::bind)
    private val viewModel by viewModels<DiscoverViewModel>()

    private val topicAdapter = HaikuTopicAdapter(this)
    private val emotionAdapter = HaikuEmotionAdapter(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

        }

        initializeRecyclerview()

        viewModel.topics.observe(viewLifecycleOwner) { topics ->
            binding.apply {

                layoutRecyclerTopics.stateLoading.isVisible = false
                layoutRecyclerTopics.stateEmpty.isVisible = false
                layoutRecyclerTopics.stateError.isVisible = false
                layoutRecyclerTopics.stateContent.isVisible = false

                when {
                    topics.isEmpty() -> layoutRecyclerTopics.stateEmpty.isVisible = true
                    topics.isNotEmpty() -> layoutRecyclerTopics.stateContent.isVisible = true
                    else -> layoutRecyclerTopics.stateError.isVisible = true
                }

            }

            topicAdapter.submitList(topics)
        }

        viewModel.emotions.observe(viewLifecycleOwner) { emotions ->
            binding.apply {

                layoutRecyclerEmotions.stateLoading.isVisible = false
                layoutRecyclerEmotions.stateEmpty.isVisible = false
                layoutRecyclerEmotions.stateError.isVisible = false
                layoutRecyclerEmotions.stateContent.isVisible = false

                when {
                    emotions.isEmpty() -> layoutRecyclerEmotions.stateEmpty.isVisible = true
                    emotions.isNotEmpty() -> layoutRecyclerEmotions.stateContent.isVisible = true
                    else -> layoutRecyclerEmotions.stateError.isVisible = true
                }

            }

            emotionAdapter.submitList(emotions)
        }

        viewModel.launch()
    }

    private fun initializeRecyclerview() {
        binding.apply {
            /*topics*/
            layoutRecyclerTopics.recyclerView.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            layoutRecyclerTopics.recyclerView.setHasFixedSize(true)
            layoutRecyclerTopics.recyclerView.adapter = topicAdapter

            /*emotions*/
            layoutRecyclerEmotions.recyclerView.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            layoutRecyclerEmotions.recyclerView.setHasFixedSize(true)
            layoutRecyclerEmotions.recyclerView.adapter = emotionAdapter
        }

    }


    override fun onHaikuTopicClicked(topic: HaikuTopic) {
        Snackbar.make(requireView(), topic.name, Snackbar.LENGTH_SHORT).show()
    }

    override fun onHaikuEmotionClicked(emotion: HaikuEmotion) {
        Snackbar.make(requireView(), emotion.title, Snackbar.LENGTH_SHORT).show()
    }

}