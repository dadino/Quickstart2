package com.dadino.quickstart2.core.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.dadino.quickstart2.core.adapters.holders.BaseHolder
import com.dadino.quickstart2.core.interfaces.AdapterClickListener
import com.dadino.quickstart2.core.interfaces.AdapterLongClickListener
import com.dadino.quickstart2.core.interfaces.HolderClickListener

abstract class BaseListAdapter<ITEM, HOLDER : BaseHolder<ITEM>> : BaseAdapter<ITEM, HOLDER>(), HolderClickListener {

	protected var layoutInflater: LayoutInflater? = null
	var clickListener: AdapterClickListener<ITEM>? = null
	var longClickListener: AdapterLongClickListener<ITEM>? = null
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

	protected fun inflater(context: android.content.Context): LayoutInflater {
		if (layoutInflater == null) layoutInflater = LayoutInflater.from(context)
		return layoutInflater!!
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HOLDER {
		return getHolder(parent, viewType)
	}

	override fun onBindViewHolder(holder: HOLDER, position: Int) {
		getItem(position)?.let { bindItem(holder, it, position) }
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

	override fun getItemCount(): Int {
		return mItemCount
	}

	protected abstract fun getItemIdSafe(position: Int): Long

	protected var footersCount: Int = 0

	protected var headersCount: Int = 0

	fun isLastItem(position: Int): Boolean {
		return position == mItemCount - 1
	}

	fun getPosition(id: Long): Int {
		if (mItemCount > 0) {
			(0 until mItemCount)
					.filter { getItemId(it) == id }
					.forEach { return it }
		}
		return -1
	}

	fun getPosition(item: ITEM?, comparator: java.util.Comparator<ITEM>): Int {
		if (item == null) return -1
		if (mItemCount > 0) {
			(0 until mItemCount)
					.filter { comparator.compare(getItem(it), item) == 0 }
					.forEach { return it }
		}
		return -1
	}

	open fun bindItem(holder: HOLDER, item: ITEM, position: Int) {
		holder.bindItem(item, position)
		holder.clickListener = (this)
	}

	fun getItem(position: Int): ITEM? {
		if (position < headersCount) return null
		val adjustedPosition = position - headersCount
		if (adjustedPosition < mItemCount - headersCount - footersCount)
			return this.items!![adjustedPosition]
		else
			return null
	}

	override fun onClick(v: android.view.View, position: Int, isLongClick: Boolean) {
		val item: ITEM = getItem(position) ?: return
		if (!isLongClick) {
			clickListener?.onClicked(v, item)
		} else {
			longClickListener?.onLongClicked(v, item)
		}
	}

	protected abstract fun getHolder(parent: ViewGroup, viewType: Int): HOLDER

	protected fun inflate(parent: ViewGroup, @android.support.annotation.LayoutRes layoutId: Int): android.view.View {
		return inflater(parent.context).inflate(layoutId, parent, false)
	}

	companion object {

		private val NOT_COUNTED = -1
	}
}