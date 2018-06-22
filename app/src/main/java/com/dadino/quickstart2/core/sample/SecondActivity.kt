package com.dadino.quickstart2.core.sample

import android.os.Bundle
import android.util.Log
import com.dadino.quickstart2.core.BaseActivity
import com.dadino.quickstart2.core.sample.entities.OnSaveSessionRequested
import com.dadino.quickstart2.core.sample.viewmodels.SpinnerState
import com.dadino.quickstart2.core.sample.viewmodels.SpinnerViewModel
import kotlinx.android.synthetic.main.activity_second.*
import org.koin.android.architecture.ext.viewModel

class SecondActivity : BaseActivity() {

	val spinnerViewModel: SpinnerViewModel by viewModel()

	override fun initViews() {
		setContentView(R.layout.activity_second)
		setSupportActionBar(toolbar)

		fab.setOnClickListener { view ->
			receiveUserAction(OnSaveSessionRequested("Second"))
		}
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		attachViewModel(spinnerViewModel) {
			render(it)
		}
	}

	private fun render(state: SpinnerState) {
		Log.d("Second", "State: $state")
	}

}
