package com.mambo.library

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.mambo.ui.databinding.LayoutFragmentGenericBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class LibraryFragment : Fragment(R.layout.layout_fragment_generic) {

    private val viewModel: LibraryViewModel by viewModels()
    private val binding by viewBinding(LayoutFragmentGenericBinding::bind)
    private val adapter by lazy { LibraryAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        lifecycleScope.launchWhenStarted {
            viewModel.events.collect { event ->
                when (event) {
                    else -> {
                    }
                }
            }
        }

        viewModel.poems.observe(viewLifecycleOwner) {

            val poems = listOf("Mambo", "Tambo", "Rambo", "Sambo", "Wambo")

            binding.apply {
                layoutStateGeneric.stateError.isVisible = it == null
                layoutStateGeneric.stateLoading.isVisible = it == null
                layoutStateGeneric.stateEmpty.isVisible = it.isEmpty()
                layoutStateGeneric.stateContent.isVisible = it.isNotEmpty()

                adapter.submitList(poems)
            }

        }

    }

    private fun initViews() {
        binding.apply {
            toolbarGeneric.title = "Library"

            layoutStateGeneric.recyclerView.adapter = adapter
        }
    }

}