package com.mambobryan.poetree.poem

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mambobryan.poetree.poem.databinding.FragmentPoemBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PoemFragment : Fragment(R.layout.fragment_poem) {

    private val binding by viewBinding(FragmentPoemBinding::bind)
    private val viewModel by viewModels<PoemViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val poem = viewModel.poem!!

        binding.apply {

            val wysiwygEditor = editor
            val textColor = ContextCompat.getColor(requireContext(), R.color.color_on_background)
            val backgroundColor = ContextCompat.getColor(requireContext(), R.color.color_background)

            wysiwygEditor.setInputEnabled(false)
            wysiwygEditor.setEditorFontColor(textColor)
            wysiwygEditor.setEditorBackgroundColor(backgroundColor)
            wysiwygEditor.setPadding(16, 16, 16, 16)

            val heading = "<h2><i><b> ${viewModel.title} </b></i></h2>"
            val details = "<i><b>${viewModel.topic}</b> \u2022 ${viewModel.days}</i>"
            val body = " <i>${viewModel.content}</i>"

            wysiwygEditor.html = "$heading\n$details<br><br>$body"
            wysiwygEditor.setCode()

//            textTopic.text = "Poetree".uppercase()

        }


    }
}