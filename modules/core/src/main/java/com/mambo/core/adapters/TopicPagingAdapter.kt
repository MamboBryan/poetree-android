package com.mambo.core.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mambo.data.models.Topic
import com.mambo.ui.databinding.ItemTopicBinding
import me.saket.cascade.CascadePopupMenu

class TopicPagingAdapter :
    PagingDataAdapter<Topic, TopicPagingAdapter.TopicViewHolder>(Topic.COMPARATOR) {

    private var mListener: ((topic: Topic) -> Unit)? = null
    private var mUpdateClicked: ((topic: Topic) -> Unit)? = null

    fun setOnTopicClicked(listener: (topic: Topic) -> Unit) {
        mListener = listener
    }

    fun setOnUpdateClicked(listener: (topic: Topic) -> Unit) {
        mUpdateClicked = listener
    }

    inner class TopicViewHolder(private val binding: ItemTopicBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {

                layoutTopic.setOnClickListener {
                    if (absoluteAdapterPosition != RecyclerView.NO_POSITION) {
                        val topic = getItem(absoluteAdapterPosition)
                        topic?.let { mListener?.invoke(it) }
                    }
                }

            }
        }

        fun bind(topic: Topic) {
            binding.apply {
                tvTopicTitle.text = topic.name.replaceFirstChar { it.uppercase() }
                layoutBg.setBackgroundColor(Color.parseColor(topic.color))
                layoutTopic.setOnLongClickListener {
                    val popup = CascadePopupMenu(binding.root.context, layoutTopic)
                    popup.menu.addSubMenu("Topic").also {
                        it.setHeaderTitle("Some Controls?")
                        it.add("update").setOnMenuItemClickListener {
                            mUpdateClicked?.invoke(topic)
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