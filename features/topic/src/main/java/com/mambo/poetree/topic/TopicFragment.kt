package com.mambo.poetree.topic

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.irozon.alertview.AlertActionStyle
import com.irozon.alertview.AlertStyle
import com.irozon.alertview.AlertView
import com.irozon.alertview.objects.AlertAction
import com.mambo.core.adapters.LazyAdapter
import com.mambo.core.adapters.getInflater
import com.mambo.core.utils.LoadingDialog
import com.mambo.core.utils.isValidHexColor
import com.mambo.core.utils.toObliviousHumanLanguage
import com.mambo.core.viewmodel.MainViewModel
import com.mambo.poetree.topic.databinding.FragmentTopicBinding
import com.mambo.poetree.topic.databinding.ItemColorBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class TopicFragment : Fragment(R.layout.fragment_topic) {

    private val binding by viewBinding(FragmentTopicBinding::bind)

    private val viewModel: TopicViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    private val lazyAdapter = LazyAdapter<String, ItemColorBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.updateTopic(mainViewModel.topic.value)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        setupAdapter()

        viewModel.color.observe(viewLifecycleOwner) {
            it?.let { color ->
                binding.apply {
                    inputColor.error = null
                    edtColor.setText(it)
                    layoutPreviewBg.setBackgroundColor(Color.parseColor(color))
                }
            }
        }

        viewModel.name.observe(viewLifecycleOwner) {
            binding.tvPreview.text = it.ifBlank { "Topic Name" }
        }

        viewModel.colors.observe(viewLifecycleOwner) {
            lazyAdapter.submitList(it)
        }

        viewModel.topic.observe(viewLifecycleOwner) {
            val btnText = when (it != null) {
                true -> "update"
                false -> "save"
            }
            binding.apply {
                edtName.setText(it?.name)
                btnSave.text = btnText
                btnDelete.isVisible = false
            }

        }

        lifecycleScope.launchWhenResumed {
            viewModel.uiEvents.collectLatest {
                when (it) {
                    is TopicViewModel.TopicEvent.Error -> showError(it.message)
                    is TopicViewModel.TopicEvent.Success -> showSuccess(it.message)
                    is TopicViewModel.TopicEvent.Loading -> {
                        when (it.isLoading) {
                            true -> LoadingDialog.show(requireContext())
                            false -> LoadingDialog.dismiss()
                        }
                    }
                    null -> {}
                }
            }
        }

    }

    private fun initViews() {
        binding.apply {

            toolbarTopic.setNavigationIcon(R.drawable.ic_baseline_close_24)
            toolbarTopic.setNavigationOnClickListener { findNavController().popBackStack() }
            toolbarTopic.title = when (viewModel.topic.value == null) {
                true -> "Create Topic"
                false -> "Update Topic"
            }

            edtName.doAfterTextChanged {
                inputName.error = null
                viewModel.updateName(it.toString().ifBlank { "" })
            }

            ivGenerateColor.setOnClickListener { viewModel.generateRandomColors() }
            btnSave.setOnClickListener { onSaveClicked() }
            btnDelete.setOnClickListener { onDeleteClicked() }

        }
    }

    private fun showError(message: String) {
        val alert = AlertView(
            title = "Error",
            message = "\n${message.toObliviousHumanLanguage()}\n",
            style = AlertStyle.DIALOG
        )
        alert.addAction(AlertAction("dismiss", AlertActionStyle.NEGATIVE) {})
        alert.show(requireActivity() as AppCompatActivity)
    }

    private fun showSuccess(message: String) {
        val alert = AlertView(
            title = "Success",
            message = "\n${message.toObliviousHumanLanguage()}\n",
            style = AlertStyle.DIALOG
        )
        alert.addAction(AlertAction("finish", AlertActionStyle.POSITIVE) {
            findNavController().popBackStack()
        })
        alert.show(requireActivity() as AppCompatActivity)
    }

    private fun setupAdapter() {
        lazyAdapter.onCreate { ItemColorBinding.inflate(it.getInflater(), it, false) }
        lazyAdapter.onBind { hex ->
            this.apply { cardColor.setBackgroundColor(Color.parseColor(hex)) }
        }
        lazyAdapter.onItemSelected { hex -> viewModel.updateColor(hex) }
        binding.recyclerColors.apply {
            layoutManager = GridLayoutManager(requireContext(), 5)
            adapter = lazyAdapter
        }
    }

    private fun onDeleteClicked() {
        val alert = AlertView(
            title = "Delete Topic",
            message = "You're about to delete this topic. Are you sure?",
            style = AlertStyle.DIALOG
        )
        alert.addAction(AlertAction("YES", AlertActionStyle.POSITIVE) {
            viewModel.delete()
        })
        alert.addAction(AlertAction("NO", AlertActionStyle.NEGATIVE) {})
        alert.show(requireActivity() as AppCompatActivity)
    }

    private fun onSaveClicked() {

        var isValid = true

        val name = binding.edtName.text.toString()
        val color = binding.edtColor.text.toString()

        if (name.isBlank()) {
            binding.inputName.error = "Invalid name"
            isValid = false
        }


        if (color.isBlank() or color.isValidHexColor().not()) {
            binding.inputColor.error = "Invalid color"
            isValid = false
        }

        if (isValid) viewModel.saveTopic(name.lowercase(), color)

    }

    override fun onDestroy() {
        super.onDestroy()
        mainViewModel.setTopic(null)
    }

}
