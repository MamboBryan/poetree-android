package com.mambobryan.features.update_password

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.irozon.alertview.AlertActionStyle
import com.irozon.alertview.AlertStyle
import com.irozon.alertview.AlertView
import com.irozon.alertview.objects.AlertAction
import com.mambo.core.utils.LoadingDialog
import com.mambo.core.utils.isValidPassword
import com.mambo.core.utils.toObliviousHumanLanguage
import com.mambobryan.features.update_password.databinding.FragmentUpdatePasswordBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class UpdatePasswordFragment : Fragment(R.layout.fragment_update_password) {

    private val binding by viewBinding(FragmentUpdatePasswordBinding::bind)
    private val viewModel: UpdatePasswordViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        lifecycleScope.launchWhenResumed {
            viewModel.uiState.collectLatest {
                when (it) {
                    is UpdatePasswordViewModel.UpdateEvents.Loading -> {
                        when (it.isLoading) {
                            true -> LoadingDialog.show(requireContext())
                            false -> LoadingDialog.dismiss()
                        }
                    }
                    is UpdatePasswordViewModel.UpdateEvents.Error -> {
                        val alert = AlertView(
                            title = "Error",
                            message = "\n${it.message.toObliviousHumanLanguage()}\n",
                            style = AlertStyle.DIALOG
                        )
                        alert.addAction(AlertAction("dismiss", AlertActionStyle.NEGATIVE) {})
                        alert.show(requireActivity() as AppCompatActivity)
                    }
                    is UpdatePasswordViewModel.UpdateEvents.Success -> {
                        clearInputs()
                        val alert = AlertView(
                            title = "Success",
                            message = "\n${it.message.toObliviousHumanLanguage()}\n",
                            style = AlertStyle.DIALOG
                        )
                        alert.addAction(AlertAction("dismiss", AlertActionStyle.POSITIVE) {
                            findNavController().popBackStack()
                        })
                        alert.show(requireActivity() as AppCompatActivity)
                    }
                    null -> {}
                }
            }
        }

    }

    private fun initViews() = binding.apply {
        NavigationUI.setupWithNavController(toolbar, findNavController())
        toolbar.title = "Update Password"
        binding.btnUpdate.setOnClickListener { updatePassword() }

        edtOldPass.doAfterTextChanged { inputOldPass.error = null }
        edtNewPass.doAfterTextChanged { inputNewPass.error = null }
        edtConfirmPass.doAfterTextChanged { inputConfirmPass.error = null }
    }

    private fun updatePassword() {
        binding.apply {

            var isValidInputs = true

            val oldPass = edtOldPass.text.toString()
            val newPass = edtNewPass.text.toString()
            val confirmPass = edtConfirmPass.text.toString()

            if (oldPass.isBlank()) {
                isValidInputs = false
                inputOldPass.error = "Invalid old password"
            }

            if (newPass.isBlank() || newPass.isValidPassword().not()) {
                isValidInputs = false
                inputNewPass.error =
                    "Invalid new Password (Should be a minimum of 8 characters with number, alphabet and symbol)"
            }

            if (confirmPass != newPass) {
                isValidInputs = false
                inputNewPass.error = "Password's don't match"
                inputConfirmPass.error = "Password's don't match"
            }

            if (isValidInputs)
                viewModel.updatePassword(oldPassword = oldPass, newPassword = newPass)

        }
    }

    private fun clearInputs() {
        binding.apply {
            edtOldPass.setText("")
            edtNewPass.setText("")
            edtConfirmPass.setText("")
        }
    }

}