package com.mambo.poetree.ui.dashboard

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mambo.poetree.R
import com.mambo.poetree.data.model.Haiku
import com.mambo.poetree.databinding.FragmentDashboardAllBinding
import com.mambo.poetree.ui.adapter.HaikuAdapter
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding

class DashboardAllFragment : Fragment(R.layout.fragment_dashboard_all),
    HaikuAdapter.OnHaikuClickListener {

    private val binding by viewBinding(FragmentDashboardAllBinding::bind)
    private val viewModel by viewModels<DashboardViewModel>({ requireParentFragment() })

    private val adapter = HaikuAdapter(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeRecyclerview()

        binding.layoutCompleteRecycler.stateLoading.isVisible = false

        viewModel.allPoems.observe(viewLifecycleOwner) { poems ->
            binding.apply {

                layoutCompleteRecycler.stateLoading.isVisible = false
                layoutCompleteRecycler.stateEmpty.isVisible = false
                layoutCompleteRecycler.stateError.isVisible = false
                layoutCompleteRecycler.stateContent.isVisible = false

                when {
                    poems.isEmpty() -> layoutCompleteRecycler.stateEmpty.isVisible = true
                    poems.isNotEmpty() -> layoutCompleteRecycler.stateContent.isVisible = true
                    else -> layoutCompleteRecycler.stateError.isVisible = true
                }

            }

            adapter.submitList(poems)
        }

    }

    override fun onHaikuClicked(haiku: Haiku) {
        viewModel.onHaikuClicked(haiku)
    }

    private fun initializeRecyclerview() {
        binding.apply {
            layoutCompleteRecycler.recyclerView.setHasFixedSize(true)
            layoutCompleteRecycler.recyclerView.adapter = adapter
        }

    }
}