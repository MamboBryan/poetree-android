package com.mambobryan.features.auth

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mambo.core.utils.isEmailValid
import com.mambobryan.features.auth.databinding.FragmentSignInBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInFragment : Fragment(R.layout.fragment_sign_in) {

    private val binding by viewBinding(FragmentSignInBinding::bind)
    private val viewModel: AuthViewModel by viewModels({ requireParentFragment() })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            edtSigninEmail.doAfterTextChanged { inputSigninEmail.error = null }
            edtSigninPassword.doAfterTextChanged { inputSigninPassword.error = null }

            btnSignin.setOnClickListener { validateInputs() }
            btnSigninCreate.setOnClickListener { viewModel.onCreateClicked() }

        }
    }

    private fun validateInputs() {
        binding.apply {

            val email = edtSigninEmail.text.toString()
            val password = edtSigninPassword.text.toString()

            var isValid = true

            if (!email.isEmailValid()) {
                inputSigninEmail.error = "Invalid Email"
                isValid = false
            }

            if (password.isEmpty()) {
                inputSigninPassword.error = "Invalid Password"
                isValid = false
            }

            if (isValid)
                viewModel.onSignInClicked(email, password)

        }
    }

}