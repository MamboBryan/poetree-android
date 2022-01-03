package com.mambo.poetree.ui.compose

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mambo.data.Emotion
import com.mambo.poetree.R
import com.mambo.poetree.databinding.FragmentChoiceChildBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChoiceEmotionFragment : Fragment(R.layout.fragment_choice_child),
    EmotionAdapter.OnEmotionClickListener {

    private val binding by viewBinding(FragmentChoiceChildBinding::bind)
    private val viewModel by viewModels<ComposeViewModel>({ requireParentFragment() })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeEmotionsRecyclerview()

    }

    private fun initializeEmotionsRecyclerview() {

        val emotionAdapter = EmotionAdapter(null, this)

        binding.apply {
            rvChoice.setHasFixedSize(true)
            rvChoice.adapter = emotionAdapter
        }

    }

    override fun onEmotionSelected(emotion: Emotion) {
        viewModel.onPoemEmotionClicked(emotion)
    }
}