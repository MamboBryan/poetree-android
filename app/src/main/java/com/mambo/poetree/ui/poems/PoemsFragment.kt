package com.mambo.poetree.ui.poems

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.mambo.data.Poem
import com.mambo.poetree.R
import com.mambo.poetree.databinding.FragmentPoemsBinding
import com.mambo.poetree.ui.adapter.PoemAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class PoemsFragment : Fragment(), PoemAdapter.OnPoemClickListener {

    private var _binding: FragmentPoemsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<PoemsViewModel>()
    private val adapter = PoemAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPoemsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            toolbarPoems.inflateMenu(R.menu.menu_fragment_poems)
            toolbarPoems.setOnMenuItemClickListener { item ->
                item.onNavDestinationSelected(findNavController()) ||
                        super.onOptionsItemSelected(item)
            }
            val menu = toolbarPoems.menu

//            val searchItem = menu.findItem(R.id.action_search)
//            val searchView = searchItem.actionView as SearchView

//            searchView.onQueryTextChanged {
//                viewModel.updateQuery(it)
//            }

            initializeRecyclerview()

            btnAdd.setOnClickListener {
                viewModel.onCreatePoemClicked()
            }

        }

        setFragmentResultListener("create_update_request") { _, bundle ->
            val result = bundle.getInt("create_update_result")
            viewModel.onCreateOrUpdateResult(result)
        }

        viewModel.poems.observe(viewLifecycleOwner) { poems ->
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
            rvMain.setHasFixedSize(true)
            rvMain.adapter = adapter

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
            }).attachToRecyclerView(rvMain)
        }

    }

    override fun onPoemClicked(poem: Poem) {
        viewModel.onPoemSelected(poem)
    }

}