package com.mambo.compose

import android.os.Bundle
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

        binding.apply {

            edtTitle.setText(viewModel.poemTitle)
            edtTitle.doAfterTextChanged { title -> viewModel.poemTitle = title.toString() }

            setUpWYSIWYGWebView()

        }
    }

    private fun setUpWYSIWYGWebView() =

        binding.apply {

            val wysiwygEditor = editor
            val textColor = ContextCompat.getColor(requireContext(), R.color.color_on_background)
            val backgroundColor = ContextCompat.getColor(requireContext(), R.color.color_background)

            wysiwygEditor.html = viewModel.poemContent

            wysiwygEditor.setEditorFontSize(16)
            wysiwygEditor.setEditorFontColor(textColor)
            wysiwygEditor.setEditorBackgroundColor(backgroundColor)
            wysiwygEditor.setPadding(16, 16, 16, 16)

            wysiwygEditor.setPlaceholder("Pen down thoughts here...")

            actionUndo.setOnClickListener { wysiwygEditor.undo() }

            actionRedo.setOnClickListener { wysiwygEditor.redo() }

            actionBold.setOnClickListener { wysiwygEditor.setBold() }

            actionItalic.setOnClickListener { wysiwygEditor.setItalic() }

            actionUnderline.setOnClickListener { wysiwygEditor.setUnderline() }

            actionIndent.setOnClickListener { wysiwygEditor.setIndent() }

            actionOutdent.setOnClickListener { wysiwygEditor.setOutdent() }

            actionAlignCenter.setOnClickListener { wysiwygEditor.setAlignCenter() }

            actionAlignLeft.setOnClickListener { wysiwygEditor.setAlignLeft() }

            actionAlignRight.setOnClickListener { wysiwygEditor.setAlignRight() }

            actionAlignJustify.setOnClickListener { wysiwygEditor.setAlignJustifyFull() }

            actionBlockquote.setOnClickListener { wysiwygEditor.setBlockquote() }

            actionHeading.setOnClickListener { wysiwygEditor.setHeading(4) }

            editor.setOnTextChangeListener(object :
                WYSIWYG.OnTextChangeListener {
                override fun onTextChange(text: String?) {
                    viewModel.poemContent = text ?: ""
                }

            })

        }


}