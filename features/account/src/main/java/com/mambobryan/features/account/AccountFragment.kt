package com.mambobryan.features.account

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import coil.load
import com.mambobryan.features.account.databinding.FragmentAccountBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class AccountFragment : Fragment(R.layout.fragment_account) {

    private val binding by viewBinding(FragmentAccountBinding::bind)
    private val viewModel: AccountViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        lifecycleScope.launchWhenResumed {
            viewModel.userDetails.collectLatest {
                binding.apply {
                    edtUserName.setText(it.name)
                    edtUserEmail.setText(it.email)
                    edtUserDob.setText(it.dateOfBirth)
                    edtUserGender.setText(if (it.gender == 1) "male" else "female")
                    edtUserBio.setText(it.bio)
                    ivUserImage.load(it.image)

                    Log.i("Cowa", "IMAGE URL -> ${it.image} ")

                }
            }
        }

    }

    private fun initViews() = binding.apply {
        NavigationUI.setupWithNavController(toolbar, findNavController())
        toolbar.title = "Update Account"
    }

}