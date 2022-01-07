package com.mambobryan.features.artist

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.mambobryan.features.artist.databinding.FragmentArtistBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArtistFragment : Fragment(R.layout.fragment_artist) {

    val binding by viewBinding(FragmentArtistBinding::bind)
    val viewModel: ArtistViewModel by viewModels()
    val adapter by lazy { ArtistAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        viewModel.poems.observe(viewLifecycleOwner) {

            val poems = listOf(
                "Mambo",
                "Tambo",
                "Rambo",
                "Sambo",
                "Wambo",
                "Mambo",
                "Tambo",
                "Rambo",
                "Sambo",
                "Wambo"
            )

            binding.apply {
                layoutStatePoems.stateError.isVisible = it == null
                layoutStatePoems.stateLoading.isVisible = it == null
                layoutStatePoems.stateEmpty.isVisible = it.isEmpty()
                layoutStatePoems.stateContent.isVisible = it.isNotEmpty()

                adapter.submitList(poems)
            }

        }

    }

    private fun initViews() = binding.apply {
        toolbar.title = "Artist"

        layoutStatePoems.recyclerView.adapter = adapter
        layoutStatePoems.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
            }
        })
    }


}