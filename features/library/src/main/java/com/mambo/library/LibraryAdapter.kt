package com.mambo.library

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mambo.core.OnPoemClickListener
import com.mambo.core.utils.toDate
import com.mambo.data.models.Poem
import com.mambo.library.databinding.ItemPoemLibraryBinding
import org.ocpsoft.prettytime.PrettyTime
import javax.inject.Inject

class LibraryAdapter @Inject constructor() :
    PagingDataAdapter<Poem, LibraryAdapter.PoemViewHolder>(Poem.COMPARATOR) {

    private var mOnClickListener: ((poem: Poem) -> Unit)? = null

    fun onPoemClicked(block: (poem: Poem) -> Unit) {
        mOnClickListener = block
    }

    inner class PoemViewHolder(private val binding: ItemPoemLibraryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val prettyTime = PrettyTime()

        init {
            binding.apply {

                layoutPoemLibraryClick.setOnClickListener {
                    if (absoluteAdapterPosition != RecyclerView.NO_POSITION) {
                        val poem = getItem(absoluteAdapterPosition)
                        poem?.let { mOnClickListener?.invoke(poem) }
                    }
                }

            }
        }

        fun bind(poem: Poem) {
            binding.apply {

                val duration = prettyTime.formatDuration(poem.createdAt.toDate())

                tvPublishedTitle.text = poem.title
                tvPublishedDuration.text = duration

                val color = poem.topic?.color ?: "#FDC29B"
                layoutPoemLibrary.setBackgroundColor(Color.parseColor(color))

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
            ItemPoemLibraryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PoemViewHolder(binding)
    }
}