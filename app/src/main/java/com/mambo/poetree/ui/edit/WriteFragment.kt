package com.mambo.poetree.ui.edit

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.onecode369.wysiwyg.WYSIWYG
import com.mambo.poetree.R
import com.mambo.poetree.databinding.FragmentWriteBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WriteFragment : Fragment(R.layout.fragment_write) {

    private val binding by viewBinding(FragmentWriteBinding::bind)
    private val viewModel by viewModels<EditViewModel>({ requireParentFragment() })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            edtTitle.setText(viewModel.poemTitle)
            edtTitle.addTextChangedListener { title ->
                viewModel.poemTitle = title.toString()
            }

            setUpWYSIWYGWebView()

        }
    }

    private fun setUpWYSIWYGWebView() {

        binding.apply {

            val wysiwygEditor = editor

            wysiwygEditor.html = (viewModel.poemContent)

            wysiwygEditor.setEditorHeight(200)
            wysiwygEditor.setEditorFontSize(16)
            wysiwygEditor.setPadding(16, 16, 16, 16)

            wysiwygEditor.setPlaceholder("Insert your notes here...")

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

            editor.setOnTextChangeListener(object : WYSIWYG.OnTextChangeListener {
                override fun onTextChange(text: String?) {
                    viewModel.poemContent = text ?: ""
                }

            })

        }

    }


}