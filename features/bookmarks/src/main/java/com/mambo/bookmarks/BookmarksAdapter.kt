package com.mambo.bookmarks

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mambo.bookmarks.databinding.ItemPoemBookmarkBinding
import com.mambo.core.OnPoemClickListener
import com.mambo.data.models.Poem
import com.mambo.data.utils.POEM_COMPARATOR
import org.ocpsoft.prettytime.PrettyTime
import javax.inject.Inject

class BookmarksAdapter @Inject constructor() :
    PagingDataAdapter<Poem, BookmarksAdapter.PoemViewHolder>(POEM_COMPARATOR) {

    private lateinit var onPoemClickListener: OnPoemClickListener

    fun setListener(listener: OnPoemClickListener) {
        onPoemClickListener = listener
    }

    inner class PoemViewHolder(private val binding: ItemPoemBookmarkBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val prettyTime = PrettyTime()

        init {
            binding.apply {

                layoutBookmarkClick.setOnClickListener {
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
                val message = " \u2022 $duration "

                tvPoemDuration.text = message
                tvPoemUser.text = poem.user?.username
                tvPoemTitle.text = poem.title

                val color = poem.topic?.color ?: "#F7DEE6"
                layoutBookmarkBg.setBackgroundColor(Color.parseColor(color))

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
            ItemPoemBookmarkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PoemViewHolder(binding)
    }
}