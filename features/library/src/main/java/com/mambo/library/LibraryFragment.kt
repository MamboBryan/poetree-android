package com.mambo.library

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayoutMediator
import com.mambo.core.adapters.ViewPagerAdapter
import com.mambo.library.databinding.FragmentLibraryBinding
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
        }

        setUpViewPager()

        lifecycleScope.launchWhenStarted {
            viewModel.events.collect { event ->
                when (event) {
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

}