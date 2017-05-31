package com.dadino.quickstart2.core.listeners

interface AdapterClickListener<ITEM> {
	fun onClicked(v: android.view.View, item: ITEM)
}