package com.mambo.poetree.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.mambo.poetree.R
import com.mambo.poetree.databinding.FragmentBaseBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding

class SearchFragment: Fragment(R.layout.fragment_base) {

    private val binding by viewBinding(FragmentBaseBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {  }
    }


}