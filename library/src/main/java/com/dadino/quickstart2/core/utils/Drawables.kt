package com.dadino.quickstart2.core.utils

import android.content.Context
import android.graphics.drawable.Drawable


object Drawables {
	fun getDrawableFromAttrRes(attrRes: Int, context: Context): Drawable {
		val a = context.obtainStyledAttributes(intArrayOf(attrRes))
		try {
			return a.getDrawable(0)
		} finally {
			a.recycle()
		}
	}

	fun getSelectableBackground(context: Context): Drawable {
		return getDrawableFromAttrRes(R.attr.selectableItemBackground, context)
	}
}