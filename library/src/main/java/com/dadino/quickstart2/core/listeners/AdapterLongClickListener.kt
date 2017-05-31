package com.dadino.quickstart2.core.listeners

import android.view.View

interface AdapterLongClickListener<ITEM> {
	fun onLongClicked(v: View, item: ITEM)
}