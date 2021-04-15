package com.mambo.poetree.ui.compose

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mambo.poetree.R
import com.mambo.poetree.databinding.FragmentChoiceParentBinding
import com.mambo.poetree.ui.edit.EditViewModel
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding

class ChoiceEmotionFragment : Fragment(R.layout.fragment_choice_child) {

    private val binding by viewBinding(FragmentChoiceParentBinding::bind)
    private val viewModel by viewModels<EditViewModel>({ requireParentFragment() })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }
}