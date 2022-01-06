package com.mambo.compose

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mambo.compose.databinding.FragmentPreviewBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PreviewFragment : Fragment(R.layout.fragment_preview) {

    private val binding by viewBinding(FragmentPreviewBinding::bind)
    private val viewModel by viewModels<ComposeViewModel>({ requireParentFragment() })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpPoemPreview()

    }

    private fun setUpPoemPreview() {
        binding.apply {

            val wysiwygEditor = editorPoem

            wysiwygEditor.setInputEnabled(false)
            wysiwygEditor.setPadding(32, 32, 32, 32)

        }
    }

    override fun onResume() {
        super.onResume()

        updatePoemPreview()
    }

    private fun updatePoemPreview() {

        val title =
            if (viewModel.poemTitle.isNotEmpty()) viewModel.poemTitle else "Untitled"

        val content =
            if (viewModel.poemContent.isNotEmpty()) viewModel.poemContent else "No art has been penned down"

        binding.apply {

            val wysiwygEditor = editorPoem


            val html =
                """
                    <h2><i><b> $title </b></i></h2>
                    <i>$content </i>
                       
                """.trimIndent()

            wysiwygEditor.html = html
            wysiwygEditor.setCode()
        }

    }

}