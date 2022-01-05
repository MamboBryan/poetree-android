package com.mambo.poetree.ui.compose

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mambo.data.Topic
import com.mambo.poetree.R
import com.mambo.poetree.databinding.ItemTopicMinimalBinding
import java.util.*
import kotlin.properties.Delegates

class MinimalTopicAdapter(
    val topic: Topic?,
    val listener: OnTopicClickListener
) : ListAdapter<Topic, MinimalTopicAdapter.TopicViewHolder>(
    TOPIC_COMPARATOR
) {

    companion object {
        private val TOPIC_COMPARATOR =
            object : DiffUtil.ItemCallback<Topic>() {
                override fun areItemsTheSame(
                    oldItem: Topic,
                    newItem: Topic
                ): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(
                    oldItem: Topic,
                    newItem: Topic
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }

    var selectedPosition by Delegates.observable(currentList.indexOf(topic)) { _, oldPos, newPos ->
        if (newPos in currentList.indices) {
            notifyItemChanged(oldPos)
            notifyItemChanged(newPos)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {
        val binding =
            ItemTopicMinimalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TopicViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TopicViewHolder, position: Int) {

        val item = getItem(position)
        val isSelected = position == selectedPosition

        if (item != null)
            holder.bind(item, isSelected)
    }

    inner class TopicViewHolder(
        val binding: ItemTopicMinimalBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {

            binding.root.setOnClickListener {

                val position = adapterPosition

                if (position != RecyclerView.NO_POSITION) {

                    val item = currentList[position]

                    listener.onTopicSelected(item)
                    selectedPosition = position

                }
            }

        }

        fun bind(topic: Topic, isSelected: Boolean) {
            binding.apply {
                tvTopicName.text = topic.name.capitalize(Locale.ROOT)

                if (isSelected) {
                    val context = root.context

                    cardItemTopic.setCardBackgroundColor(
                        context.resources.getColor(R.color.color_primary)
                    )
                    tvTopicName.setTextColor(context.resources.getColor(R.color.color_on_primary))
                }
            }
        }

    }

    interface OnTopicClickListener {
        fun onTopicSelected(topic: Topic)
    }
}