package com.mambo.poetree.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.mambo.poetree.R
import com.mambo.poetree.databinding.FragmentRegisterBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding

class RegisterFragment : Fragment(R.layout.fragment_register) {

    private val binding by viewBinding(FragmentRegisterBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            NavigationUI.setupWithNavController(toolbarRegister, findNavController())

            btnRegister.setOnClickListener { finishAccountCreation() }
        }
    }

    private fun finishAccountCreation() {
        val action = RegisterFragmentDirections.actionRegisterFragmentToSetupFragment()
        findNavController().navigate(action)
    }

}