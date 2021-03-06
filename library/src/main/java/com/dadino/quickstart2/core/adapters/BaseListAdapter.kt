package com.dadino.quickstart2.core.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.dadino.quickstart2.core.adapters.holders.BaseHolder

abstract class BaseListAdapter<ITEM, HOLDER : BaseHolder<ITEM>> : BaseAdapter<ITEM, HOLDER>() {

	protected var layoutInflater: LayoutInflater? = null

	var items: List<ITEM>? = null
		set(items) {
			field = items
			count = NOT_COUNTED
			notifyDataSetChanged()
		}

	private var count = NOT_COUNTED

	init {
		this.setHasStableIds(this.useStableId())
	}

	open fun useStableId(): Boolean {
		return true
	}

	protected fun inflater(context: android.content.Context): LayoutInflater {
		if (layoutInflater == null) layoutInflater = LayoutInflater.from(context)
		return layoutInflater!!
	}


	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HOLDER {
		val holder = getHolder(parent, viewType)
		attachListenerToHolder(holder)
		return holder
	}

	override fun onBindViewHolder(holder: HOLDER, position: Int) {
		getItem(position)?.let { bindItem(holder, it, position) }
	}

	override fun getItemId(position: Int): Long {
		return if (getItem(position) != null) getItemIdSafe(position) else -1
	}

	private val mItemCount: Int
		get() {
			if (count >= 0) return count
			return if (this.items != null) {
				count = this.items!!.size + headersCount + footersCount
				count
			} else {
				count = headersCount + footersCount
				count
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
	}

	fun getItem(position: Int): ITEM? {
		if (position < headersCount) return null
		val adjustedPosition = position - headersCount
		return if (adjustedPosition < mItemCount - headersCount - footersCount)
			this.items!![adjustedPosition]
		else
			null
	}

	protected abstract fun getHolder(parent: ViewGroup, viewType: Int): HOLDER

	protected fun inflate(parent: ViewGroup, @androidx.annotation.LayoutRes layoutId: Int): android.view.View {
		return inflater(parent.context).inflate(layoutId, parent, false)
	}

	companion object {

		private const val NOT_COUNTED = -1
	}
}