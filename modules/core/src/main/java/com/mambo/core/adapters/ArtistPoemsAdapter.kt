package com.mambo.core.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mambo.core.OnPoemClickListener
import com.mambo.data.models.Poem
import com.mambo.data.utils.POEM_COMPARATOR
import com.mambo.ui.databinding.ItemPoemArtistBinding
import org.ocpsoft.prettytime.PrettyTime
import javax.inject.Inject

class ArtistPoemsAdapter @Inject constructor() :
    PagingDataAdapter<Poem, ArtistPoemsAdapter.PoemViewHolder>(POEM_COMPARATOR) {

    private lateinit var onPoemClickListener: OnPoemClickListener

    fun setListener(listener: OnPoemClickListener) {
        onPoemClickListener = listener
    }

    inner class PoemViewHolder(private val binding: ItemPoemArtistBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val prettyTime = PrettyTime()

        init {
            binding.apply {

                layoutArtistClick.setOnClickListener {
                    if (absoluteAdapterPosition != RecyclerView.NO_POSITION) {
                        val poem = getItem(absoluteAdapterPosition)
                        if (poem != null)
                            onPoemClickListener.onPoemClicked(poem)
                    }
                }

            }
        }

        fun bind(poem: Poem) {
            binding.apply {

                val duration = prettyTime.formatDuration(poem.createdAt)
                val message = "${poem.topic?.name ?: "Topicless"} \u2022 $duration "

                tvPoemTitle.text = poem.title
                tvPoemDuration.text = message

                val color = poem.topic?.color ?: "#94F292"
                layoutArtistBg.setBackgroundColor(Color.parseColor(color))

            }
        }

    }

    override fun onBindViewHolder(holder: PoemViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null)
            holder.bind(currentItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PoemViewHolder {
        val binding =
            ItemPoemArtistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PoemViewHolder(binding)
    }
}