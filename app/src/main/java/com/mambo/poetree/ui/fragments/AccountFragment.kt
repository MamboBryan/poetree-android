package com.mambo.poetree.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.onNavDestinationSelected
import com.google.android.material.tabs.TabLayoutMediator
import com.mambo.poetree.R
import com.mambo.poetree.databinding.FragmentAccountBinding
import com.mambo.poetree.ui.account.AccountOverviewFragment
import com.mambo.poetree.ui.account.PublishedPoemsFragment
import com.mambo.poetree.ui.account.UnpublishedPoemsFragment
import com.mambo.core.adapters.ViewPagerAdapter
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding

class AccountFragment : Fragment(R.layout.fragment_account) {

    private val binding by viewBinding(FragmentAccountBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViewPager()

        binding.apply {

            NavigationUI.setupWithNavController(toolbarAccount, findNavController())
            toolbarAccount.title = "Mambo Bryan"
            toolbarAccount.inflateMenu(R.menu.menu_fragment_poems)
            toolbarAccount.setOnMenuItemClickListener { item ->
                item.onNavDestinationSelected(findNavController()) ||
                        super.onOptionsItemSelected(item)
            }

        }
    }

    private fun setUpViewPager() {

        val fragments = arrayListOf(
            AccountOverviewFragment(),
            PublishedPoemsFragment(),
            UnpublishedPoemsFragment()
        )
        val titles = arrayListOf("Overview", "Published", "Unpublished")

        val adapter = ViewPagerAdapter(fragments, childFragmentManager, lifecycle)

        binding.apply {
            viewPagerAccount.isUserInputEnabled = true
            viewPagerAccount.adapter = adapter

            TabLayoutMediator(tabLayoutAccount, viewPagerAccount) { tab, position ->
                tab.text = titles[position]
            }.attach()
        }


    }


}