package com.mambobryan.features.setup

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
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.work.Data
import coil.load
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.mambo.core.utils.Constants
import com.mambo.core.utils.LoadingDialog
import com.mambo.core.utils.fromDateToString
import com.mambo.core.work.UploadImageWork
import com.mambobryan.features.setup.databinding.FragmentSetupBinding
import com.mambobryan.navigation.Destinations
import com.mambobryan.navigation.extensions.getDeeplink
import com.mambobryan.navigation.extensions.getNavOptionsPopUpToCurrent
import com.mambobryan.navigation.extensions.navigate
import com.tapadoo.alerter.Alerter
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class SetupFragment : Fragment(R.layout.fragment_setup) {

    private val binding by viewBinding(FragmentSetupBinding::bind)
    private val viewModel: SetupViewModel by viewModels()

    private val startResultImage =
        registerForActivityResult(
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        lifecycleScope.launchWhenStarted {
            viewModel.events.collect { event ->
                when (event) {
                    SetupViewModel.SetupEvent.HideLoadingDialog -> LoadingDialog.dismiss()
                    SetupViewModel.SetupEvent.ShowLoadingDialog -> LoadingDialog.show(requireContext())
                    SetupViewModel.SetupEvent.NavigateToFeeds -> navigateToFeeds()
                    SetupViewModel.SetupEvent.ShowBioError -> {
                        binding.inputUserBio.error = "Bio cannot be empty"
                    }
                    SetupViewModel.SetupEvent.ShowDOBError -> {
                        binding.inputUserDob.error = "Select Date of Birth"
                    }
                    SetupViewModel.SetupEvent.ShowGenderError -> {
                        binding.inputUserGender.error = "Select Gender to continue"
                    }
                    SetupViewModel.SetupEvent.ShowUsernameError -> {
                        binding.inputUserName.error = "Username cannot be empty"
                    }
                    is SetupViewModel.SetupEvent.ShowSetupError -> {
                        showErrorMessage(message = event.message) { viewModel.onRetryClicked() }
                    }
                    is SetupViewModel.SetupEvent.ShowSetupSuccess -> {
                        Alerter.create(requireActivity())
                            .setText(event.message)
                            .setIcon(R.drawable.ic_baseline_check_circle_24)
                            .setBackgroundColorRes(R.color.success)
                            .show()
                    }
                    is SetupViewModel.SetupEvent.StartImageUpload -> startUploadImage(event.uri)
                }
            }
        }

        viewModel.imageUri.observe(viewLifecycleOwner) { uri ->
            if (uri != null) binding.ivUserImage.load(uri)
        }

        viewModel.dob.observe(viewLifecycleOwner) { date ->
            if (date != null) binding.edtUserDob.setText(fromDateToString(date))
        }
    }

    private fun initViews() = binding.apply {
        fabUploadImage.setOnClickListener { openGalleryForImage() }
        btnSaveDetails.setOnClickListener { viewModel.onSaveClicked() }
        edtUserDob.setOnClickListener { openDateDialog() }

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

    private fun openGalleryForImage() {

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

    private fun showErrorMessage(message: String, retryMethod: () -> Unit) {

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Error")
            .setMessage(message)
            .setNegativeButton("retry") { _, _ ->
                retryMethod.invoke()
            }
            .setPositiveButton("dismiss", null)
            .show()

    }

    private fun startUploadImage(uri: Uri) {
        val data = Data.Builder()
        data.putString(Constants.KEY_MEDIA_URI, uri.toString())
        UploadImageWork.scheduleWork(requireContext(), data.build())
    }

    private fun navigateToFeeds() {

        val intent = requireActivity().intent
        requireActivity().finishAffinity().also {
            startActivity(intent)
        }

    }

}