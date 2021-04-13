package com.mambo.poetree

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.onNavDestinationSelected
import com.mambo.poetree.databinding.FragmentAccountBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding

class AccountFragment : Fragment(R.layout.fragment_account) {

    private val binding by viewBinding(FragmentAccountBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            NavigationUI.setupWithNavController(toolbar, findNavController())
            toolbar.inflateMenu(R.menu.menu_fragment_poems)
            toolbar.setOnMenuItemClickListener { item ->
                item.onNavDestinationSelected(findNavController()) ||
                        super.onOptionsItemSelected(item)
            }

        }
    }


}