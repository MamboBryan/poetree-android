package com.mambo.poetree.ui.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.snackbar.Snackbar
import com.mambo.poetree.R
import com.mambo.poetree.databinding.FragmentEditBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class EditFragment : Fragment() {

    private var _binding: FragmentEditBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<EditViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        NavigationUI.setupWithNavController(binding.toolbarCreate, findNavController())

        binding.apply {

            toolbarCreate.setOnMenuItemClickListener { item ->

                when (item.itemId) {

                    R.id.menu_item_save -> {
                        viewModel.onSave()
                        true
                    }

                    else -> {
                        false
                    }
                }


            }

            edtTitle.setText(viewModel.poemTitle)
            edtTitle.addTextChangedListener { title ->
                viewModel.poemTitle = title.toString()
            }

            edtContent.setText(viewModel.poemContent)
            edtContent.addTextChangedListener { content ->
                viewModel.poemContent = content.toString()
            }

            setUpEmotionCheck()

        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.editPoemEvent.collect { event ->
                when (event) {
                    is EditViewModel.EditPoemEvent.NavigateBackWithResult -> {
                        binding.root.clearFocus()
                        setFragmentResult(
                            "create_update_request",
                            bundleOf("create_update_result" to event.result)
                        )
                        findNavController().popBackStack()
                    }
                    is EditViewModel.EditPoemEvent.ShowInvalidInputMessage -> {
                        Snackbar.make(requireView(), event.message, Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun setUpEmotionCheck() {

    }
}