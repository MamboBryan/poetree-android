package com.mambo.library

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mambo.core.OnPoemClickListener
import com.mambo.library.databinding.ItemPoemLibraryPublishedBinding
import org.ocpsoft.prettytime.PrettyTime


class PublishedLibraryAdapter :
    ListAdapter<String, PublishedLibraryAdapter.PublishedViewHolder>(LIBRARY_COMPARATOR) {

    val titles = listOf(
        "Smorfs In Town",
        "Emanating Bliss ",
        "The Sigh Thigh",
        "Cultural Amnesty",
        "A Glimpse Of Hope",
        "Smorfs In Town",
        "Emanating Bliss ",
        "The Sigh Thigh",
        "Cultural Amnesty",
        "A Glimpse Of Hope"
    )

    val colors = listOf(
        "#E4B4E2",
        "#AFFEB9",
        "#F3BACD",
        "#FBEDB1",
        "#D6D6D6",
        "#E4B4E2",
        "#AFFEB9",
        "#F3BACD",
        "#FBEDB1",
        "#D6D6D6"
    )

    val times = listOf(
        "5 hours ago",
        "8 days ago",
        "7 months ago",
        "2 years ago",
        "3 years ago",
        "5 hours ago",
        "8 days ago",
        "7 months ago",
        "2 years ago",
        "3 years ago"
    )

    private lateinit var clickListener: OnPoemClickListener

    fun setListener(listener: OnPoemClickListener) {
        clickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PublishedViewHolder {
        val binding =
            ItemPoemLibraryPublishedBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return PublishedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PublishedViewHolder, position: Int) {
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

    inner class PublishedViewHolder(private val binding: ItemPoemLibraryPublishedBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val prettyTime = PrettyTime()

        init {

            binding.root.setOnClickListener {
                if(adapterPosition != RecyclerView.NO_POSITION)
                clickListener.onPoemClicked(getItem(adapterPosition))
            }

        }

        fun bind(haiku: String) {
            binding.apply {

                tvPublishedTitle.text = titles[adapterPosition]
                tvPublishedDuration.text = times[adapterPosition]

                layoutPoem.setBackgroundColor(Color.parseColor(colors[adapterPosition]))
//                layoutPublish.setBackgroundColor(Color.parseColor("#FFC285"))

            }
        }

    }

}