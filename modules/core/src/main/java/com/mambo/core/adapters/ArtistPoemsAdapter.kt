package com.mambo.core.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mambo.core.OnPoemClickListener
import com.mambo.core.utils.toDateTime
import com.mambo.data.models.Poem
import com.mambo.ui.databinding.ItemPoemArtistBinding
import org.ocpsoft.prettytime.PrettyTime
import java.util.*
import javax.inject.Inject

class ArtistPoemsAdapter @Inject constructor() :
    PagingDataAdapter<Poem, ArtistPoemsAdapter.PoemViewHolder>(Poem.COMPARATOR) {

    private var mOnClickListener : ((poem: Poem) -> Unit)? = null

    fun onPoemClicked(block : (poem: Poem) -> Unit){
        mOnClickListener = block
    }

    inner class PoemViewHolder(private val binding: ItemPoemArtistBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val prettyTime = PrettyTime()

        init {
            binding.apply {

                layoutArtistClick.setOnClickListener {
                    if (absoluteAdapterPosition != RecyclerView.NO_POSITION) {
                        val poem = getItem(absoluteAdapterPosition)
                        if (poem != null) mOnClickListener?.invoke(poem)
                    }
                }

            }
        }

        fun bind(poem: Poem) {
            binding.apply {

                val topic = poem.topic?.name?.replaceFirstChar { it.uppercase() } ?: "Topicless"

                val date = Date(poem.createdAt.toDateTime() ?: 0)
                val duration = prettyTime.formatDuration(date)
                val message = "$topic  \u2022  $duration "

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