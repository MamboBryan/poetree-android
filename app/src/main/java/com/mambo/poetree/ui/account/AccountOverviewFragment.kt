package com.mambo.poetree.ui.account

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.mambo.poetree.R
import com.mambo.poetree.databinding.FragmentAccountOverviewBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding

class AccountOverviewFragment : Fragment(R.layout.fragment_account_overview) {

    private val binding by viewBinding(FragmentAccountOverviewBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}