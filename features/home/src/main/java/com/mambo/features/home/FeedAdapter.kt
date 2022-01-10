package com.mambo.features.home

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mambo.ui.databinding.ItemPoemDetailedBinding
import org.ocpsoft.prettytime.PrettyTime
import kotlin.random.Random


class FeedAdapter : ListAdapter<String, FeedAdapter.FeedViewHolder>(FEED_COMPARATOR) {

    val names = listOf(
        "ObiliousKoala",
        "ShadyRacoon",
        "ThriftyMonkey",
        "DeviousOrca",
        "CuriousPanda"
    )

    val titles = listOf(
        "The Summit Of The Trees",
        "Deceiving Humans",
        "Dripping With Excellence",
        "The Fool Ain't A Fool, Till Fooled",
        "The Impact of Global Warming"
    )

    val colors = listOf(
        "#AFFEB9",
        "#F3BACD",
        "#FBEDB1",
        "#E4B4E2",
        "#D6D6D6"
    )

    val times = listOf(
        "2 days ago",
        "a month ago",
        "28 days ago",
        "2 years ago",
        "a day ago"
    )

    val images = listOf(
        "https://images.unsplash.com/photo-1519562990232-845beca99938?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1170&q=80",
        "https://images.unsplash.com/photo-1615812214207-34e3be6812df?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8Mnx8cmFjb29ufGVufDB8fDB8fA%3D%3D&auto=format&fit=crop&w=500&q=60",
        "https://images.unsplash.com/photo-1463852247062-1bbca38f7805?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8M3x8bW9ua2V5fGVufDB8fDB8fA%3D%3D&auto=format&fit=crop&w=500&q=60",
        "https://images.unsplash.com/photo-1624807903172-57c657c75fdb?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8Mnx8b3JjYXxlbnwwfHwwfHw%3D&auto=format&fit=crop&w=500&q=60",
        "https://images.unsplash.com/photo-1597953601374-1ff2d5640c85?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8M3x8cGFuZGF8ZW58MHx8MHx8&auto=format&fit=crop&w=500&q=60"
    )

    private lateinit var listener: OnFeedPoemClicked
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val binding =
            ItemPoemDetailedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FeedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }
    
    fun setClickListener(listener: OnFeedPoemClicked){
        this.listener = listener
    }

    companion object {
        private val FEED_COMPARATOR =
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

    inner class FeedViewHolder(private val binding: ItemPoemDetailedBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val prettyTime = PrettyTime()

        init {

            binding.layoutPoem.setOnClickListener {
                if(adapterPosition != RecyclerView.NO_POSITION){
                    listener.onPoemClicked(getItem(adapterPosition))
                }
            }

        }

        fun bind(haiku: String) {
            binding.apply {

                val duration = times[adapterPosition]
                val comments = Random.nextInt(100, 10000)

                val message =
                    "  \u2022  $duration"

                tvPoemUser.text = names[adapterPosition]
                tvPoemTitle.text = titles[adapterPosition]
                tvPoemDuration.text = message

                layoutPoem.setBackgroundColor(Color.parseColor(colors[adapterPosition]))

            }
        }

    }

    interface OnFeedPoemClicked {
        fun onPoemClicked(poem: String)
    }

}