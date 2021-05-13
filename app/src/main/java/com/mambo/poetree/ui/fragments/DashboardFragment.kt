package com.mambo.poetree.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.mambo.poetree.R
import com.mambo.poetree.databinding.FragmentDashboardBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding

class DashboardFragment : Fragment(R.layout.fragment_dashboard) {
    private val binding by viewBinding(FragmentDashboardBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            floatingActionButton10.setOnClickListener {
                Snackbar.make(requireView(), "Dashboard", Snackbar.LENGTH_LONG).show()
                Toast.makeText(requireContext(), "Dashboard", Toast.LENGTH_SHORT).show()
            }
        }
    }
}