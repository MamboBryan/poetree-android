package com.mambo.bookmarks

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mambo.bookmarks.databinding.ItemPoemBookmarkBinding
import com.mambo.core.OnPoemClickListener
import com.mambo.core.utils.toDate
import com.mambo.data.models.Poem
import org.ocpsoft.prettytime.PrettyTime
import javax.inject.Inject

class BookmarksAdapter @Inject constructor() :
    PagingDataAdapter<Poem, BookmarksAdapter.PoemViewHolder>(Poem.COMPARATOR) {

    private var mOnClickListener: ((poem: Poem) -> Unit)? = null

    fun onPoemClicked(block: (poem: Poem) -> Unit) {
        mOnClickListener = block
    }

    inner class PoemViewHolder(private val binding: ItemPoemBookmarkBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val prettyTime = PrettyTime()

        init {
            binding.apply {

                layoutBookmarkClick.setOnClickListener {
                    if (absoluteAdapterPosition != RecyclerView.NO_POSITION) {
                        val poem = getItem(absoluteAdapterPosition)
                        poem?.let { mOnClickListener?.invoke(it) }
                    }
                }

            }
        }

        fun bind(poem: Poem) {
            binding.apply {

                val duration = prettyTime.formatDuration(poem.createdAt.toDate())
                val message = " \u2022 $duration "

                tvPoemDuration.text = message
                tvPoemUser.text = poem.user?.name
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