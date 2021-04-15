package com.mambo.poetree.ui.poem

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mambo.poetree.R
import com.mambo.poetree.databinding.FragmentPoemBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PoemFragment : Fragment(R.layout.fragment_poem) {

    private val binding by viewBinding(FragmentPoemBinding::bind)
    private val viewModel by viewModels<PoemViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val poem = viewModel.poem!!

        binding.apply {

            val wysiwygEditor = editorPoem

            wysiwygEditor.setInputEnabled(false)

            wysiwygEditor.setPadding(16, 32, 32, 32)

            val html =
                """
                    <h2><i><b> ${poem.title} </b></i></h2>
                    <i>${poem.content} </i>
                   
                """.trimIndent()

            wysiwygEditor.html = html
            wysiwygEditor.setCode()

        }


    }
}