package com.mambo.explore

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.mambo.core.adapters.GenericStateAdapter
import com.mambo.core.adapters.LazyPagingAdapter
import com.mambo.core.adapters.getInflater
import com.mambo.core.utils.*
import com.mambo.core.viewmodel.MainViewModel
import com.mambo.data.models.Topic
import com.mambo.explore.databinding.FragmentExploreBinding
import com.mambo.ui.databinding.ItemTopicBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.saket.cascade.CascadePopupMenu
import javax.inject.Inject

@AndroidEntryPoint
class ExploreFragment : Fragment(R.layout.fragment_explore) {

    @Inject
    lateinit var exploreActions: ExploreActions

    private val binding by viewBinding(FragmentExploreBinding::bind)
    private val mainViewModel: MainViewModel by activityViewModels()
    private val adapter = LazyPagingAdapter<Topic, ItemTopicBinding>(Topic.COMPARATOR)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        lifecycleScope.launch {
            mainViewModel.topics.collectLatest { adapter.submitData(it) }
        }

        lifecycleScope.launchWhenResumed {
            adapter.loadStateFlow.collectLatest { state: CombinedLoadStates ->
                binding.layoutStateExplore.apply {
                    when (state.mediator?.refresh) {
                        is LoadState.Loading -> showLoading()
                        is LoadState.Error -> showError()
                        is LoadState.NotLoading -> {
                            if (state.append.endOfPaginationReached && adapter.itemCount < 1)
                                showEmpty()
                            else
                                showContent()
                        }
                        null -> showLoading()
                    }
                }
            }
        }

    }

    private fun initViews() {
        adapter.apply {
            onCreate { ItemTopicBinding.inflate(it.getInflater(), it, false) }
            onBind { topic ->
                this.apply {
                    tvTopicTitle.text = topic.name.replaceFirstChar { it.uppercase() }
                    layoutBg.setBackgroundColor(Color.parseColor(topic.color))
                    layoutTopic.setOnClickListener { exploreActions.navigateToSearch(topic) }
                    layoutTopic.setOnLongClickListener {
                        val popup = CascadePopupMenu(binding.root.context, layoutTopic)
                        popup.menu.addSubMenu("Topic").also {
                            it.setHeaderTitle("More?")
                            it.add("update").setOnMenuItemClickListener {
                                exploreActions.navigateToTopic(topic = topic)
                                true
                            }
//                        it.add("delete").setOnMenuItemClickListener {
//                            popup.navigateBack()
//                        }
                        }
                        popup.show()
                        return@setOnLongClickListener true
                    }
                }
            }
            withLoadStateHeaderAndFooter(
                header = GenericStateAdapter(adapter::retry),
                footer = GenericStateAdapter(adapter::retry)
            )

        }

        binding.apply {

            layoutSearch.setOnClickListener { exploreActions.navigateToSearch() }
            fabTopic.setOnClickListener { exploreActions.navigateToTopic() }

            layoutStateExplore.apply {
                tvEmpty.text = "No Topic Found!"
                tvError.text = "Couldn't Load Topics!"

                recyclerView.adapter = adapter
                recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
                buttonRetry.setOnClickListener { adapter.retry() }
            }

        }

    }

}