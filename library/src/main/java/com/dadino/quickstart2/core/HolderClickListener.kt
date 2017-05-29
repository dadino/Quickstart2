package com.dadino.quickstart2.core

import android.view.View

interface HolderClickListener {
    fun onClick(v: View, position: Int, isLongClick: Boolean)
}