package com.mambo.poetree.ui.home

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.onNavDestinationSelected
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.mambo.data.Poem
import com.mambo.poetree.R
import com.mambo.poetree.databinding.FragmentHomeBinding
import com.mambo.poetree.ui.adapter.PoemAdapter
import com.mambo.poetree.ui.poems.PoemsViewModel
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home), PoemAdapter.OnPoemClickListener {

    private val binding by viewBinding(FragmentHomeBinding::bind)
    private val viewModel by viewModels<PoemsViewModel>()

    private val adapter = PoemAdapter(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            toolbarHome.setOnMenuItemClickListener { item ->
                item.onNavDestinationSelected(findNavController()) ||
                        super.onOptionsItemSelected(item)
            }

            NavigationUI.setupWithNavController(toolbarHome, findNavController())

            initializeRecyclerview()

        }

        setFragmentResultListener("create_update_request") { _, bundle ->
            val result = bundle.getInt("create_update_result")
            viewModel.onCreateOrUpdateResult(result)
        }

        viewModel.poems.observe(viewLifecycleOwner) { poems ->
            binding.apply {

                layoutHomeRecycler.stateLoading.isVisible = false
                layoutHomeRecycler.stateEmpty.isVisible = false
                layoutHomeRecycler.stateError.isVisible = false
                layoutHomeRecycler.stateContent.isVisible = false

                when {
                    poems.isEmpty() -> layoutHomeRecycler.stateEmpty.isVisible = true
                    poems.isNotEmpty() -> layoutHomeRecycler.stateContent.isVisible = true
                    else -> layoutHomeRecycler.stateError.isVisible = true
                }

            }

            adapter.submitList(poems)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.poemsEvent.collect { event ->
                when (event) {
                    is PoemsViewModel.PoemsEvent.ShowPoemConfirmationMessage -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_SHORT).show()
                    }

                    is PoemsViewModel.PoemsEvent.ShowUndoDeletePoemMessage -> {
                        val poem = event.poem

                        Snackbar.make(requireView(), "Poem deleted", Snackbar.LENGTH_LONG)
                            .setAction("UNDO") {
                                viewModel.onPoemDeleteUndone(poem)
                            }.show()

                    }

                    is PoemsViewModel.PoemsEvent.NavigateToCreatePoem -> {

                    }

                    is PoemsViewModel.PoemsEvent.NavigateToEditPoem -> {

                    }
                }
            }
        }

    }

    private fun initializeRecyclerview() {
        binding.apply {
            layoutHomeRecycler.recyclerView.setHasFixedSize(true)
            layoutHomeRecycler.recyclerView.adapter = adapter

            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val poem = adapter.currentList[viewHolder.adapterPosition]
                    viewModel.onPoemSwiped(poem)
                }
            }).attachToRecyclerView(layoutHomeRecycler.recyclerView)
        }

    }

    override fun onPoemClicked(poem: Poem) {
        viewModel.onPoemSelected(poem)
    }
}