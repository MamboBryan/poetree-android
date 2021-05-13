package com.mambo.poetree.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.mambo.poetree.R
import com.mambo.poetree.databinding.FragmentPublishBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding

class PublishFragment : Fragment(R.layout.fragment_publish) {

    private val binding by viewBinding(FragmentPublishBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            floatingActionButton9.setOnClickListener { }
        }
    }
}