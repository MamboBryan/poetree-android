package com.mambobryan.features.account

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.DatePicker
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.work.Data
import coil.load
import com.irozon.alertview.AlertActionStyle
import com.irozon.alertview.AlertStyle
import com.irozon.alertview.AlertView
import com.irozon.alertview.objects.AlertAction
import com.mambo.core.utils.*
import com.mambo.core.work.UploadImageWork
import com.mambobryan.features.account.databinding.FragmentAccountBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.util.*

@AndroidEntryPoint
class AccountFragment : Fragment(R.layout.fragment_account) {

    private val binding by viewBinding(FragmentAccountBinding::bind)
    private val viewModel: AccountViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        lifecycleScope.launchWhenStarted {
            viewModel.events.collect { event ->
                when (event) {
                    is AccountViewModel.AccountEvents.StartImageUpload -> startUploadImage(event.uri)
                    is AccountViewModel.AccountEvents.ShowError -> showErrorMessage(event.message) {}
                    is AccountViewModel.AccountEvents.ShowSuccess -> showSuccess(event.message)
                    AccountViewModel.AccountEvents.HideLoadingDialog -> LoadingDialog.dismiss()
                    AccountViewModel.AccountEvents.ShowLoadingDialog ->
                        LoadingDialog.show(requireContext())
                }
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.userDetails.collectLatest {
                binding.apply {
                    edtUserName.setText(it.name)
                    edtUserEmail.setText(it.email)
                    edtUserDob.setText(it.dateOfBirth)
                    edtUserGender.setText(if (it.gender == 1) "male" else "female")
                    edtUserBio.setText(it.bio)
                    ivUserImage.load(it.image)
                }
            }
        }

    }

    private fun initViews() = binding.apply {
        NavigationUI.setupWithNavController(toolbar, findNavController())
        toolbar.title = "Update Account"

        fabUploadImage.setOnClickListener { openGalleryForImage() }
        btnUpdate.setOnClickListener { updateUserDetails() }
        edtUserDob.setOnClickListener { openDateDialog() }

        viewModel.imageUri.observe(viewLifecycleOwner) { uri ->
            if (uri != null) binding.ivUserImage.load(uri)
        }

        viewModel.dob.observe(viewLifecycleOwner) { date ->
            if (date != null) binding.edtUserDob.setText(fromDateToString(date))
        }

        edtUserName.doAfterTextChanged { username ->
            viewModel.updateUsername(username.toString())
            inputUserName.error = null
        }

        edtUserBio.doAfterTextChanged { bio ->
            viewModel.updateBio(bio.toString())
            inputUserBio.error = null
        }

        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, viewModel.genders)
        edtUserGender.setAdapter(adapter)
        edtUserGender.setOnItemClickListener { _, _, index, _ ->
            viewModel.onGenderSelected(index)
            inputUserGender.error = null
        }
    }

    private fun updateUserDetails() {

        val username = binding.edtUserName.text.toString()
        val email = binding.edtUserEmail.text.toString()
        val about = binding.edtUserBio.text.toString()
        val gender = binding.edtUserGender.text.toString()

        if (username.isBlank()) {
            binding.inputUserName.error = "Invalid username"
            return
        }

        if (email.isBlank() || email.isValidEmail().not()) {
            binding.inputUserEmail.error = "Invalid Email"
            return
        }

        if (about.isBlank() || about.length > 140) {
            binding.inputUserBio.error = "Invalid about"
            return
        }

        if (binding.edtUserDob.text.toString().isBlank()) {
            binding.inputUserGender.error = "Invalid date of birth"
            return
        }

        if (gender.isBlank()) {
            binding.inputUserGender.error = "Invalid gender"
            return
        }

        viewModel.updateUserDetails(
            username = username.trim(),
            email = email.trim(),
            gender = gender,
            about = about
        )

    }

    private fun openGalleryForImage() {

        val startResultImage = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    val data = result.data?.data!!
                    viewModel.onImageSelected(data)
                }
                else -> {
                    showErrorMessage("No Image Selected") { openGalleryForImage() }
                }
            }
        }

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startResultImage.launch(intent)

    }

    private fun openDateDialog() {
        val today = Calendar.getInstance()

        val year = today.get(Calendar.YEAR)
        val month = today.get(Calendar.MONTH)
        val day = today.get(Calendar.DAY_OF_MONTH)

        val maximumDate = Calendar.getInstance()
        maximumDate.set(Calendar.YEAR, year - 15)

        val dialog = DatePickerDialog(
            requireContext(),
            { _: DatePicker, mYear: Int, mMonth: Int, mDay: Int ->

                val selectedDate = Calendar.getInstance()

                selectedDate.set(Calendar.YEAR, mYear)
                selectedDate.set(Calendar.MONTH, mMonth)
                selectedDate.set(Calendar.DAY_OF_MONTH, mDay)

                binding.inputUserDob.error = null

                viewModel.onDateSelected(selectedDate)

            }, year, month, day
        )
        dialog.datePicker.maxDate = maximumDate.timeInMillis
        dialog.show()
    }

    private fun startUploadImage(uri: Uri) {
        val data = Data.Builder()
        data.putString(Constants.KEY_MEDIA_URI, uri.toString())
        UploadImageWork.scheduleWork(requireContext(), data.build())
    }

    private fun showErrorMessage(message: String, retryMethod: () -> Unit) {

        val alert = AlertView(
            title = "Error",
            message = "\n${message.toObliviousHumanLanguage()}\n",
            style = AlertStyle.DIALOG
        )
        alert.addAction(AlertAction("retry", AlertActionStyle.POSITIVE) {
            retryMethod.invoke()
        })
        alert.addAction(AlertAction("dismiss", AlertActionStyle.NEGATIVE) {})
        alert.show(requireActivity() as AppCompatActivity)

    }

    private fun showSuccess(message: String) {

        val alert = AlertView(
            title = "Success",
            message = "\n${message.toObliviousHumanLanguage()}\n",
            style = AlertStyle.DIALOG
        )
        alert.addAction(AlertAction("dismiss", AlertActionStyle.POSITIVE) {
            findNavController().popBackStack()
        })
        alert.show(requireActivity() as AppCompatActivity)

    }

}