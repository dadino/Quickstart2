package com.dadino.quickstart2.core.adapters.holders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.dadino.quickstart2.core.entities.UserActionable


abstract class BaseHolder<in T> constructor(itemView: View) :
		RecyclerView.ViewHolder(itemView),
		UserActionable {

	abstract fun bindItem(item: T, position: Int)
}
