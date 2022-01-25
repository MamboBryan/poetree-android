package com.mambo.core.adapters

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mambo.core.OnPoemClickListener
import com.mambo.data.models.Poem
import com.mambo.data.utils.POEM_COMPARATOR
import com.mambo.ui.databinding.ItemPoemBinding
import org.ocpsoft.prettytime.PrettyTime

class PoemPagingAdapter :
    PagingDataAdapter<Poem, PoemPagingAdapter.PoemViewHolder>(POEM_COMPARATOR) {

    private lateinit var onPoemClickListener: OnPoemClickListener

    fun setListener(listener: OnPoemClickListener) {
        onPoemClickListener = listener
    }

    inner class PoemViewHolder(private val binding: ItemPoemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val prettyTime = PrettyTime()

        init {
            binding.apply {

                layoutPoemClick.setOnClickListener {
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

                Log.i("FEEDS", "poem bind called :: ${poem.title}")

                val duration = prettyTime.formatDuration(poem.createdAt)
                val message = " \u2022 $duration "

                tvPoemUsername.text = poem.user?.username
                tvPoemTitle.text = poem.title
                tvPoemDate.text = message

                tvLikes.text = "${poem.likesCount}"
                tvComments.text = "${poem.commentsCount}"
                tvBookmarks.text = "${poem.bookmarksCount}"
                tvReads.text = "${poem.readsCount}"

                val color = poem.topic?.color ?: "#ffffff"
                layoutPoem.setBackgroundColor(Color.parseColor(color))

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
            ItemPoemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PoemViewHolder(binding)
    }
}