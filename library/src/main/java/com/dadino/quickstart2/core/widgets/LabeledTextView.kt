package com.dadino.quickstart2.core.widgets

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.StringRes
import com.dadino.quickstart2.core.R
import com.dadino.quickstart2.core.utils.goneIf

class LabeledTextView @kotlin.jvm.JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	defStyle: Int = 0
) : LinearLayout(context, attrs, defStyle) {

	private val label: TextView by lazy { findViewById<TextView>(R.id.labeled_textview_label) }

	init {
		orientation = VERTICAL
		View.inflate(context, R.layout.view_labeled_textview, this)
		if (attrs != null) {
			val a = context.obtainStyledAttributes(attrs, R.styleable.LabeledTextView)
			setLabel(a.getString(R.styleable.LabeledTextView_lt_label))
			setValue(a.getString(R.styleable.LabeledTextView_lt_value))
			a.recycle()
		}
	}

	fun setLabel(@StringRes labelRes: Int) {
		label.setText(labelRes)
	}

	fun setLabel(labelText: String?) {
		label.text = labelText
	}

	fun setValue(value: String?) {
		goneIf(TextUtils.isEmpty(value))
		label.text = value
	}
}