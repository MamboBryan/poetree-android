package com.mambo.features.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.mambo.features.home.databinding.FragmentFeedBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class FeedFragment : Fragment(R.layout.fragment_feed) {

    private val viewModel: FeedViewModel by viewModels()
    private val binding by viewBinding(FragmentFeedBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            imageUser.setOnClickListener { viewModel.onUserImageClicked() }
            btnCreatePoem.setOnClickListener { viewModel.onCreatePoemClicked() }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.events.collect { event ->
                when (event) {
                    FeedViewModel.FeedEvent.NavigateToAccountDetails -> {
                    }
                    FeedViewModel.FeedEvent.NavigateToCreatePoem -> {
                        Snackbar
                            .make(requireView(), "Create Poem Clicked!", Snackbar.LENGTH_SHORT)
                            .show()
                    }
                    is FeedViewModel.FeedEvent.NavigateToPoem -> {
                    }
                }
            }
        }
    }

}