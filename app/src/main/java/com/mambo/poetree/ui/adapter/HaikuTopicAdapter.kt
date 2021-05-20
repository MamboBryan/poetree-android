package com.mambo.poetree.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mambo.poetree.data.model.HaikuTopic
import com.mambo.poetree.databinding.ItemHaikuTopicBinding
import com.mambo.poetree.utils.GradientUtils


class HaikuTopicAdapter(
    private val listener: OnHaikuTopicClickListener
) : ListAdapter<HaikuTopic, HaikuTopicAdapter.HaikuTopicViewHolder>(HAIKU_TOPIC_COMPARATOR) {

    private val gradientUtils = GradientUtils()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HaikuTopicViewHolder {
        val binding =
            ItemHaikuTopicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HaikuTopicViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HaikuTopicViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    companion object {
        private val HAIKU_TOPIC_COMPARATOR =
            object : DiffUtil.ItemCallback<HaikuTopic>() {
                override fun areItemsTheSame(
                    oldItem: HaikuTopic,
                    newItem: HaikuTopic
                ): Boolean {
                    return oldItem.name == newItem.name
                }

                override fun areContentsTheSame(
                    oldItem: HaikuTopic,
                    newItem: HaikuTopic
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }

    inner class HaikuTopicViewHolder(private val binding: ItemHaikuTopicBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {

            binding.root.setOnClickListener {

                val position = adapterPosition

                if (position != RecyclerView.NO_POSITION) {
                    val poem = getItem(position)
                    listener.onHaikuTopicClicked(poem)
                }

            }

        }

        fun bind(haiku: HaikuTopic) {
            binding.apply {

                tvTopicTitle.text = haiku.name

            }
        }

    }

    interface OnHaikuTopicClickListener {
        fun onHaikuTopicClicked(topic: HaikuTopic)
    }

}