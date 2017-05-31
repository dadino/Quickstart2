package com.dadino.quickstart2.core.adapters

import com.dadino.quickstart2.core.adapters.holders.BaseHolder

abstract class BaseSpinnerAdapter<ITEM, out HOLDER : BaseHolder<ITEM>> : android.widget.BaseAdapter(), android.widget.SpinnerAdapter {
	private var items: List<ITEM>? = null
	private var count: Int = 0
	private var inflater: android.view.LayoutInflater? = null

	protected fun inflater(context: android.content.Context): android.view.LayoutInflater {
		if (inflater == null) inflater = android.view.LayoutInflater.from(context)
		return inflater!!
	}

	fun setItems(items: List<ITEM>) {
		this.items = items
		count = -1
		notifyDataSetChanged()
	}

	val additionalItemCount: Int
		get() = 0

	override fun getCount(): Int {
		if (count >= 0) return count
		if (items != null) {
			count = items!!.size + additionalItemCount
			return count
		} else {
			count = additionalItemCount
			return count
		}
	}

	override fun getItem(position: Int): ITEM {
		return items!![position]
	}

	abstract override fun getItemId(position: Int): Long

	override fun getView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
		var convertview = convertView
		val viewHolder: HOLDER
		if (convertview == null) {
			convertview = inflateView(inflater(parent.context), parent)
			viewHolder = createHolder(convertview)
			convertview.tag = viewHolder
		} else {
			viewHolder = convertview.tag as HOLDER
		}
		viewHolder.bindItem(getItem(position), position)
		return convertview
	}

	fun getPosition(id: Long): Int {
		if (getCount() == 0) return com.dadino.quickstart2.core.adapters.BaseSpinnerAdapter.Companion.ID_NOT_FOUND
		return (0..getCount() - 1).firstOrNull { id == getItemId(it) }
				?: com.dadino.quickstart2.core.adapters.BaseSpinnerAdapter.Companion.ID_NOT_FOUND
	}

	override fun getDropDownView(position: Int, convertView: android.view.View, parent: android.view.ViewGroup): android.view.View {
		return modifyDropDownView(getView(position, convertView, parent))
	}

	protected abstract fun modifyDropDownView(view: android.view.View): android.view.View

	protected abstract fun createHolder(convertView: android.view.View): HOLDER
	protected abstract fun inflateView(context: android.view.LayoutInflater, parent: android.view.ViewGroup): android.view.View

	protected fun inflate(parent: android.view.ViewGroup, @android.support.annotation.LayoutRes layoutId: Int): android.view.View {
		return inflater(parent.context).inflate(layoutId, parent, false)
	}

	inner class ViewHolder {

		internal var spinner: android.widget.Spinner? = null
	}

	companion object {

		val ID_NOT_FOUND = -1
	}
}
