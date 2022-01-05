package com.mambo.bookmarks

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mambo.bookmarks.databinding.ItemPoemBookmarkBinding
import org.ocpsoft.prettytime.PrettyTime


class BookmarkAdapter :
    ListAdapter<String, BookmarkAdapter.PoemBookmarkViewHolder>(BOOKMARK_COMPARATOR) {

    val names = listOf(
        "LazyLion",
        "RoamingBadger",
        "FrolickingZebra",
        "StandingMongoose",
        "PouncingAntelope"
    )

    val titles = listOf(
        "Sleeping Ecstasy",
        "Courageous Grit",
        "The World In Monochrome",
        "The Call to Others",
        "Skipping Relationships"
    )

    val colors = listOf(
        "#F3BACD",
        "#E4B4E2",
        "#AFFEB9",
        "#D6D6D6",
        "#FBEDB1"
    )

    val times = listOf(
        "10 days ago",
        "4 month ago",
        "3 days ago",
        "5 years ago",
        "2 hours ago"
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PoemBookmarkViewHolder {
        val binding =
            ItemPoemBookmarkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PoemBookmarkViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PoemBookmarkViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    companion object {
        private val BOOKMARK_COMPARATOR =
            object : DiffUtil.ItemCallback<String>() {
                override fun areItemsTheSame(
                    oldItem: String,
                    newItem: String
                ): Boolean {
                    return oldItem == newItem
                }

                override fun areContentsTheSame(
                    oldItem: String,
                    newItem: String
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }

    inner class PoemBookmarkViewHolder(private val binding: ItemPoemBookmarkBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val prettyTime = PrettyTime()

        init {

            binding.root.setOnClickListener {

            }

        }

        fun bind(haiku: String) {
            binding.apply {

                val duration = times[adapterPosition]
                val message = " \u2022 $duration "

                tvPoemUser.text = names[adapterPosition]
                tvPoemTitle.text = titles[adapterPosition]
                tvPoemDuration.text = message

                layoutPoem.setBackgroundColor(Color.parseColor(colors[adapterPosition]))

            }
        }

    }

}