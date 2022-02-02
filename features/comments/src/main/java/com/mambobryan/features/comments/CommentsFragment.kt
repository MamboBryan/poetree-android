package com.mambobryan.features.comments

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mambo.core.utils.prettyCount
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

        viewModel.content.observe(viewLifecycleOwner) {
            binding.layoutCommentsEdit.apply {

                ivComment.isEnabled = !it.isNullOrEmpty()

                if (it.isNullOrEmpty())
                    ivComment.setColorFilter(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.primary_100
                        ), android.graphics.PorterDuff.Mode.SRC_IN
                    )
                else
                    ivComment.setColorFilter(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorPrimary
                        ), android.graphics.PorterDuff.Mode.SRC_IN
                    )

            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.layoutCommentsEdit.apply {
                edtComment.isEnabled = isLoading.not()
                ivComment.isVisible = isLoading.not()
                progressComment.isVisible = isLoading
            }
        }

    }

    private fun initViews() = binding.apply {
        val count = 200000
        tvCommentsTitle.text = "The Minority View Of Self Image"
        tvCommentsCount.text = "Comments (${prettyCount(count)})"

        layoutComments.recyclerView.adapter = adapter

        layoutCommentsEdit.apply {
//            layoutCommentRoot.isVisible = viewModel.isOnline
            edtComment.doAfterTextChanged { viewModel.onContentUpdated(it.toString()) }
            ivComment.setOnClickListener { viewModel.onCommentSendClicked() }
        }
    }

}