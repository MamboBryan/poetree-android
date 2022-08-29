package com.mambo.core.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.selection.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.mambo.core.R

fun ViewGroup.getInflater(): LayoutInflater {
    return LayoutInflater.from(this.context)
}

class LazyAdapter<T, R : ViewBinding>(
    comparator: DiffUtil.ItemCallback<Any> = DEFAULT_COMPARATOR
) : ListAdapter<Any, LazyAdapter<T, R>.LazyViewHolder?>(comparator) {

    companion object {
        private const val SELECTION = "LAZY_SELECTION"
        private var DEFAULT_COMPARATOR = object : DiffUtil.ItemCallback<Any>() {
            override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean =
                oldItem == newItem

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean =
                oldItem == newItem
        }
    }

    init {
        setHasStableIds(true)
    }

    private var mCreate: ((parent: ViewGroup) -> R)? = null
    private var mBind: (R.(item: T) -> Unit)? = null
    private var mBindWithSelection: (R.(item: T, selected: Boolean) -> Unit)? = null
    private var mSelected: ((item: T) -> Unit)? = null

    private var tracker: SelectionTracker<Long>? = null

    fun onCreate(create: (parent: ViewGroup) -> R) {
        mCreate = create
    }

    fun onBind(bind: R.(item: T) -> Unit) {
        mBind = bind
    }

    fun onBind(bind: R.(item: T, selected: Boolean) -> Unit) {
        mBindWithSelection = bind
    }

    fun onItemSelected(selected: (item: T) -> Unit) {
        mSelected = selected
    }

    inner class RecyclerViewIdKeyProvider(private val recyclerView: RecyclerView) :
        ItemKeyProvider<Long>(ItemKeyProvider.SCOPE_MAPPED) {

        override fun getKey(position: Int): Long? {
            return recyclerView.adapter?.getItemId(position)
                ?: throw IllegalStateException("RecyclerView adapter is not set!")
        }

        override fun getPosition(key: Long): Int {
            val viewHolder = recyclerView.findViewHolderForItemId(key)
            return viewHolder?.layoutPosition ?: RecyclerView.NO_POSITION
        }
    }

    fun createSelection(
        view: RecyclerView,
        isMultiSelection: Boolean = false,
        block: (item: T, selected: Boolean) -> Unit
    ) {

        val stableIdProvider = RecyclerViewIdKeyProvider(view)
        val lookup = LazyDetailsLookup(view)
        val storageStrategy = StorageStrategy.createLongStorage()

        val builder = SelectionTracker.Builder<Long>(
            SELECTION,
            view,
            stableIdProvider,
            lookup,
            storageStrategy
        ).withSelectionPredicate(
            when (isMultiSelection) {
                true -> SelectionPredicates.createSelectAnything()
                false -> SelectionPredicates.createSelectSingleAnything()
            }
        )

        tracker = builder.build()

        tracker?.addObserver(
            object : SelectionTracker.SelectionObserver<Long>() {
                override fun onItemStateChanged(key: Long, selected: Boolean) {
                    super.onItemStateChanged(key, selected)
                    block.invoke(get(key.toInt()), selected)
                }
            })

    }

    inner class LazyViewHolder(
        val context: Context,
        val binding: R?
    ) : RecyclerView.ViewHolder(binding?.root ?: View(context)) {

        init {

            binding?.let {
                it.root.setOnClickListener {
                    mSelected?.invoke(getItem(absoluteAdapterPosition) as T)
                }
            }

        }

        fun bindHolder(item: T) {
            mBind?.let { block -> binding?.block(item) }
        }

        fun bindHolder(item: T, selected: Boolean) {
            mBindWithSelection?.let { block -> binding?.block(item, selected) }
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> =
            object : ItemDetailsLookup.ItemDetails<Long>() {
                override fun getPosition(): Int = absoluteAdapterPosition
                override fun getSelectionKey(): Long = itemId
            }

    }

    inner class LazyDetailsLookup(private val recyclerView: RecyclerView) :
        ItemDetailsLookup<Long>() {
        override fun getItemDetails(event: MotionEvent): ItemDetails<Long>? {
            val view = recyclerView.findChildViewUnder(event.x, event.y)
            if (view != null) {
                val holder =
                    recyclerView.getChildViewHolder(view) as LazyAdapter<T, R>.LazyViewHolder
                return holder.getItemDetails()
            }
            return null
        }
    }

    private fun getMutableList() = currentList.toMutableList()

    fun get(position: Int) = getItem(position) as T

    fun add(item: T) {
        val list = getMutableList()
        list.add(item)
        submitList(list)
    }

    fun remove(item: T) {
        val list = getMutableList()
        if (!list.contains(item)) return
        list.remove(item)
        submitList(list)
    }

    fun remove(index: Int) {
        val list = getMutableList()
        if (index >= list.size) return
        list.removeAt(index)
        submitList(list)
    }

    override fun getItemId(position: Int): Long = position.toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LazyViewHolder {
        val binding = mCreate?.invoke(parent)
        return LazyViewHolder(parent.context, binding)
    }

    override fun onBindViewHolder(holder: LazyViewHolder, position: Int) {
        val item = getItem(position) as T
        when (tracker == null) {
            true -> holder.bindHolder(item)
            false -> holder.bindHolder(item, tracker!!.isSelected(position.toLong()))
        }
    }

}