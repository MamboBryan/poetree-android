package com.mambobryan.features.comments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.core.view.isVisible
import com.mambobryan.features.comments.databinding.FragmentCommentsBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CommentsFragment : Fragment(R.layout.fragment_comments) {

    private val binding by viewBinding(FragmentCommentsBinding::bind)
    private val viewModel: CommentViewModel by viewModels()

    private val adapter = CommentAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        viewModel.comments.observe(viewLifecycleOwner) {

            val poems = listOf("Mambo", "Tambo", "Rambo", "Sambo", "Wambo")

            binding.layoutComments.apply {
                stateError.isVisible = it == null
                stateLoading.isVisible = it == null
                stateEmpty.isVisible = it.isEmpty()
                stateContent.isVisible = it.isNotEmpty()

                adapter.submitList(poems)
            }

        }

    }

    private fun initViews() = binding.apply {
        layoutComments.recyclerView.adapter = adapter
    }

}