package com.mambobryan.features.comments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.mambobryan.features.comments.databinding.ItemCommentBinding
import org.ocpsoft.prettytime.PrettyTime


class CommentAdapter :
    ListAdapter<String, CommentAdapter.CommentViewHolder>(COMMENT_COMPARATOR) {

    val images = listOf(
        "https://images.unsplash.com/photo-1519562990232-845beca99938?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1170&q=80",
        "https://images.unsplash.com/photo-1615812214207-34e3be6812df?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8Mnx8cmFjb29ufGVufDB8fDB8fA%3D%3D&auto=format&fit=crop&w=500&q=60",
        "https://images.unsplash.com/photo-1463852247062-1bbca38f7805?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8M3x8bW9ua2V5fGVufDB8fDB8fA%3D%3D&auto=format&fit=crop&w=500&q=60",
        "https://images.unsplash.com/photo-1624807903172-57c657c75fdb?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8Mnx8b3JjYXxlbnwwfHwwfHw%3D&auto=format&fit=crop&w=500&q=60",
        "https://images.unsplash.com/photo-1597953601374-1ff2d5640c85?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8M3x8cGFuZGF8ZW58MHx8MHx8&auto=format&fit=crop&w=500&q=60"
    )

    val names = listOf(
        "LazyLion",
        "RoamingBadger",
        "FrolickingZebra",
        "StandingMongoose",
        "PouncingAntelope"
    )

    val titles = listOf(
        "I thought this sentence was about being in peace and fining joy",
        "No you may have been reading your own stuff lad",
        "What are you guys even talking about",
        "They are making invalid arguments about the authors work and they are getting ti wrong",
        "Who made you moderator?"
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding =
            ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    companion object {
        private val COMMENT_COMPARATOR =
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

    inner class CommentViewHolder(private val binding: ItemCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val prettyTime = PrettyTime()

        init {
            binding.apply {
                ivCommentUser.setOnClickListener { }
                ivCommentActions.setOnClickListener { }
                ivCommentLike.setOnClickListener { }
            }
        }

        fun bind(comment: String) {
            binding.apply {

                tvCommentUser.text = names[absoluteAdapterPosition]
                tvCommentDays.text = times[absoluteAdapterPosition]
                tvCommentContent.text = titles[absoluteAdapterPosition]
                ivCommentUser.load(images[absoluteAdapterPosition])

                ivCommentActions.isVisible = absoluteAdapterPosition % 2 == 0

            }
        }

    }

}