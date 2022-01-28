package com.mambo.library

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayoutMediator
import com.mambo.core.adapters.ViewPagerAdapter
import com.mambo.core.extensions.onQueryTextChanged
import com.mambo.core.viewmodel.MainViewModel
import com.mambo.library.databinding.FragmentLibraryBinding
import com.mambobryan.navigation.Destinations
import com.mambobryan.navigation.extensions.getDeeplink
import com.mambobryan.navigation.extensions.navigate
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class LibraryFragment : Fragment(R.layout.fragment_library) {

    private val sharedViewModel:MainViewModel by activityViewModels()
    private val viewModel: LibraryViewModel by viewModels()

    private val binding by viewBinding(FragmentLibraryBinding::bind)

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_library, menu)

        val searchItem = menu.findItem(R.id.action_library_search)
        val searchView = searchItem.actionView as SearchView

        searchView.onQueryTextChanged { sharedViewModel.updateLibraryQuery(it) }

        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        binding.apply {
            toolbarLibrary.title = "Library"
            (requireActivity() as AppCompatActivity).setSupportActionBar(toolbarLibrary)
            fabCreatePoem.setOnClickListener { viewModel.onComposeButtonClicked() }
        }

        setUpViewPager()

        lifecycleScope.launchWhenStarted {
            viewModel.events.collect { event ->
                when (event) {
                    LibraryViewModel.LibraryEvent.NavigateToCompose -> navigateToCompose()
                    LibraryViewModel.LibraryEvent.NavigateToPoem -> navigateToPoem()
                }
            }
        }

    }

    private fun setUpViewPager() {

        val fragments = arrayListOf(
            PublishedFragment(),
            UnpublishedFragment(),
        )
        val titles = arrayListOf("Public", "Private")

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