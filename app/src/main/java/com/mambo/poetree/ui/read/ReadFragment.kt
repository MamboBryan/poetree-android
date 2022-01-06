package com.mambo.poetree.ui.read

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.mambo.poetree.R
import com.mambo.poetree.databinding.FragmentReadBinding
import com.mambo.poetree.ui.fragments.CommentsFragment
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ReadFragment : Fragment(R.layout.fragment_read) {

    private val binding by viewBinding(FragmentReadBinding::bind)
    private val viewModel by viewModels<ReadViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.apply {
//            fabRead.setOnClickListener { findNavController().popBackStack() }
//
//            bottomAppBarRead.setOnMenuItemClickListener { item ->
//                when (item.itemId) {
//                    R.id.menu_action_like -> {
//                        Snackbar.make(requireView(), "Haiku Liked", Snackbar.LENGTH_SHORT).show()
//                    }
//                    R.id.menu_action_unlike -> {
//                        Snackbar.make(requireView(), "Haiku Liked", Snackbar.LENGTH_SHORT).show()
//                    }
//                    R.id.menu_action_comments -> {
//                        openCommentsBottomSheet()
//                    }
//                }
//
//                false
//            }
//        }

        setupReaderView()
        viewModel.launch()
    }

    private fun openCommentsBottomSheet() {
        val bottomSheet = CommentsFragment()
        bottomSheet.show(childFragmentManager, CommentsFragment.TAG)
    }

    private fun setupReaderView() {

        val haiku = viewModel.haiku!!

        val poet: StringBuilder = StringBuilder("")

        for (character in haiku.poet) {
            poet.append("$character \n")
        }

        binding.apply {

            tvReadTopic.text = poet.toString().toUpperCase(Locale.ENGLISH)

            val wysiwygEditor = editorRead

            wysiwygEditor.setInputEnabled(false)

            wysiwygEditor.setPadding(16, 32, 32, 32)

            val html =
                """
                    <h2><i><b> ${haiku.title} </b></i></h2>
                    <i>${haiku.content} </i>
                   
                """.trimIndent()

            wysiwygEditor.html = html
            wysiwygEditor.setCode()
        }
    }
}