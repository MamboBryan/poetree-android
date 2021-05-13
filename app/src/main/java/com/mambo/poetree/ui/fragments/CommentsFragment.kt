package com.mambo.poetree.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.mambo.poetree.R
import com.mambo.poetree.databinding.FragmentCommentsBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding

class CommentsFragment : Fragment(R.layout.fragment_comments) {

    private val binding by viewBinding(FragmentCommentsBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            floatingActionButton11.setOnClickListener {
                requireActivity().onBackPressed()
            }
        }
    }
}