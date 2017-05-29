package com.dadino.quickstart2.core

import android.support.v7.widget.RecyclerView
import android.view.View
import butterknife.ButterKnife

abstract class BaseHolder<T> @JvmOverloads constructor(itemView: View, withClickListener: Boolean = true) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {

    var clickListener: HolderClickListener? = null

    init {
        if (withClickListener) {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }
        bindView(itemView)
    }

    fun bindView(view: View) {
        ButterKnife.bind(this, view)
    }

    abstract fun bindItem(item: T?, position: Int)

    override fun onClick(v: View) {
        clickListener?.onClick(clickedView(v), layoutPosition, false)
    }

    override fun onLongClick(v: View): Boolean {
        clickListener?.onClick(longClickedView(v), layoutPosition, true)
        return true
    }

    fun clickedView(parent: View): View {
        return parent
    }

    fun longClickedView(parent: View): View {
        return parent
    }

}
