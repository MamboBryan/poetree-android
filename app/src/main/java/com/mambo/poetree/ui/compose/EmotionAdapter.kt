package com.mambo.poetree.ui.compose

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mambo.poetree.data.model.Emotion
import com.mambo.poetree.databinding.ItemEmotionBinding
import com.mambo.poetree.utils.EmojiUtils
import com.mambo.poetree.utils.EmotionsUtils
import java.util.*

class EmotionAdapter : ListAdapter<Emotion, EmotionAdapter.EmotionViewHolder>(
    EmotionsUtils.EMOTION_COMPARATOR
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmotionViewHolder {
        val binding =
            ItemEmotionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EmotionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EmotionViewHolder, position: Int) {
        val item = getItem(position)

        if (item != null)
            holder.bind(item)
    }

    inner class EmotionViewHolder(
        val binding: ItemEmotionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(emotion: Emotion) {
            binding.apply {
                tvEmotionEmoji.text = EmojiUtils.getEmojiByUnicode(emotion.emoji)
                tvEmotionName.text = emotion.name.capitalize(Locale.ROOT)
            }
        }

    }

}