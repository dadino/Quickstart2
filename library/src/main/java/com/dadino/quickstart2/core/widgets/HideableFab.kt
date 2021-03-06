package com.dadino.quickstart2.core.widgets

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.floatingactionbutton.FloatingActionButton

open class HideableFab : FloatingActionButton {

	var canShow = true
		set(value) {
			field = value
			if (canShow.not()) {
				hide()
			} else {
				show()
			}
		}

	constructor(context: Context) : super(context)

	constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

	constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

	override fun show() {
		if (canShow) super.show()
	}
}
