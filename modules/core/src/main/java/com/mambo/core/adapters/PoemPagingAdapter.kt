package com.mambo.core.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mambo.core.OnPoemClickListener
import com.mambo.core.utils.prettyCount
import com.mambo.core.utils.toDate
import com.mambo.data.models.Poem
import com.mambo.ui.databinding.ItemPoemBinding
import org.ocpsoft.prettytime.PrettyTime
import javax.inject.Inject

class PoemPagingAdapter @Inject constructor() :
    PagingDataAdapter<Poem, PoemPagingAdapter.PoemViewHolder>(Poem.COMPARATOR) {

    private var mOnClickListener: ((poem: Poem) -> Unit)? = null

    fun onPoemClicked(block: (poem: Poem) -> Unit) {
        mOnClickListener = block
    }

    inner class PoemViewHolder(private val binding: ItemPoemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val prettyTime = PrettyTime()

        init {
            binding.apply {

                layoutPoemClick.setOnClickListener {
                    if (absoluteAdapterPosition != RecyclerView.NO_POSITION) {
                        val poem = getItem(absoluteAdapterPosition)
                        if (poem != null) mOnClickListener?.invoke(poem)
                    }
                }

            }
        }

        fun bind(poem: Poem) {

            val context = binding.root.context

            binding.apply {

                val date = poem.updatedAt.toDate()
                val duration = prettyTime.formatDuration(date)
                val message = " \u2022 $duration "

                tvPoemUsername.text = poem.user?.name
                tvPoemTitle.text = poem.title
                tvPoemDate.text = message

                val likeIcon = when (poem.liked) {
                    true -> com.mambo.ui.R.drawable.liked
                    false -> com.mambo.ui.R.drawable.unliked
                }
                tvLikes.apply {
                    text = prettyCount(poem.likes)
                    setCompoundDrawablesRelativeWithIntrinsicBounds(
                        likeIcon,
                        0,
                        0,
                        0
                    )
                }

                val commentIcon = when (poem.commented) {
                    true -> com.mambo.ui.R.drawable.commented
                    false -> com.mambo.ui.R.drawable.uncommented
                }
                tvComments.apply {
                    text = prettyCount(poem.comments)
                    setCompoundDrawablesRelativeWithIntrinsicBounds(
                        commentIcon,
                        0,
                        0,
                        0
                    )
                }

                val bookmarkIcon = when (poem.bookmarked) {
                    true -> com.mambo.ui.R.drawable.bookmarked
                    false -> com.mambo.ui.R.drawable.unbookmarked
                }
                tvBookmarks.apply {
                    text = prettyCount(poem.bookmarks)
                    setCompoundDrawablesRelativeWithIntrinsicBounds(
                        bookmarkIcon,
                        0,
                        0,
                        0
                    )
                }

                val readIcon = when (poem.read) {
                    true -> com.mambo.ui.R.drawable.read
                    false -> com.mambo.ui.R.drawable.unread
                }
                tvReads.apply {
                    text = prettyCount(poem.reads)
                    setCompoundDrawablesRelativeWithIntrinsicBounds(
                        readIcon,
                        0,
                        0,
                        0
                    )
                }


                val color = poem.topic?.color ?: "#fefefe"
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