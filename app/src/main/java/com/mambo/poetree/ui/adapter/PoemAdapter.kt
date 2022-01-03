package com.mambo.poetree.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mambo.data.Poem
import com.mambo.poetree.databinding.ItemPoemBinding
import com.mambo.poetree.utils.GradientUtils
import org.ocpsoft.prettytime.PrettyTime


class PoemAdapter(
    private val listener: OnPoemClickListener
) : ListAdapter<Poem, PoemAdapter.PoemViewHolder>(POEM_COMPARATOR) {

    private val gradientUtils = GradientUtils()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PoemViewHolder {
        val binding =
            ItemPoemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PoemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PoemViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    companion object {
        private val POEM_COMPARATOR =
            object : DiffUtil.ItemCallback<Poem>() {
                override fun areItemsTheSame(
                    oldItem: Poem,
                    newItem: Poem
                ): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(
                    oldItem: Poem,
                    newItem: Poem
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }

    inner class PoemViewHolder(private val binding: ItemPoemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val prettyTime = PrettyTime()

        init {

            binding.root.setOnClickListener {

                val position = adapterPosition

                if (position != RecyclerView.NO_POSITION) {
                    val poem = getItem(position)
                    listener.onPoemClicked(poem)
                }

            }

        }

        fun bind(poem: Poem) {
            binding.apply {

                tvPoemUser.text = poem.user.username
                tvPoemDate.text = prettyTime.format(poem.date)
                tvPoemTitle.text = poem.title

                val background = GradientUtils.getDefaultPoemBackground()
                ivPoemImage.setImageDrawable(background)

            }
        }

    }

    interface OnPoemClickListener {
        fun onPoemClicked(poem: Poem)
    }

}