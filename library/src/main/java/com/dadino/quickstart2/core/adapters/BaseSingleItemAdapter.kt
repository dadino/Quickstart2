package com.dadino.quickstart2.core.adapters

import com.dadino.quickstart2.core.adapters.holders.BaseHolder

abstract class BaseSingleItemAdapter<ITEM, HOLDER : BaseHolder<ITEM>> : com.dadino.quickstart2.core.BaseAdapter<ITEM, HOLDER>() {

	protected var layoutInflater: android.view.LayoutInflater? = null
	protected var item: ITEM? = null
		set(item) {
			field = item
			notifyDataSetChanged()
		}

	init {
		setHasStableIds(useStableId())
	}

	protected fun useStableId(): Boolean {
		return false
	}

	protected fun inflater(context: android.content.Context): android.view.LayoutInflater {
		if (layoutInflater == null) layoutInflater = android.view.LayoutInflater.from(context)
		return layoutInflater!!
	}

	override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): HOLDER {
		return getHolder(parent, viewType)
	}

	override fun onBindViewHolder(holder: HOLDER, position: Int) {
		bindItem(holder, item, position)
	}

	override abstract fun getItemId(position: Int): Long

	fun isLastItem(position: Int): Boolean {
		return position == itemCount - 1
	}

	fun bindItem(holder: HOLDER, item: ITEM?, position: Int) {
		holder.bindItem(item, position)
	}

	protected fun inflate(parent: android.view.ViewGroup, @android.support.annotation.LayoutRes layoutId: Int): android.view.View {
		return inflater(parent.context).inflate(layoutId, parent, false)
	}

	protected abstract fun getHolder(parent: android.view.ViewGroup, viewType: Int): HOLDER
}
