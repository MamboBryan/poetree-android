package com.mambo.poetree.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mambo.poetree.R
import com.mambo.poetree.databinding.FragmentAuthenticationBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding

class AuthenticationFragment : Fragment(R.layout.fragment_authentication) {

    private val binding by viewBinding(FragmentAuthenticationBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnLogin.setOnClickListener { navigateToLogin() }
            btnRegister.setOnClickListener { navigateToRegister() }
        }
    }

    private fun navigateToLogin(){
        findNavController().navigate(R.id.action_authenticationFragment_to_loginFragment)
    }

    private fun navigateToRegister(){
        findNavController().navigate(R.id.action_authenticationFragment_to_registerFragment)
    }
}