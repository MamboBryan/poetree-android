package com.mambobryan.poetree.poem

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.mambo.core.viewmodel.MainViewModel
import com.mambo.libraries.editor.WYSIWYG
import com.mambobryan.navigation.Destinations
import com.mambobryan.navigation.extensions.getDeeplink
import com.mambobryan.navigation.extensions.navigate
import com.mambobryan.poetree.poem.databinding.FragmentPoemBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class PoemFragment : Fragment(R.layout.fragment_poem) {

    private val binding by viewBinding(FragmentPoemBinding::bind)
    private val viewModel: PoemViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val poem = mainViewModel.poem.value!!
        viewModel.updatePoem(poem)

        initViews()

        initEditor()

        lifecycleScope.launchWhenStarted {
            viewModel.events.collect { event ->
                when (event) {
                    PoemViewModel.PoemEvent.NavigateToArtistDetails -> navigateToArtistDetails()
                    PoemViewModel.PoemEvent.NavigateToComments -> navigateToComments()
                    is PoemViewModel.PoemEvent.NavigateToEditPoem -> {
                    }
                    is PoemViewModel.PoemEvent.ShowPoemConfirmationMessage -> {
                    }
                    is PoemViewModel.PoemEvent.ShowUndoDeletePoemMessage -> {
                    }
                }
            }
        }

    }

    private fun initViews() = binding.apply {

        NavigationUI.setupWithNavController(toolbar, findNavController())
        toolbar.title = ""

        val isUser = false

        if (isUser)
            toolbar.inflateMenu(R.menu.menu_poem)

        layoutArtist.setOnClickListener { viewModel.onArtistImageClicked() }
        ivPoemComment.setOnClickListener { viewModel.onCommentsClicked() }

        layoutArtist.isVisible = viewModel.poem?.userId == ""
        layoutPoemActions.isVisible = viewModel.poem?.isOffline == false


    }


    private fun initEditor() = binding.apply {
        val wysiwygEditor = editor
        val textColor = ContextCompat.getColor(requireContext(), R.color.color_on_background)
        val backgroundColor = ContextCompat.getColor(requireContext(), R.color.color_background)

        wysiwygEditor.setInputEnabled(false)
        wysiwygEditor.setEditorFontColor(textColor)
        wysiwygEditor.setEditorBackgroundColor(backgroundColor)
        wysiwygEditor.setPadding(16, 16, 16, 16)

        wysiwygEditor.html = viewModel.getHtml()
        wysiwygEditor.setCode()

        wysiwygEditor.setOnInitialLoadListener(object : WYSIWYG.AfterInitialLoadListener {
            override fun onAfterInitialLoad(isReady: Boolean) {
                layoutPoemLoading.isVisible = !isReady
                layoutPoemContent.isVisible = isReady
            }
        })
    }

    private fun navigateToArtistDetails(){
        navigate(getDeeplink(Destinations.ARTIST))
    }

    private fun navigateToComments(){
        navigate(getDeeplink(Destinations.COMMENTS))
    }

}