package com.mambo.poetree.ui.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.github.onecode369.wysiwyg.WYSIWYG
import com.google.android.material.snackbar.Snackbar
import com.mambo.poetree.R
import com.mambo.poetree.databinding.FragmentEditBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class EditFragment : Fragment() {

    private var _binding: FragmentEditBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<EditViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            NavigationUI.setupWithNavController(toolbarEdit, findNavController())
            toolbarEdit.setOnMenuItemClickListener { item ->

                when (item.itemId) {

                    R.id.menu_item_save -> {
                        viewModel.onSave()
                        true
                    }

                    R.id.menu_item_preview -> {
                        openPreviewBottomSheet()
                        true
                    }

                    R.id.menu_item_stash -> {
                        Snackbar.make(requireView(), viewModel.poemContent, Snackbar.LENGTH_LONG)
                            .show()
                        true
                    }

                    else -> {
                        false
                    }
                }


            }

            edtTitle.setText(viewModel.poemTitle)
            edtTitle.addTextChangedListener { title ->
                viewModel.poemTitle = title.toString()
            }

            setUpEmotionCheck()

            setUpWYSIWYGWebView()

        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.editPoemEvent.collect { event ->
                when (event) {
                    is EditViewModel.EditPoemEvent.NavigateBackWithResult -> {
                        binding.root.clearFocus()
                        setFragmentResult(
                            "create_update_request",
                            bundleOf("create_update_result" to event.result)
                        )
                        findNavController().popBackStack()
                    }
                    is EditViewModel.EditPoemEvent.ShowInvalidInputMessage -> {
                        Snackbar.make(requireView(), event.message, Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun setUpWYSIWYGWebView() {

        binding.apply {

            val wysiwygEditor = editor

            wysiwygEditor.html = (viewModel.poemContent)

//            wysiwygEditor.setEditorBackgroundColor(R.color.black)
//            wysiwygEditor.setTextColor(R.color.white)
//            wysiwygEditor.setEditorFontColor(R.color.color_on_background)
//            wysiwygEditor.setTextBackgroundColor(R.color.color_on_background)

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

    private fun setUpEmotionCheck() {

    }

    private fun openPreviewBottomSheet() {
        val bottomSheet = PoemPreviewFragment()
        bottomSheet.show(childFragmentManager, "BottomSheet")
    }
}