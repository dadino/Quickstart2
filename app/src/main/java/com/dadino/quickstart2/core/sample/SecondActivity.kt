package com.dadino.quickstart2.core.sample

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import com.dadino.quickstart2.core.BaseActivity
import com.dadino.quickstart2.core.entities.UserAction
import com.dadino.quickstart2.core.sample.entities.OnSaveSessionRequested
import com.dadino.quickstart2.core.sample.viewmodels.SpinnerState
import com.dadino.quickstart2.core.sample.viewmodels.SpinnerViewModel
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_second.*
import org.koin.android.viewmodel.ext.android.viewModel

class SecondActivity : BaseActivity() {

	private val spinnerViewModel: SpinnerViewModel by viewModel()

	override fun initViews() {
		setContentView(R.layout.activity_second)
		setSupportActionBar(toolbar)

		supportActionBar?.setDisplayHomeAsUpEnabled(true)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		attachViewModel(spinnerViewModel, Lifecycle.State.RESUMED) {
			render(it)
		}
	}

	override fun collectUserActions(): Observable<UserAction> {
		return fab.clicks().map {
			OnSaveSessionRequested("Second")
		}
	}

	private fun render(state: SpinnerState) {
		Log.d("Second", "State: $state")

		Toast.makeText(this, "Session: ${spinnerViewModel.state().session}", Toast.LENGTH_LONG).show()
	}
}
