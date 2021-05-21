package com.mambo.poetree.ui.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mambo.poetree.databinding.FragmentCommentsBinding
import com.mambo.poetree.ui.adapter.HaikuCommentAdapter
import com.mambo.poetree.ui.read.ReadViewModel
import com.mambo.poetree.utils.setupFullHeight
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CommentsFragment : BottomSheetDialogFragment() {

    companion object {

        const val TAG = "CommentsSheetDialogFragment"

    }

    private var _binding: FragmentCommentsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<ReadViewModel>({ requireParentFragment() })

    private val adapter = HaikuCommentAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCommentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val haiku = viewModel.haiku!!

        binding.apply {
            toolbarComments.title = haiku.title
        }

        initializeRecyclerview()

        viewModel.comments.observe(viewLifecycleOwner) { comments ->
            binding.apply {

                layoutCompleteRecycler.stateLoading.isVisible = false
                layoutCompleteRecycler.stateEmpty.isVisible = false
                layoutCompleteRecycler.stateError.isVisible = false
                layoutCompleteRecycler.stateContent.isVisible = false

                when {
                    comments.isEmpty() -> layoutCompleteRecycler.stateEmpty.isVisible = true
                    comments.isNotEmpty() -> layoutCompleteRecycler.stateContent.isVisible = true
                    else -> layoutCompleteRecycler.stateError.isVisible = true
                }

            }

            adapter.submitList(comments)
        }

    }

    private fun initializeRecyclerview() {
        binding.apply {
            layoutCompleteRecycler.recyclerView.setHasFixedSize(true)
            layoutCompleteRecycler.recyclerView.adapter = adapter
        }

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.setOnShowListener {

            val bottomSheetDialog = it as BottomSheetDialog

            val parentLayout =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { dialog ->

                setupFullHeight(dialog)

                val behaviour = BottomSheetBehavior.from(dialog)
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
                behaviour.isDraggable = false
                behaviour.isHideable = false
            }
        }
        return dialog
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }
}