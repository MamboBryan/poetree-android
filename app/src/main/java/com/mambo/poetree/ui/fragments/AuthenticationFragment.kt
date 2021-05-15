package com.mambo.poetree.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import coil.load
import com.mambo.poetree.R
import com.mambo.poetree.databinding.FragmentAuthenticationBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding

class AuthenticationFragment : Fragment(R.layout.fragment_authentication) {

    private val binding by viewBinding(FragmentAuthenticationBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val openingPoem =
            "What is it that attracts \n" +
                    "That man can't detract \n" +
                    "His own self from being captured \n" +
                    "By the allure of companions \n" +
                    "To form bonds."

        binding.apply {

            tvPoem.text = openingPoem
            tvPoemAuthor.text = "- kaluki -"

            ivAuthBg.scaleType = ImageView.ScaleType.CENTER_CROP
            ivAuthBg.load(R.drawable.trees) {
                crossfade(true)
                placeholder(R.drawable.trees)
            }

            btnAuthLogin.setOnClickListener { navigateToLogin() }
            btnAuthRegister.setOnClickListener { navigateToRegister() }
        }
    }

    private fun navigateToLogin() {
        findNavController().navigate(R.id.action_authenticationFragment_to_loginFragment)
    }

    private fun navigateToRegister() {
        findNavController().navigate(R.id.action_authenticationFragment_to_registerFragment)
    }
}