package com.mambo.poetree.ui.compose

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mambo.data.Emotion
import com.mambo.poetree.R
import com.mambo.poetree.databinding.ItemEmotionBinding
import com.mambo.poetree.utils.EmojiUtils
import com.mambo.poetree.utils.EmotionsUtils
import java.util.*
import kotlin.properties.Delegates

class EmotionAdapter(
    val emotion: Emotion?,
    val listener: OnEmotionClickListener
) : ListAdapter<Emotion, EmotionAdapter.EmotionViewHolder>(
    EmotionsUtils.EMOTION_COMPARATOR
) {

    var selectedPosition by Delegates.observable(currentList.indexOf(emotion)) { _, oldPos, newPos ->
        if (newPos in currentList.indices) {
            notifyItemChanged(oldPos)
            notifyItemChanged(newPos)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmotionViewHolder {
        val binding =
            ItemEmotionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EmotionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EmotionViewHolder, position: Int) {

        val item = getItem(position)
        val isSelected = position == selectedPosition

        if (item != null)
            holder.bind(item, isSelected)
    }

    inner class EmotionViewHolder(
        val binding: ItemEmotionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {

            binding.root.setOnClickListener {

                val position = adapterPosition

                if (position != RecyclerView.NO_POSITION) {

                    val item = currentList[position]

                    listener.onEmotionSelected(item)
                    selectedPosition = position

                }
            }

        }

        fun bind(emotion: Emotion, isSelected: Boolean) {
            binding.apply {
                tvEmotionEmoji.text = EmojiUtils.getEmojiByUnicode(emotion.emoji)
                tvEmotionName.text = emotion.name.capitalize(Locale.ROOT)

                if (isSelected) {
                    val context = root.context

                    tvEmotionName.setTextColor(context.resources.getColor(R.color.color_on_primary))
                    cardEmotion.setCardBackgroundColor(
                        context.resources.getColor(R.color.color_primary)
                    )
                }
            }
        }

    }

    interface OnEmotionClickListener {
        fun onEmotionSelected(emotion: Emotion)
    }

}