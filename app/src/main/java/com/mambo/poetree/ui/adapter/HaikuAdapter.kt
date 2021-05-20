package com.mambo.poetree.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mambo.poetree.data.model.Haiku
import com.mambo.poetree.databinding.ItemHaikuBinding
import com.mambo.poetree.utils.GradientUtils
import org.ocpsoft.prettytime.PrettyTime


class HaikuAdapter(
    private val listener: OnHaikuClickListener
) : ListAdapter<Haiku, HaikuAdapter.HaikuViewHolder>(HAKIU_COMPARATOR) {

    private val gradientUtils = GradientUtils()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HaikuViewHolder {
        val binding =
            ItemHaikuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HaikuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HaikuViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    companion object {
        private val HAKIU_COMPARATOR =
            object : DiffUtil.ItemCallback<Haiku>() {
                override fun areItemsTheSame(
                    oldItem: Haiku,
                    newItem: Haiku
                ): Boolean {
                    return oldItem.title == newItem.title
                }

                override fun areContentsTheSame(
                    oldItem: Haiku,
                    newItem: Haiku
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }

    inner class HaikuViewHolder(private val binding: ItemHaikuBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val prettyTime = PrettyTime()

        init {

            binding.root.setOnClickListener {

                val position = adapterPosition

                if (position != RecyclerView.NO_POSITION) {
                    val poem = getItem(position)
                    listener.onHaikuClicked(poem)
                }

            }

        }

        fun bind(haiku: Haiku) {
            binding.apply {

                tvHaikuTitle.text = haiku.title
                tvHaikuPoet.text = haiku.poet

                val background =
                    GradientUtils.getDefaultPoemBackground()

                ivHaikuEmotion.setImageDrawable(background)

            }
        }

    }

    interface OnHaikuClickListener {
        fun onHaikuClicked(haiku: Haiku)
    }

}