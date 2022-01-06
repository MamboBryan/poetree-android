package com.mambo.library

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mambo.library.databinding.ItemPoemLibraryUnpublishedBinding
import org.ocpsoft.prettytime.PrettyTime


class UnpublishedLibraryAdapter :
    ListAdapter<String, UnpublishedLibraryAdapter.PoemBookmarkViewHolder>(LIBRARY_COMPARATOR) {

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
        "#D6D6D6",
        "#AFFEB9",
        "#FBEDB1"
    )

    val times = listOf(
        "an hour ago",
        "2 days ago",
        "7 days ago",
        "3 months ago",
        "8 years ago"
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PoemBookmarkViewHolder {
        val binding =
            ItemPoemLibraryUnpublishedBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return PoemBookmarkViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PoemBookmarkViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    companion object {
        private val LIBRARY_COMPARATOR =
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

    inner class PoemBookmarkViewHolder(private val binding: ItemPoemLibraryUnpublishedBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val prettyTime = PrettyTime()

        init {

            binding.root.setOnClickListener {

            }

        }

        fun bind(haiku: String) {
            binding.apply {

                tvTitle.text = titles[adapterPosition]
                tvDuration.text = times[adapterPosition]

                val color =
                    if (adapterPosition % 2 == 0)
                        ContextCompat.getColor(layoutPoem.context, R.color.white)
                    else Color.parseColor(colors[adapterPosition])

                layoutPoem.setBackgroundColor(color)
//                layoutPublish.setBackgroundColor(Color.parseColor("#FFC285"))

            }
        }

    }

}