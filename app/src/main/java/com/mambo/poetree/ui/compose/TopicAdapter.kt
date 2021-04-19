package com.mambo.poetree.ui.compose

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mambo.poetree.R
import com.mambo.poetree.data.model.Topic
import com.mambo.poetree.databinding.ItemTopicBinding
import com.mambo.poetree.utils.TopicUtils
import java.util.*
import kotlin.properties.Delegates

class TopicAdapter(
    val topic: Topic?,
    val listener: OnTopicClickListener
) : ListAdapter<Topic, TopicAdapter.TopicViewHolder>(
    TopicUtils.TOPIC_COMPARATOR
) {
    var selectedPosition by Delegates.observable(currentList.indexOf(topic)) { _, oldPos, newPos ->
        if (newPos in currentList.indices) {
            notifyItemChanged(oldPos)
            notifyItemChanged(newPos)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {
        val binding =
            ItemTopicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TopicViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TopicViewHolder, position: Int) {

        val item = getItem(position)
        val isSelected = position == selectedPosition

        if (item != null)
            holder.bind(item, isSelected)
    }

    inner class TopicViewHolder(
        val binding: ItemTopicBinding
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