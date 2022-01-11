package com.mambo.library

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayoutMediator
import com.mambo.core.adapters.ViewPagerAdapter
import com.mambo.library.databinding.FragmentLibraryBinding
import com.mambobryan.navigation.Destinations
import com.mambobryan.navigation.extensions.getDeeplink
import com.mambobryan.navigation.extensions.navigate
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class LibraryFragment : Fragment(R.layout.fragment_library) {

    private val viewModel: LibraryViewModel by viewModels()
    private val binding by viewBinding(FragmentLibraryBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            toolbarLibrary.title = "Library"
            fabCreatePoem.setOnClickListener { viewModel.onComposeButtonClicked() }
        }

        setUpViewPager()

        lifecycleScope.launchWhenStarted {
            viewModel.events.collect { event ->
                when (event) {
                    LibraryViewModel.LibraryEvent.NavigateToCompose -> navigateToCompose()
                    LibraryViewModel.LibraryEvent.NavigateToPoem -> navigateToPoem()
                    else -> {
                    }
                }
            }
        }

    }

    private fun setUpViewPager() {

        val fragments = arrayListOf(
            PublishedFragment(),
            UnpublishedFragment(),
        )
        val titles = arrayListOf("Published", "Unpublished")

        val adapter = ViewPagerAdapter(fragments, childFragmentManager, lifecycle)

        binding.apply {
            viewPagerLibrary.isUserInputEnabled = true
            viewPagerLibrary.adapter = adapter

            TabLayoutMediator(tabsLibrary, viewPagerLibrary) { tab, position ->
                tab.text = titles[position]
            }.attach()
        }


    }

    private fun navigateToCompose(){
        navigate(getDeeplink(Destinations.COMPOSE))
    }

    private fun navigateToPoem(){
        navigate(getDeeplink(Destinations.POEM))
    }

}