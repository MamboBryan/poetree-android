package com.mambo.poetree.ui.edit

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mambo.poetree.databinding.FragmentPoemPreviewBinding
import com.mambo.poetree.utils.GradientUtils
import dagger.hilt.android.AndroidEntryPoint
import org.ocpsoft.prettytime.PrettyTime
import java.util.*

@AndroidEntryPoint
class PoemPreviewFragment : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "ReviewsBottomSheet"
    }

    private var _binding: FragmentPoemPreviewBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<EditViewModel>({ requireParentFragment() })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPoemPreviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireDialog() as BottomSheetDialog).dismissWithAnimation = true

        binding.apply {

            ivPreviewBg.background = GradientUtils.getGradientBackground()

        }

        setUpPoemTitle()
        setUpPoemBody()
        setUpPoemUser()
    }

    private fun setUpPoemTitle() {
        binding.apply {
            val wysiwygEditor = editorTitle

//            wysiwygEditor.setEditorHeight(200)
//            wysiwygEditor.setEditorFontSize(16)
            wysiwygEditor.setPadding(16, 16, 16, 0)

            wysiwygEditor.html = ("<h2><i><b> ${viewModel.poemTitle} </b></i></h2>")
            wysiwygEditor.setCode()
        }
    }

    private fun setUpPoemBody() {
        binding.apply {

            val wysiwygEditor = editorContent

            wysiwygEditor.setCode()

//            wysiwygEditor.setEditorHeight(200)
//            wysiwygEditor.setEditorFontSize(16)
            wysiwygEditor.setPadding(16, 16, 16, 16)

            wysiwygEditor.html = "<i>${viewModel.poemContent}</i>"
            wysiwygEditor.setCode()
        }
    }

    private fun setUpPoemUser() {
        binding.apply {

            val prettyTime = PrettyTime()

            ivPreviewUser.background = GradientUtils.getGradientBackground()
            tvPreviewDate.text = prettyTime.format(Date())

        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.setOnShowListener {

            val bottomSheetDialog = it as BottomSheetDialog

            val parentLayout =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { it ->
                setupFullHeight(it)

                val behaviour = BottomSheetBehavior.from(it)
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
                behaviour.isDraggable = false
                behaviour.isHideable = true
            }
        }
        return dialog
    }

    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
    }


}