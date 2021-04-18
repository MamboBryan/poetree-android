package com.mambo.poetree.ui.compose

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mambo.poetree.data.model.Topic
import com.mambo.poetree.databinding.ItemTopicBinding
import com.mambo.poetree.utils.TopicUtils
import java.util.*

class TopicAdapter : ListAdapter<Topic, TopicAdapter.TopicViewHolder>(
    TopicUtils.TOPIC_COMPARATOR
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {
        val binding =
            ItemTopicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TopicViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TopicViewHolder, position: Int) {
        val item = getItem(position)

        if (item != null)
            holder.bind(item)
    }

    inner class TopicViewHolder(
        val binding: ItemTopicBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(topic: Topic) {
            binding.apply {
                tvTopicName.text = topic.name.capitalize(Locale.ROOT)
            }
        }

    }

}