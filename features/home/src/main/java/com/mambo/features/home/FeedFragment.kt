package com.mambo.features.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.core.view.isVisible
import com.mambo.features.home.databinding.FragmentFeedBinding
import com.mambobryan.navigation.Destinations
import com.mambobryan.navigation.extensions.getDeeplink
import com.mambobryan.navigation.extensions.navigate
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class FeedFragment : Fragment(R.layout.fragment_feed) {

    private val viewModel: FeedViewModel by viewModels()
    private val binding by viewBinding(FragmentFeedBinding::bind)
    private val adapter = FeedAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        lifecycleScope.launchWhenStarted {
            viewModel.events.collect { event ->
                when (event) {
                    FeedViewModel.FeedEvent.NavigateToProfile -> navigateToProfile()
                    FeedViewModel.FeedEvent.NavigateToCompose -> navigateToCompose()
                    is FeedViewModel.FeedEvent.NavigateToPoem -> navigateToPoem()
                }
            }
        }

        viewModel.poems.observe(viewLifecycleOwner) {

            val poems = listOf("Mambo", "Tambo", "Rambo", "Sambo", "Wambo")

            binding.apply {
                layoutState.stateError.isVisible = it == null
                layoutState.stateLoading.isVisible = it == null
                layoutState.stateEmpty.isVisible = it.isEmpty()
                layoutState.stateContent.isVisible = it.isNotEmpty()

                adapter.submitList(poems)
            }

        }
    }

    private fun initViews() {
        binding.apply {
            imageUser.setOnClickListener { viewModel.onUserImageClicked() }
            btnCreatePoem.setOnClickListener { viewModel.onCreatePoemClicked() }

            layoutState.recyclerView.adapter = adapter
        }

        adapter.setClickListener(object: FeedAdapter.OnFeedPoemClicked{
            override fun onPoemClicked(poem: String) {
                viewModel.onPoemClicked(poem)
            }
        })
    }

    private fun navigateToProfile() {
        val deeplink = getDeeplink(Destinations.PROFILE)
        navigate(deeplink)
    }

    private fun navigateToCompose() {
        val deeplink = getDeeplink(Destinations.COMPOSE)
        navigate(deeplink)
    }

    private fun navigateToPoem(){
        val deeplink = getDeeplink(Destinations.POEM)
        navigate(deeplink)
    }


}