package com.mambobryan.features.artist

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mambobryan.features.artist.databinding.ItemPoemArtistBinding
import org.ocpsoft.prettytime.PrettyTime

class ArtistAdapter :
    ListAdapter<String, ArtistAdapter.ArtistPoemViewHolder>(ARTIST_POEM_COMPARATOR) {

    val titles = listOf(
        "Sleeping Ecstasy",
        "Courageous Grit",
        "The World In Monochrome",
        "The Call to Others",
        "Skipping Relationships",
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
        "#FBEDB1",
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
        "2 hours ago",
        "10 days ago",
        "4 month ago",
        "3 days ago",
        "5 years ago",
        "2 hours ago"
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistPoemViewHolder {
        val binding =
            ItemPoemArtistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArtistPoemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArtistPoemViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    companion object {
        private val ARTIST_POEM_COMPARATOR =
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

    inner class ArtistPoemViewHolder(private val binding: ItemPoemArtistBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val prettyTime = PrettyTime()

        init {

            binding.root.setOnClickListener {

            }

        }

        fun bind(haiku: String) {
            binding.apply {

                tvPoemTitle.text = titles[adapterPosition]
                tvPoemDuration.text = times[adapterPosition]

                layoutPoem.setBackgroundColor(Color.parseColor(colors[adapterPosition]))

            }
        }

    }

}