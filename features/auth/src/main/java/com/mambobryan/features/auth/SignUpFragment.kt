package com.mambobryan.features.auth

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mambo.core.utils.isValidEmail
import com.mambo.core.utils.isValidPassword
import com.mambobryan.features.auth.databinding.FragmentSignUpBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : Fragment(R.layout.fragment_sign_up) {

    private val binding by viewBinding(FragmentSignUpBinding::bind)
    private val viewModel: AuthViewModel by viewModels({ requireParentFragment() })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            edtSignupEmail.doAfterTextChanged { inputSignupEmail.error = null }
            edtSignupPassword.doAfterTextChanged { inputSignupPassword.error = null }
            edtSignupConfirmPassword.doAfterTextChanged { inputSignupConfirmPassword.error = null }

            btnSignup.setOnClickListener { validateInputs() }
            btnSignupLogin.setOnClickListener { viewModel.onSignUpLoginClicked() }

        }

    }

    private fun validateInputs() {
        binding.apply {

            val email = edtSignupEmail.text.toString()
            val password = edtSignupPassword.text.toString()
            val passwordConfirm = edtSignupConfirmPassword.text.toString()

            var isValid = true

            if (!email.isValidEmail()) {
                inputSignupEmail.error = "Invalid Email"
                isValid = false
            }

            if (password != passwordConfirm) {
                inputSignupPassword.error = "Passwords don't match"
                inputSignupConfirmPassword.error = "Passwords don't match"
                isValid = false
            }

            if (!password.isValidPassword()) {
                val message = "Invalid Password (Should be a minimum of 8 characters and contain A-Za-z0-9)"
                inputSignupPassword.error = message
                inputSignupConfirmPassword.error = message
                isValid = false
            }

            if (isValid) viewModel.onSignUpClicked(email, password)

        }
    }

}