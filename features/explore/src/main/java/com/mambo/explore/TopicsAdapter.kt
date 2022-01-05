package com.mambo.explore

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mambo.explore.databinding.ItemTopicBinding


class TopicsAdapter(
    private val listener: OnTopicClickListener
) : ListAdapter<String, TopicsAdapter.TopicViewHolder>(TOPIC_COMPARATOR) {

    val colors = listOf(
        "#BAFFAD",
        "#FFC2F1",
        "#FEC2C6",
        "#ADFFFF",
        "#FFFFAE",
        "#C2C7FF",
        "#E3C2FF",
        "#E0E0E0",
        "#FFCFC2",
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {
        val binding =
            ItemTopicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TopicViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TopicViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    companion object {
        private val TOPIC_COMPARATOR =
            object : DiffUtil.ItemCallback<String>() {
                override fun areItemsTheSame(
                    oldItem: String,
                    newItem: String
                ): Boolean {
                    return oldItem == newItem
                }

                override fun areContentsTheSame(
                    oldItem: String,
                    newItem: String
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }

    inner class TopicViewHolder(private val binding: ItemTopicBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {

            binding.root.setOnClickListener {

                val position = adapterPosition

                if (position != RecyclerView.NO_POSITION) {
                    val poem = getItem(position)
                    listener.onTopicClicked(poem)
                }

            }

        }

        fun bind(haiku: String) {
            binding.apply {

                tvTopicTitle.text = haiku.replaceFirstChar { it.uppercase() }
                layoutTopic.setBackgroundColor(Color.parseColor(colors[adapterPosition]))

            }
        }

    }

    interface OnTopicClickListener {
        fun onTopicClicked(topic: String)
    }

}