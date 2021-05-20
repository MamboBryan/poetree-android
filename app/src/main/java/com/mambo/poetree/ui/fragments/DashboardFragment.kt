package com.mambo.poetree.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.mambo.poetree.R
import com.mambo.poetree.data.model.Haiku
import com.mambo.poetree.databinding.FragmentDashboardBinding
import com.mambo.poetree.ui.adapter.HaikuAdapter
import com.mambo.poetree.ui.compose.ViewPagerAdapter
import com.mambo.poetree.ui.dashboard.DashboardAllFragment
import com.mambo.poetree.ui.dashboard.DashboardPoetsFragment
import com.mambo.poetree.ui.dashboard.DashboardViewModel
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardFragment : Fragment(R.layout.fragment_dashboard), HaikuAdapter.OnHaikuClickListener {

    private val binding by viewBinding(FragmentDashboardBinding::bind)
    private val viewModel by viewModels<DashboardViewModel>()

    private val adapter = HaikuAdapter(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViewPager()

        binding.apply {
            NavigationUI.setupWithNavController(toolBarDashboard, findNavController())
            btnCreatePoem.setOnClickListener {
                Snackbar.make(requireView(), "Create Poem Clicked", Snackbar.LENGTH_LONG).show()
            }
        }

        viewModel.launch()
    }

    private fun setUpViewPager() {

        val fragments = arrayListOf(DashboardAllFragment(), DashboardPoetsFragment())
        val titles = arrayListOf("All", "My Poets")

        val adapter = ViewPagerAdapter(fragments, childFragmentManager, lifecycle)

        binding.apply {
            viewPagerDashboard.isUserInputEnabled = true
            viewPagerDashboard.adapter = adapter

            TabLayoutMediator(tabLayoutDashboard, viewPagerDashboard) { tab, position ->
                tab.text = titles[position]
            }.attach()
        }


    }

    override fun onHaikuClicked(haiku: Haiku) {
        Snackbar.make(requireView(), haiku.title, Snackbar.LENGTH_SHORT).show()
    }


}