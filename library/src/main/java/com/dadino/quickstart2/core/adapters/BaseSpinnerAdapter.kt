package com.dadino.quickstart2.core.adapters

import android.content.Context
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SpinnerAdapter
import com.dadino.quickstart2.core.adapters.holders.BaseHolder

abstract class BaseSpinnerAdapter<ITEM, out HOLDER : BaseHolder<ITEM>> : android.widget.BaseAdapter(), SpinnerAdapter {
	private var items: List<ITEM> = ArrayList()
	private var count: Int = 0
	private var inflater: LayoutInflater? = null

	protected fun inflater(context: Context): LayoutInflater {
		if (inflater == null) inflater = LayoutInflater.from(context)
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

		count = items.size + additionalItemCount
		return count

	}

	override fun getItem(position: Int): ITEM {
		return items[position]
	}

	abstract override fun getItemId(position: Int): Long

	override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
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
		if (getCount() == 0) return BaseSpinnerAdapter.Companion.ID_NOT_FOUND
		return (0..getCount() - 1).firstOrNull { id == getItemId(it) }
				?: BaseSpinnerAdapter.Companion.ID_NOT_FOUND
	}

	override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View? {
		return modifyDropDownView(getView(position, convertView, parent))
	}

	protected abstract fun modifyDropDownView(view: View?): View?

	protected abstract fun createHolder(convertView: View): HOLDER
	protected abstract fun inflateView(inflater: LayoutInflater, parent: ViewGroup): View

	protected fun inflate(parent: ViewGroup, @LayoutRes layoutId: Int): View {
		return inflater(parent.context).inflate(layoutId, parent, false)
	}

	companion object {

		val ID_NOT_FOUND = -1
	}
}
