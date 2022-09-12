package com.mambo.core.adapters

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.ColorDrawable
import android.view.View
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
    val comparator: DiffUtil.ItemCallback<T>
) : PagingDataAdapter<T, LazyPagingAdapter<T, R>.LazyViewHolder>(comparator) {

    private var mCreate: ((parent: ViewGroup) -> R)? = null
    private var mBind: (R.(item: T) -> Unit)? = null
    private var mBindSelected: (R.(item: T, selected: Boolean) -> Unit)? = null

    private var onItemClicked: ((item: T) -> Unit)? = null
    private var onItemLongClicked: ((item: T) -> Boolean)? = null
    private var onItemsSelected: ((items: List<T?>) -> Unit)? = null
    private var onItemSelected: ((item: T?) -> Unit)? = null

    private val selectedItems = mutableListOf<Long>()

    inner class LazyViewHolder(
        val context: Context,
        val binding: R?
    ) : RecyclerView.ViewHolder(binding?.root ?: View(context)) {

        init {

            binding?.let {
                it.root.setOnClickListener {

                    // ON CLICK
                    onItemClicked?.invoke(getItem(absoluteAdapterPosition) as T)

                    // CHECK SELECTIONS AND TOGGLE
                    if (onItemSelected != null || onItemsSelected != null)
                        when (selectedItems.contains(absoluteAdapterPosition.toLong())) {
                            true -> removeSelection(absoluteAdapterPosition)
                            false -> addSelection(absoluteAdapterPosition)
                        }

                    // ON SINGLE ITEM SELECTED
                    onItemSelected?.let { mSelect ->
                        val item = selectedItems.map { position -> getItem(position.toInt()) }
                            .firstOrNull()
                        mSelect.invoke(item)
                    }

                    // ON MULTIPLE ITEM SELECTED
                    onItemsSelected?.let { mSelects ->
                        val list = selectedItems.map { position -> getItem(position.toInt()) }
                        mSelects.invoke(list)
                    }

                }
                it.root.setOnLongClickListener {
                    getItem(absoluteAdapterPosition)?.let { item ->
                        onItemLongClicked?.invoke(item)
                    } ?: false
                }
            }

        }

        fun bindHolder(item: T) {
            mBind?.let { block -> binding?.block(item) }
        }

        fun bindHolder(item: T, selected: Boolean) {
            mBindSelected?.let { block -> binding?.block(item, selected) }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LazyViewHolder {
        val binding = mCreate?.invoke(parent)
        return LazyViewHolder(parent.context, binding)
    }

    override fun onBindViewHolder(holder: LazyViewHolder, position: Int) {
        val item = getItem(position) as T
        if (onItemSelected != null || onItemsSelected != null)
            holder.bindHolder(item, selectedItems.contains(position.toLong()))
        else
            holder.bindHolder(item)
    }

    /**
     * CREATE VIEWS
     */

    fun onCreate(create: (parent: ViewGroup) -> R) {
        mCreate = create
    }

    /**
     * INVOKE BINDINGS
     */

    fun onBind(bind: R.(item: T) -> Unit) {
        mBind = bind
    }

    fun onBind(bind: R.(item: T, selected: Boolean) -> Unit) {
        mBindSelected = bind
    }

    /**
     * ADAPTER SELECTIONS, CLICKS, SWIPES
     */

    fun onItemClicked(block: ((item: T) -> Unit)? = null) {
        onItemClicked = block
    }

    fun onItemLongClicked(block: ((item: T) -> Boolean)? = null) {
        onItemLongClicked = block
    }

    fun onItemSelected(block: ((item: T?) -> Unit)? = null) {
        onItemSelected = block
    }

    fun onItemsSelected(block: ((items: List<T?>) -> Unit)? = null) {
        onItemsSelected = block
    }

    fun onSwipedRight(
        @DrawableRes icon: Int? = null,
        @ColorRes iconColor: Int? = null,
        @ColorRes color: Int? = null,
        remove: Boolean = false,
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
                val position = viewHolder.absoluteAdapterPosition
                getItem(position)?.let {
                    when (remove) {
                        true -> remove(position)
                        false -> notifyItemChanged(position)
                    }
                    swiped.invoke(it)
                }
            }

        }
        ItemTouchHelper(swiper).attachToRecyclerView(view)
    }

    fun onSwipedLeft(
        @DrawableRes icon: Int? = null,
        @ColorRes iconColor: Int? = null,
        @ColorRes color: Int? = null,
        remove: Boolean = true,
        view: RecyclerView,
        swiped: (item: T) -> Unit
    ) {
        val fields = LazySwipeFields(
            drawable = icon,
            iconColor = iconColor,
            background = color
        )
        val swiper = object : SwipeLeft(context = view.context, lazyField = fields) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.absoluteAdapterPosition
                getItem(position)?.let {
                    when (remove) {
                        true -> remove(position)
                        false -> notifyItemChanged(position)
                    }
                    swiped.invoke(it)
                }
            }

        }
        ItemTouchHelper(swiper).attachToRecyclerView(view)
    }

    private fun getMutableList(): MutableList<T> = this.snapshot().filterNotNull().toMutableList()

    private fun updatedPagedData(list: List<T>): PagingData<T> = PagingData.from(list)

    private fun addSelection(position: Int) {
        if (selectedItems.contains(position.toLong())) return
        val previousPosition = selectedItems.firstOrNull()
        if (onItemSelected != null) selectedItems.clear()
        selectedItems.add(position.toLong())
        previousPosition?.toInt()?.let { notifyItemChanged(it) }
        notifyItemChanged(position)
    }

    private fun removeSelection(position: Int) {
        if (!selectedItems.contains(position.toLong())) return
        selectedItems.remove(position.toLong())
        notifyItemChanged(position)
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
    ) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

        private val icon =
            ContextCompat.getDrawable(
                context,
                lazyField?.drawable ?: com.mambo.core.R.drawable.ic_baseline_edit_24
            )

        private val intrinsicWidth = icon?.intrinsicWidth ?: 0
        private val intrinsicHeight = icon?.intrinsicHeight ?: 0

        private val background = ColorDrawable()
        private val backgroundColor =
            ContextCompat.getColor(
                context,
                lazyField?.background ?: com.mambo.core.R.color.colorAccent
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
            if (viewHolder.absoluteAdapterPosition == RecyclerView.NO_POSITION) return 0
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

            // Draw the background
            background.apply {
                color = backgroundColor
                setBounds(
                    itemView.left,
                    itemView.top,
                    itemView.left + dX.toInt(),
                    itemView.bottom
                )
                draw(c)
            }

            // Calculate position of the icon
            val iconMargin = (itemHeight - intrinsicHeight) / 2
            val iconTop = itemView.top + (itemHeight - intrinsicHeight) / 2
            val iconBottom = iconTop + intrinsicHeight
            val iconRight = itemView.left + iconMargin + intrinsicWidth
            val iconLeft = itemView.left + iconMargin

            // Draw the icon
            icon?.setTint(
                ContextCompat.getColor(context, lazyField?.iconColor ?: android.R.color.white)
            )
            icon?.setBounds(iconLeft, iconTop, iconRight, iconBottom)
            icon?.draw(c)

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }

        private fun clearCanvas(c: Canvas?, left: Float, top: Float, right: Float, bottom: Float) {
            c?.drawRect(left, top, right, bottom, clearPaint)
        }

    }

    private abstract inner class SwipeLeft(
        private val context: Context,
        private val lazyField: LazySwipeFields?
    ) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

        private val icon =
            ContextCompat.getDrawable(
                context,
                lazyField?.drawable ?: com.mambo.core.R.drawable.ic_baseline_delete_24
            )

        private val intrinsicWidth = icon?.intrinsicWidth ?: 0
        private val intrinsicHeight = icon?.intrinsicHeight ?: 0
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
            if (viewHolder.absoluteAdapterPosition == RecyclerView.NO_POSITION) return 0
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

            // Draw the background
            background.apply {
                color = backgroundColor
                setBounds(
                    itemView.right + dX.toInt(),
                    itemView.top,
                    itemView.right,
                    itemView.bottom
                )
                draw(c)
            }


            // Calculate position of the icon
            val deleteIconTop = itemView.top + (itemHeight - intrinsicHeight) / 2
            val deleteIconMargin = (itemHeight - intrinsicHeight) / 2
            val deleteIconLeft = itemView.right - deleteIconMargin - intrinsicWidth
            val deleteIconRight = itemView.right - deleteIconMargin
            val deleteIconBottom = deleteIconTop + intrinsicHeight

            // Draw the the icon
            icon?.setTint(
                ContextCompat.getColor(context, lazyField?.iconColor ?: android.R.color.white)
            )
            icon?.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
            icon?.draw(c)

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }

        private fun clearCanvas(c: Canvas?, left: Float, top: Float, right: Float, bottom: Float) {
            c?.drawRect(left, top, right, bottom, clearPaint)
        }

    }

}