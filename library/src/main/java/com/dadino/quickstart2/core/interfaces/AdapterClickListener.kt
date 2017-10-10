package com.dadino.quickstart2.core.interfaces

interface AdapterClickListener<ITEM> {
	fun onClicked(v: android.view.View, item: ITEM)
}