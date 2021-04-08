package com.mambo.poetree.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mambo.poetree.data.model.Poem
import com.mambo.poetree.databinding.ItemPoemBinding
import com.mambo.poetree.utils.GradientUtils

class PoemAdapter : RecyclerView.Adapter<PoemAdapter.PoemViewHolder>() {

    private val poems = ArrayList<Poem>()
    private val gradientUtils = GradientUtils()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PoemViewHolder {
        val binding =
            ItemPoemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PoemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PoemViewHolder, position: Int) {
        val currentItem = poems[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int {
        return poems.size
    }

    fun insert(poems: ArrayList<Poem>) {
        this.poems.addAll(poems)
        notifyDataSetChanged()
    }

    inner class PoemViewHolder(private val binding: ItemPoemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(poem: Poem) {
            binding.apply {

                tvPoemUser.text = poem.user.username
                tvPoemDate.text = "$adapterPosition days ago"
                tvPoemTitle.text = poem.title
                ivPoemImage.setImageDrawable(
                    gradientUtils.getGradientBackground(adapterPosition)
                )

            }
        }

    }
}