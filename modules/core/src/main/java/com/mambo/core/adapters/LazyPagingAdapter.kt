package com.mambo.core.adapters

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LazyPagingAdapter<T : Any, R : ViewBinding>(
    val create: (parent: ViewGroup) -> R,
    val bind: (R.(item: T) -> Unit)? = null,
    val selected: ((item: T) -> Unit)? = null,
    val comparator: DiffUtil.ItemCallback<T>
) : PagingDataAdapter<T, LazyPagingAdapter<T, R>.LazyViewHolder>(comparator) {

    private var onSwipedRight: ((item: T) -> Unit)? = null
    private var onSwipedLeft: ((item: T) -> Unit)? = null

    inner class LazyViewHolder(val binding: R) : RecyclerView.ViewHolder(binding.root) {

        init {

            binding.root.setOnClickListener {
                selected?.invoke(getItem(absoluteAdapterPosition) as T)
            }

        }

        fun bindHolder(item: T) {
            bind?.let { block -> binding.block(item) }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LazyViewHolder {
        val binding = create.invoke(parent)
        return LazyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LazyViewHolder, position: Int) {
        val item = getItem(position) as T
        holder.bindHolder(item)
    }

    private fun getMutableList(): MutableList<T> = this.snapshot().filterNotNull().toMutableList()

    private fun updatedPagedData(list: List<T>): PagingData<T> = PagingData.from(list)

    fun onSwipedRight(
        @DrawableRes icon: Int? = null,
        @ColorRes iconColor: Int? = null,
        @ColorRes color: Int? = null,
        view: RecyclerView,
        swiped: (item: T) -> Unit
    ) {
        val fields = LazySwipeFields(
            drawable = icon,
            iconColor = iconColor,
            background = color
        )
        val swiper = object : SwipeRight(context = view.context, lazyField = fields) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                getItem(viewHolder.absoluteAdapterPosition)?.let {
                    swiped.invoke(it)
                    notifyItemChanged(viewHolder.absoluteAdapterPosition)
                }
            }

        }
        ItemTouchHelper(swiper).attachToRecyclerView(view)
    }

    fun onSwipedLeft(swiped: (item: T) -> Unit) {
        onSwipedLeft = swiped
    }

    fun add(item: T) {
        val list: MutableList<T> = getMutableList()
        list.add(item)
        updateList(list)
    }

    fun remove(item: T) {
        val list = getMutableList()
        if (!list.contains(item)) return
        list.remove(item)
        updateList(list)
    }

    fun remove(index: Int) {
        val list = getMutableList()
        if (index >= list.size) return
        list.removeAt(index)
        updateList(list)
    }

    private fun updateList(list: List<T>) = CoroutineScope(Dispatchers.Main).launch {
        submitData(updatedPagedData(list))
    }

    inner class LazySwipeFields(
        @DrawableRes val drawable: Int?,
        @ColorRes val iconColor: Int?,
        @ColorRes val background: Int?
    )

    private abstract inner class SwipeRight(
        private val context: Context,
        private val lazyField: LazySwipeFields?
    ) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

        private val deleteIcon =
            ContextCompat.getDrawable(
                context,
                lazyField?.drawable ?: com.mambo.core.R.drawable.ic_baseline_delete_24
            )

        private val intrinsicWidth = deleteIcon?.intrinsicWidth ?: 0
        private val intrinsicHeight = deleteIcon?.intrinsicHeight ?: 0
        private val background = ColorDrawable()
        private val backgroundColor =
            ContextCompat.getColor(
                context,
                lazyField?.background ?: com.mambo.core.R.color.error
            )
        private val clearPaint =
            Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            if (viewHolder.absoluteAdapterPosition == 10) return 0
            return super.getMovementFlags(recyclerView, viewHolder)
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {

            val itemView = viewHolder.itemView
            val itemHeight = itemView.bottom - itemView.top
            val isCanceled = dX == 0f && !isCurrentlyActive

            if (isCanceled) {
                clearCanvas(
                    c,
                    itemView.right + dX,
                    itemView.top.toFloat(),
                    itemView.right.toFloat(),
                    itemView.bottom.toFloat()
                )
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                return
            }

            // Draw the red delete background
            background.color = backgroundColor
            background.setBounds(
                itemView.right + dX.toInt(),
                itemView.top,
                itemView.right,
                itemView.bottom
            )
            background.draw(c)

            // Calculate position of delete icon
            val deleteIconTop = itemView.top + (itemHeight - intrinsicHeight) / 2
            val deleteIconMargin = (itemHeight - intrinsicHeight) / 2
            val deleteIconLeft = itemView.right - deleteIconMargin - intrinsicWidth
            val deleteIconRight = itemView.right - deleteIconMargin
            val deleteIconBottom = deleteIconTop + intrinsicHeight

            // Draw the delete icon
            deleteIcon?.setTint(
                ContextCompat.getColor(context, lazyField?.iconColor ?: android.R.color.white)
            )
            deleteIcon?.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
            deleteIcon?.draw(c)

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }

        private fun clearCanvas(c: Canvas?, left: Float, top: Float, right: Float, bottom: Float) {
            c?.drawRect(left, top, right, bottom, clearPaint)
        }

    }

}