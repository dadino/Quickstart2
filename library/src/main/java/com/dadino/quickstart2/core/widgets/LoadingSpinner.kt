package com.dadino.quickstart2.core.widgets

import android.content.Context
import android.database.DataSetObserver
import android.support.annotation.StringRes
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.widget.*
import com.dadino.quickstart2.core.R
import com.dadino.quickstart2.core.adapters.BaseSpinnerAdapter

abstract class LoadingSpinner<T : BaseSpinnerAdapter<*, *>> : FrameLayout {

	protected var mAdapter: T? = null
	private val progress: ProgressBar by lazy { findViewById<ProgressBar>(R.id.loading_spinner_progress) }
	private val spinner: Spinner by lazy { findViewById<Spinner>(R.id.loading_spinner_spinner) }
	private val label: TextView by lazy { findViewById<TextView>(R.id.loading_spinner_label) }
	private var mLoading: Boolean = false
	private var mLabel: String? = null

	constructor(context: Context) : super(context) {
		init()
	}

	constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
		setLabelFromAttributeSet(context, attrs)
		init()
	}

	constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
		setLabelFromAttributeSet(context, attrs)
		init()
	}

	private fun init() {
		View.inflate(context, R.layout.view_loading_spinner, this)

		setLabel(mLabel)
		setOnClickListener { spinner.performClick() }
		initialize()
	}

	private fun setLabelFromAttributeSet(context: Context, attrs: AttributeSet?) {
		if (attrs != null) {
			val a = context.obtainStyledAttributes(attrs, R.styleable.LoadingSpinner)
			mLabel = a.getString(R.styleable.LoadingSpinner_ls_label)
			a.recycle()
		}
	}

	protected fun initialize() {}

	fun setListLoading(loading: Boolean) {
		this.mLoading = loading

		progress.visibility = if (loading) View.VISIBLE else View.INVISIBLE
		spinner.visibility = if (loading) View.INVISIBLE else View.VISIBLE
	}

	fun setLabel(@StringRes stringId: Int) {
		label.text = context.getString(stringId)
	}

	fun setLabel(string: String?) {
		label.text = string
		if (TextUtils.isEmpty(string))
			label.visibility = View.GONE
		else
			label.visibility = View.VISIBLE
	}

	fun setOnItemSelectedListener(listener: AdapterView.OnItemSelectedListener) {
		spinner.onItemSelectedListener = listener
	}

	fun updateLoadingState() {
		setListLoading(mLoading)
	}

	fun setAdapter(adapter: T) {
		this.mAdapter = adapter
		mAdapter!!.registerDataSetObserver(object : DataSetObserver() {
			override fun onChanged() {
				super.onChanged()
				updateLoadingState()
			}
		})
		spinner.adapter = mAdapter
		updateLoadingState()
	}

	fun getAdapter(): T? {
		return mAdapter
	}

	var selection: Int
		get() = spinner.selectedItemPosition
		set(position) {
			if (position == -1) return
			if (position == spinner.selectedItemPosition) return
			spinner.setSelection(position)
		}

	var selectedId: Long
		get() = mAdapter?.getItemId(spinner.selectedItemPosition) ?: 0
		set(id) {
			val wantedPosition = mAdapter?.getPosition(id) ?: 0
			if (wantedPosition < 0 || wantedPosition == spinner.selectedItemPosition) return
			spinner.setSelection(wantedPosition)
		}

	override fun setEnabled(enabled: Boolean) {
		super.setEnabled(enabled)
		spinner.isEnabled = enabled
		label.isEnabled = enabled
	}
}
