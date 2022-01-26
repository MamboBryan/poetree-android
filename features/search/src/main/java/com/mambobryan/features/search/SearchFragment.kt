package com.mambobryan.features.search

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.mambo.core.viewmodel.MainViewModel
import com.mambobryan.features.search.databinding.FragmentSearchBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search) {

    private val sharedViewModel: MainViewModel by activityViewModels()
    private val viewModel: SearchViewModel by viewModels()

    private val binding by viewBinding(FragmentSearchBinding::bind)
    private val adapter by lazy { SearchAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        viewModel.poems.observe(viewLifecycleOwner) {

            val poems = listOf("Mambo", "Tambo", "Rambo", "Sambo", "Wambo")

            binding.apply {
                layoutPoems.stateError.isVisible = it == null
                layoutPoems.stateLoading.isVisible = it == null
                layoutPoems.stateEmpty.isVisible = it.isEmpty()
                layoutPoems.stateContent.isVisible = it.isNotEmpty()

                adapter.submitList(poems)
            }

        }

    }

    private fun initViews() =
        binding.apply {

            layoutPoems.recyclerView.adapter = adapter
            layoutPoems.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy > 50) {
                        fabSearch.show()
                    } else {
                        fabSearch.hide()
                    }
                    super.onScrolled(recyclerView, dx, dy)
                }
            })

        }

}