package com.mambo.poetree.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.snackbar.Snackbar
import com.mambo.data.Haiku
import com.mambo.poetree.R
import com.mambo.poetree.databinding.FragmentLibraryBinding
import com.mambo.poetree.ui.adapter.HaikuAdapter
import com.mambo.poetree.ui.library.LibraryViewModel
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LibraryFragment : Fragment(R.layout.fragment_library), HaikuAdapter.OnHaikuClickListener {

    private val binding by viewBinding(FragmentLibraryBinding::bind)
    private val viewModel by viewModels<LibraryViewModel>()

    private val adapter = HaikuAdapter(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            NavigationUI.setupWithNavController(toolbar, findNavController())
        }

        initializeRecyclerview()

        viewModel.allPoems.observe(viewLifecycleOwner) { poems ->
            binding.apply {

                layoutCompleteRecycler.stateLoading.isVisible = false
                layoutCompleteRecycler.stateEmpty.isVisible = false
                layoutCompleteRecycler.stateError.isVisible = false
                layoutCompleteRecycler.stateContent.isVisible = false

                when {
                    poems.isEmpty() -> layoutCompleteRecycler.stateEmpty.isVisible = true
                    poems.isNotEmpty() -> layoutCompleteRecycler.stateContent.isVisible = true
                    else -> layoutCompleteRecycler.stateError.isVisible = true
                }

            }

            adapter.submitList(poems)
        }

        viewModel.launch()

    }

    override fun onHaikuClicked(haiku: Haiku) {
        Snackbar.make(requireView(), haiku.title, Snackbar.LENGTH_SHORT).show()
    }

    private fun initializeRecyclerview() {
        binding.apply {
            layoutCompleteRecycler.recyclerView.setHasFixedSize(true)
            layoutCompleteRecycler.recyclerView.adapter = adapter
        }

    }
}