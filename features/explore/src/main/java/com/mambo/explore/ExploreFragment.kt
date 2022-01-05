package com.mambo.explore

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.mambo.explore.databinding.FragmentExploreBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExploreFragment : Fragment(R.layout.fragment_explore), TopicsAdapter.OnTopicClickListener {

    private val binding by viewBinding(FragmentExploreBinding::bind)
    private val viewModel: ExploreViewModel by viewModels()

    private val adapter = TopicsAdapter(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        viewModel.topics.observe(viewLifecycleOwner) { it ->

            val topics =
                listOf(
                    "nature",
                    "love",
                    "beauty",
                    "self",
                    "desire",
                    "motivation",
                    "relationships",
                    "death",
                    "spiritual",
                )

            binding.layoutState.apply {
                stateLoading.isVisible = it == null
                stateError.isVisible = it == null
                stateEmpty.isVisible = it.isEmpty()
                stateContent.isVisible = it.isNotEmpty()
            }

            adapter.submitList(topics)

        }
    }

    private fun initViews() {
        binding.apply {

            ivUserImage.setOnClickListener { snack("User Image Clicked") }
            layoutSearch.setOnClickListener { snack("Search clicked") }

            layoutState.tvEmpty.text = "Topics are empty"
            layoutState.tvError.text = "Couldn't load topics!"
//            imageUser.setOnClickListener { viewModel.onUserImageClicked() }
//            btnCreatePoem.setOnClickListener { viewModel.onCreatePoemClicked() }

            layoutState.recyclerView.adapter = adapter
            layoutState.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        }
    }

    private fun snack(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onTopicClicked(topic: String) {

    }

}