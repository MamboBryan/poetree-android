package com.mambo.compose

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mambo.compose.databinding.FragmentPreviewBinding
import com.mambo.data.utils.isNotNull
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

    override fun onResume() {
        super.onResume()

        updatePoemPreview()

    }

    private fun setUpPoemPreview() = binding.apply {

        val wysiwygEditor = editorPreviewPoem

        wysiwygEditor.setInputEnabled(false)
        wysiwygEditor.setPadding(32, 32, 32, 32)

    }

    private fun updatePoemPreview() = binding.apply {

        val textColor = ContextCompat.getColor(requireContext(), R.color.color_on_background)
        val backgroundColor = ContextCompat.getColor(requireContext(), R.color.color_background)

        editorPreviewPoem.setEditorFontColor(textColor)
        editorPreviewPoem.setEditorBackgroundColor(backgroundColor)

        editorPreviewPoem.html = viewModel.getHtml()
        editorPreviewPoem.setCode()

        fabPreviewPublish.isEnabled = viewModel.poemContent.isNotEmpty() &&
                viewModel.poemTitle.isNotEmpty() && viewModel.topic.isNotNull()

    }

}