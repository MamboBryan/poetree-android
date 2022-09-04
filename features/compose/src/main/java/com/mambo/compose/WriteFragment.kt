package com.mambo.compose

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mambo.compose.databinding.FragmentWriteBinding
import com.mambo.libraries.editor.WYSIWYG
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WriteFragment : Fragment(R.layout.fragment_write) {

    private val binding by viewBinding(FragmentWriteBinding::bind)
    private val viewModel by viewModels<ComposeViewModel>({ requireParentFragment() })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()

    }

    override fun onResume() {
        super.onResume()

        initViews()

    }

    private fun initViews() = binding.apply {
        edtTitle.setText(viewModel.poemTitle)
        editor.html = viewModel.poemContent
    }

    private fun setupViews() = binding.apply {

        edtTitle.doAfterTextChanged { title -> viewModel.poemTitle = title.toString() }

        val textColor = ContextCompat.getColor(requireContext(), R.color.color_on_background)
        val backgroundColor = ContextCompat.getColor(requireContext(), R.color.color_background)

        editor.setEditorFontSize(16)
        editor.setEditorFontColor(textColor)
        editor.setEditorBackgroundColor(backgroundColor)
        editor.setPadding(16, 16, 16, 16)

        editor.setPlaceholder("Pen down thoughts here...")

        actionUndo.setOnClickListener { editor.undo() }

        actionRedo.setOnClickListener { editor.redo() }

        actionBold.setOnClickListener { editor.setBold() }

        actionItalic.setOnClickListener { editor.setItalic() }

        actionUnderline.setOnClickListener { editor.setUnderline() }

        actionIndent.setOnClickListener { editor.setIndent() }

        actionOutdent.setOnClickListener { editor.setOutdent() }

        actionAlignCenter.setOnClickListener { editor.setAlignCenter() }

        actionAlignLeft.setOnClickListener { editor.setAlignLeft() }

        actionAlignRight.setOnClickListener { editor.setAlignRight() }

        actionAlignJustify.setOnClickListener { editor.setAlignJustifyFull() }

        actionBlockquote.setOnClickListener { editor.setBlockquote() }

        actionHeading.setOnClickListener { editor.setHeading(4) }

        editor.onTextChanged { content, html ->
            viewModel.poemContent = content ?: ""
            viewModel.poemHtml = html ?: ""
        }

    }

}