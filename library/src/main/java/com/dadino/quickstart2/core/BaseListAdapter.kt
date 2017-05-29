package com.dadino.quickstart2.core

import android.content.Context
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java.util.*

abstract class BaseListAdapter<ITEM, HOLDER : BaseHolder<ITEM>> : BaseAdapter<ITEM, HOLDER>() {
    protected var layoutInflater: LayoutInflater? = null
    protected var clickListener: AdapterClickListener<ITEM>? = null
    protected var longClickListener: AdapterLongClickListener<ITEM>? = null
    var items: List<ITEM>? = null
        set(items) {
            field = items
            count = NOT_COUNTED
            notifyDataSetChanged()
        }
    private var count = NOT_COUNTED

    init {
        setHasStableIds(useStableId())
    }

    protected fun useStableId(): Boolean {
        return true
    }


    protected fun inflater(context: Context): LayoutInflater {
        if (layoutInflater == null) layoutInflater = LayoutInflater.from(context)
        return layoutInflater
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HOLDER {
        return getHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: HOLDER, position: Int) {
        bindItem(holder, getItem(position), position)
    }

    override fun getItemId(position: Int): Long {
        return if (getItem(position) != null) getItemIdSafe(position) else -1
    }

    val mItemCount: Int
        get() {
            if (count >= 0) return count
            if (this.items != null) {
                count = this.items!!.size + headersCount + footersCount
                return count
            } else {
                count = headersCount + footersCount
                return count
            }
        }

    protected abstract fun getItemIdSafe(position: Int): Long

    val footersCount: Int
        get() = 0

    val headersCount: Int
        get() = 0

    fun isLastItem(position: Int): Boolean {
        return position == mItemCount - 1
    }

    fun getPosition(id: Long): Int {
        if (mItemCount > 0) {
            for (i in 0..mItemCount - 1) {
                if (getItemId(i) == id) return i
            }
        }
        return -1
    }

    fun getPosition(item: ITEM?, comparator: Comparator<ITEM>): Int {
        if (item == null) return -1
        if (mItemCount > 0) {
            for (i in 0..mItemCount - 1) {
                if (comparator.compare(getItem(i), item) == 0) return i
            }
        }
        return -1
    }

    fun bindItem(holder: HOLDER, item: ITEM?, position: Int) {
        holder.bindItem(item, position)
        holder.clickListener :HolderClickListener = listener : HolderClickListener(){ v, pos, isLongClick ->
            if (!isLongClick) {
                // View v at position pos is clicked.
                Logs.ui("Item at pos$pos clicked!", Logs.INFO)
                if (clickListener != null) clickListener!!.onClicked(v, getItem(pos.toInt()))
            } else {
                // View v at position pos is long-clicked.
                Logs.ui("Item at pos$pos long clicked!", Logs.INFO)
                if (longClickListener != null) longClickListener!!.onLongClicked(v, getItem(pos.toInt()))
            }
        }
    }

    fun getItem(position: Int): ITEM? {
        if (position < headersCount) return null
        val adjustedPosition = position - headersCount
        if (adjustedPosition < mItemCount - headersCount - footersCount)
            return this.items!![adjustedPosition]
        else
            return null
    }

    protected abstract fun getHolder(parent: ViewGroup, viewType: Int): HOLDER


    protected fun inflate(parent: ViewGroup, @LayoutRes layoutId: Int): View {
        return inflater(parent.context).inflate(layoutId, parent, false)
    }


    companion object {

        private val NOT_COUNTED = -1
    }
}