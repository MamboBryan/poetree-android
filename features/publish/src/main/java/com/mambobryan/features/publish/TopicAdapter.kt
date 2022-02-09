package com.mambobryan.features.publish

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mambo.core.OnTopicClickListener
import com.mambo.data.models.Topic
import com.mambo.data.utils.TOPIC_COMPARATOR
import com.mambobryan.features.publish.databinding.ItemTopicChoiceBinding
import javax.inject.Inject

class TopicAdapter @Inject constructor() :
    PagingDataAdapter<Topic, TopicAdapter.TopicViewHolder>(TOPIC_COMPARATOR) {

    private lateinit var onTopicClickListener: OnTopicClickListener
    private var selectedPosition = RecyclerView.NO_POSITION

    fun setListener(listener: OnTopicClickListener) {
        onTopicClickListener = listener
    }

    private fun selectItem(position: Int) {
        val previousPosition = selectedPosition
        selectedPosition = position

        if (previousPosition != RecyclerView.NO_POSITION)
            notifyItemChanged(previousPosition)
        notifyItemChanged(selectedPosition)

    }

    inner class TopicViewHolder(private val binding: ItemTopicChoiceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {

                layoutTopicChoice.setOnClickListener {
                    if (absoluteAdapterPosition != RecyclerView.NO_POSITION) {
                        val topic = getItem(absoluteAdapterPosition)
                        if (topic != null) {
                            onTopicClickListener.onTopicClicked(topic)
                            selectItem(absoluteAdapterPosition)
                        }
                    }
                }

            }
        }

        fun bind(topic: Topic, isSelected: Boolean) {
            binding.apply {

                val context = tvTopicChoice.context

                val textColor =
                    if (isSelected) R.color.color_on_primary else R.color.color_on_surface
                val bgColor = if (isSelected) R.color.color_primary else R.color.color_surface

                tvTopicChoice.text = topic.name.replaceFirstChar { it.uppercase() }

                layoutTopicChoice.setBackgroundColor(ContextCompat.getColor(context, bgColor))
                tvTopicChoice.setTextColor(ContextCompat.getColor(context, textColor))

            }
        }

    }

    override fun onBindViewHolder(holder: TopicViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null)
            holder.bind(currentItem, position == selectedPosition)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {
        val binding =
            ItemTopicChoiceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TopicViewHolder(binding)
    }
}