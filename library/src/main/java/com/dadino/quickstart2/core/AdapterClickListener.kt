package com.dadino.quickstart2.core

import android.view.View

interface AdapterClickListener<ITEM> {
    fun onClicked(v: View, item: ITEM)
}