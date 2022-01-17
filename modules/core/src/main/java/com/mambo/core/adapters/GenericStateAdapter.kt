package com.mambo.core.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mambo.ui.databinding.LayoutLoadStateBinding

class GenericStateAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<GenericStateAdapter.LoadStateViewHolder>() {

    class LoadStateViewHolder(
        val binding: LayoutLoadStateBinding, retry: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.loadStateButton.setOnClickListener { retry() }
        }

        fun bind(loadState: LoadState) {
            binding.apply {

                if (loadState is LoadState.Error) {
                    loadStateMessage.text = loadState.error.localizedMessage
                }

                loadStateProgress.isVisible = loadState is LoadState.Loading
                loadStateButton.isVisible = loadState is LoadState.Error
                loadStateMessage.isVisible = loadState is LoadState.Error
            }
        }
    }

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        val binding =
            LayoutLoadStateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadStateViewHolder(binding, retry)
    }

}