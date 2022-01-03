package com.mambo.poetree.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mambo.poetree.R
import com.mambo.poetree.databinding.FragmentSetupBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding

class SetupFragment : Fragment(R.layout.fragment_setup) {

    private val binding by viewBinding(FragmentSetupBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnSetupFinish.setOnClickListener { finishAccountSetup() }
        }
    }

    private fun finishAccountSetup() {

    }

}