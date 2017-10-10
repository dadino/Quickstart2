package com.dadino.quickstart2.core.widgets

import android.content.Context
import android.support.design.widget.FloatingActionButton
import android.util.AttributeSet

class HideableFab : FloatingActionButton {

	private var mCanShow = true

	constructor(context: Context) : super(context)

	constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

	constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

	fun setCanShow(canShow: Boolean) {
		this.mCanShow = canShow
		if (!mCanShow)
			hide()
		else
			show()
	}

	override fun show() {
		if (mCanShow) super.show()
	}
}
