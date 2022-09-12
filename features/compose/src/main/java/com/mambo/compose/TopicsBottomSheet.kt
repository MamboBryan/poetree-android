package com.mambo.compose

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mambo.compose.databinding.BottomSheetTopicsBinding
import com.mambo.compose.databinding.ItemTopicChooseBinding
import com.mambo.core.adapters.GenericStateAdapter
import com.mambo.core.adapters.LazyPagingAdapter
import com.mambo.core.adapters.getInflater
import com.mambo.core.utils.*
import com.mambo.data.models.Topic
import com.mambo.ui.databinding.ItemTopicBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class TopicsBottomSheet : BottomSheetDialogFragment() {

    private val viewModel: ComposeViewModel by viewModels({ requireParentFragment() })

    private var _binding: BottomSheetTopicsBinding? = null
    private val binding get() = _binding!!

    private var mTopicSelected: ((topic: Topic) -> Unit)? = null
    private val adapter = LazyPagingAdapter<Topic, ItemTopicChooseBinding>(Topic.COMPARATOR)

    private val selected = MutableLiveData<Topic>(null)

    fun onTopicSelected(block: (topic: Topic) -> Unit) {
        mTopicSelected = block
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.setOnShowListener {

            val bottomSheetDialog = it as BottomSheetDialog

            val parentLayout =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { dialog ->

                dialog.setupFullHeight()
                val behaviour = BottomSheetBehavior.from(dialog)
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
                behaviour.isDraggable = false
                behaviour.isHideable = false
            }
        }
        return dialog
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BottomSheetTopicsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupRecyclerview()

        selected.observe(viewLifecycleOwner) {
            binding.btnChoose.isEnabled = it != null
        }

        lifecycleScope.launchWhenResumed {
            viewModel.topics.collectLatest { adapter.submitData(it) }
        }

        lifecycleScope.launchWhenStarted {
            adapter.loadStateFlow.collectLatest { state: CombinedLoadStates ->
                when (state.source.refresh) {
                    is LoadState.Loading -> binding.layoutTopics.showLoading()
                    is LoadState.Error -> binding.layoutTopics.showError()
                    is LoadState.NotLoading -> {
                        if (state.append.endOfPaginationReached && adapter.itemCount < 1)
                            binding.layoutTopics.showEmpty()
                        else
                            binding.layoutTopics.showContent()

                    }
                }
            }
        }

    }

    private fun setupRecyclerview() = binding.apply {

        adapter.apply {
            onCreate { ItemTopicChooseBinding.inflate(it.getInflater(), it, false) }
            onBind { topic, selected ->
                binding.apply {

                    val context = tvTopicChoice.context

                    val textColor = R.color.color_on_surface
                    val bgColor = if (selected) topic.color else "#FEFEFE"

                    tvTopicChoice.text = topic.name.replaceFirstChar { it.uppercase() }

                    layoutTopicChoice.setBackgroundColor(Color.parseColor(bgColor))
                    tvTopicChoice.setTextColor(ContextCompat.getColor(context, textColor))

                }
            }
            onItemSelected { selected.value = it }
            withLoadStateHeaderAndFooter(
                header = GenericStateAdapter(adapter::retry),
                footer = GenericStateAdapter(adapter::retry)
            )
        }

        layoutTopics.apply {
            tvEmpty.text = "No Topic Found"
            tvError.text = "Couldn't load Topics!"
            recyclerView.layoutManager = StaggeredGridLayoutManager(3, LinearLayoutCompat.VERTICAL)
            recyclerView.adapter = adapter
            buttonRetry.setOnClickListener { adapter.retry() }
        }

    }

    private fun setupViews() = binding.apply {
        toolbarPublish.title = "Choose Topic"
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbarPublish)
        toolbarPublish.setNavigationOnClickListener { dismiss() }
        btnChoose.setOnClickListener {
            selected.value?.let { item ->
                mTopicSelected?.invoke(item)
                dismiss()
            }
        }
    }


}