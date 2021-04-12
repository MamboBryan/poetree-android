package com.mambo.poetree.ui.settings

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.mambo.poetree.R
import com.mambo.poetree.databinding.FragmentSettingBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SettingFragment : Fragment(R.layout.fragment_setting) {

    private val binding by viewBinding(FragmentSettingBinding::bind)
    private val viewModel by viewModels<SettingsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            NavigationUI.setupWithNavController(toolbar, findNavController())

            switchDarkTheme.setOnCheckedChangeListener { view, isChecked ->
                viewModel.updateAppTheme(isChecked)
            }

        }

        viewModel.isDarkModeEnabled.observe(viewLifecycleOwner) { isDarkModeEnabled ->
            binding.switchDarkTheme.isChecked = isDarkModeEnabled
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.settingsEvent.collect { event ->
                when (event) {
                    is SettingsViewModel.SettingsEvent.UpdateAppTheme -> {
                        changeTheme(event.isDarkModeEnable)
                    }
                }
            }
        }

    }

    private fun changeTheme(isDarkModeEnabled: Boolean) {

        when (isDarkModeEnabled) {

            true -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }

            false -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

        }

        (activity as AppCompatActivity?)!!.delegate.applyDayNight()

    }

}