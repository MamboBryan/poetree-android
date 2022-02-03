package com.mambobryan.features.publish

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.mambo.core.OnTopicClickListener
import com.mambo.core.adapters.GenericStateAdapter
import com.mambo.core.extensions.onQueryTextChanged
import com.mambo.core.viewmodel.MainViewModel
import com.mambo.data.models.Topic
import com.mambo.data.utils.isNotNull
import com.mambobryan.features.publish.databinding.FragmentPublishBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class PublishFragment : Fragment(R.layout.fragment_publish) {

    private val sharedViewModel: MainViewModel by activityViewModels()

    private val binding by viewBinding(FragmentPublishBinding::bind)
    private val viewModel: PublishViewModel by viewModels()

    @Inject
    lateinit var adapter: TopicAdapter

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_publish, menu)

        val searchItem = menu.findItem(R.id.action_publish_search)
        val searchView = searchItem.actionView as SearchView

        searchView.onQueryTextChanged { viewModel.onQueryUpdated(it) }

        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        setupViews()
        setupRecyclerview()

        lifecycleScope.launchWhenStarted { viewModel.topics.collectLatest { adapter.submitData(it) } }

        lifecycleScope.launchWhenStarted {
            adapter.loadStateFlow.collectLatest { loadState ->
                binding.layoutPublish.apply {

                    stateContent.isVisible = false
                    stateEmpty.isVisible = false
                    stateError.isVisible = false
                    stateLoading.isVisible = false

                    when (loadState.source.refresh) {
                        is LoadState.Loading -> {
                            stateLoading.isVisible = true
                        }

                        is LoadState.Error -> {
                            stateError.isVisible = true
                        }

                        is LoadState.NotLoading -> {

                            if (loadState.append.endOfPaginationReached) {
                                if (adapter.itemCount < 1)
                                    stateEmpty.isVisible = true
                                else {
                                    stateContent.isVisible = true
                                }
                            } else {
                                stateContent.isVisible = true
                            }

                        }
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.topic.collectLatest {
                binding.apply { btnChoose.isEnabled = it.isNotNull() }
            }
        }

    }

    private fun setupRecyclerview() = binding.apply {

        btnChoose.setOnClickListener { viewModel.onChooseClicked() }

        layoutPublish.apply {
            tvEmpty.text = "No Topic Found"
            tvError.text = "Couldn't load Topics!"

            recyclerView.layoutManager = StaggeredGridLayoutManager(3, LinearLayoutCompat.VERTICAL)
            recyclerView.adapter = adapter
            buttonRetry.setOnClickListener { adapter.retry() }
        }

        adapter.withLoadStateHeaderAndFooter(
            header = GenericStateAdapter(adapter::retry),
            footer = GenericStateAdapter(adapter::retry)
        )

        adapter.setListener(object : OnTopicClickListener {
            override fun onTopicClicked(topic: Topic) {
                viewModel.onTopicSelected(topic)
            }
        })
    }

    private fun setupViews() = binding.apply {
        toolbarPublish.title = "Choose Topic"
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbarPublish)
        toolbarPublish.setNavigationOnClickListener { findNavController().popBackStack() }
    }

}