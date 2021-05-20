package com.mambo.poetree.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mambo.poetree.data.model.HaikuEmotion
import com.mambo.poetree.databinding.ItemHaikuEmotionBinding
import com.mambo.poetree.utils.GradientUtils


class HaikuEmotionAdapter(
    private val listener: OnHaikuEmotionClickListener
) : ListAdapter<HaikuEmotion, HaikuEmotionAdapter.HaikuEmotionViewHolder>(HAIKU_TOPIC_COMPARATOR) {

    private val gradientUtils = GradientUtils()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HaikuEmotionViewHolder {
        val binding =
            ItemHaikuEmotionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HaikuEmotionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HaikuEmotionViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    companion object {
        private val HAIKU_TOPIC_COMPARATOR =
            object : DiffUtil.ItemCallback<HaikuEmotion>() {
                override fun areItemsTheSame(
                    oldItem: HaikuEmotion,
                    newItem: HaikuEmotion
                ): Boolean {
                    return oldItem.title == newItem.title
                }

                override fun areContentsTheSame(
                    oldItem: HaikuEmotion,
                    newItem: HaikuEmotion
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }

    inner class HaikuEmotionViewHolder(private val binding: ItemHaikuEmotionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {

            binding.root.setOnClickListener {

                val position = adapterPosition

                if (position != RecyclerView.NO_POSITION) {
                    val poem = getItem(position)
                    listener.onHaikuEmotionClicked(poem)
                }

            }

        }

        fun bind(haiku: HaikuEmotion) {
            binding.apply {

                tvEmotionTitle.text = haiku.title

            }
        }

    }

    interface OnHaikuEmotionClickListener {
        fun onHaikuEmotionClicked(emotion: HaikuEmotion)
    }

}