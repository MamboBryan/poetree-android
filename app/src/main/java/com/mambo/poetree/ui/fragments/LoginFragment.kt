package com.mambo.poetree.ui.fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.mambo.poetree.R
import com.mambo.poetree.databinding.FragmentLoginBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding

class LoginFragment : Fragment(R.layout.fragment_login) {

    private val binding by viewBinding(FragmentLoginBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            NavigationUI.setupWithNavController(toolbarLogin, findNavController())

            btnLogin.setOnClickListener {

                val progressDialog = ProgressDialog(requireContext())
                progressDialog.setTitle("Login")
                progressDialog.setMessage("Please wait ...")
                progressDialog.setCancelable(false)
                progressDialog.show()

                Handler(Looper.getMainLooper())
                    .postDelayed({
                        if (progressDialog.isShowing) progressDialog.dismiss()
                        navigateToHome()
                    }, 2000)

            }
        }
    }

    private fun navigateToHome() {
        /*
        navigates back to landing screen and clears backstack
         */
        val action = LoginFragmentDirections.actionLoginFragmentToLandingFragment()
        findNavController().navigate(action)

    }

}