package com.mambo.library

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mambo.library.databinding.FragmentGenericLibraryBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UnpublishedFragment : Fragment(R.layout.fragment_generic_library) {

    val viewModel: LibraryViewModel by viewModels({ requireParentFragment() })
    val binding by viewBinding(FragmentGenericLibraryBinding::bind)

    val adapter by lazy { UnpublishedLibraryAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        viewModel.poems.observe(viewLifecycleOwner) {

            val poems = listOf("Mambo", "Tambo", "Rambo", "Sambo", "Wambo")

            binding.apply {
                layoutStateLibrary.stateError.isVisible = it == null
                layoutStateLibrary.stateLoading.isVisible = it == null
                layoutStateLibrary.stateEmpty.isVisible = it.isEmpty()
                layoutStateLibrary.stateContent.isVisible = it.isNotEmpty()

                adapter.submitList(poems)
            }

        }

    }

    private fun initViews() {
        binding.apply {
            layoutStateLibrary.recyclerView.adapter = adapter
        }
    }

}