package com.mambo.core.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mambo.core.OnTopicClickListener
import com.mambo.data.models.Topic
import com.mambo.data.utils.TOPIC_COMPARATOR
import com.mambo.ui.databinding.ItemTopicBinding

class TopicPagingAdapter :
    PagingDataAdapter<Topic, TopicPagingAdapter.TopicViewHolder>(TOPIC_COMPARATOR) {

    private lateinit var onTopicClickListener: OnTopicClickListener

    fun setListener(listener: OnTopicClickListener) {
        onTopicClickListener = listener
    }

    inner class TopicViewHolder(private val binding: ItemTopicBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {

                layoutTopic.setOnClickListener {
                if(absoluteAdapterPosition != RecyclerView.NO_POSITION)
                    onTopicClickListener.onTopicClicked()
                }

            }
        }

        fun bind(topic: Topic) {
            binding.apply {

                tvTopicTitle.text = topic.name
                layoutBg.setBackgroundColor(Color.parseColor(topic.color))

            }
        }

    }

    override fun onBindViewHolder(holder: TopicViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null)
            holder.bind(currentItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {
        val binding =
            ItemTopicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TopicViewHolder(binding)
    }
}