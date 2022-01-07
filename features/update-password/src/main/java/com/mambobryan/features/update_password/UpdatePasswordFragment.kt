package com.mambobryan.features.update_password

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mambobryan.features.update_password.databinding.FragmentUpdatePasswordBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpdatePasswordFragment : Fragment(R.layout.fragment_update_password) {

    private val binding by viewBinding(FragmentUpdatePasswordBinding::bind)
    private val viewModel: UpdatePasswordViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

}