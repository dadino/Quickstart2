package com.dadino.quickstart2.core.sample

import android.arch.lifecycle.Lifecycle
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.dadino.quickstart2.core.BaseActivity
import com.dadino.quickstart2.core.entities.DoNotReactToThisAction
import com.dadino.quickstart2.core.entities.Signal
import com.dadino.quickstart2.core.entities.UserAction
import com.dadino.quickstart2.core.sample.entities.*
import com.dadino.quickstart2.core.sample.viewmodels.CounterState
import com.dadino.quickstart2.core.sample.viewmodels.CounterViewModel
import com.dadino.quickstart2.core.sample.viewmodels.SpinnerState
import com.dadino.quickstart2.core.sample.viewmodels.SpinnerViewModel
import com.dadino.quickstart2.core.sample.widgets.ExampleSpinner
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Observable
import org.koin.android.architecture.ext.viewModel

class SpinnerActivity : BaseActivity() {
	private val spinner: ExampleSpinner by lazy { findViewById<ExampleSpinner>(R.id.example_data_spinner) }
	private val idle: Button by lazy { findViewById<Button>(R.id.example_data_idle) }
	private val loading: Button by lazy { findViewById<Button>(R.id.example_data_loading) }
	private val error: Button by lazy { findViewById<Button>(R.id.example_data_error) }
	private val done: Button by lazy { findViewById<Button>(R.id.example_data_done) }
	private val secondPage: Button by lazy { findViewById<Button>(R.id.example_data_go_to_second_page) }
	private val saveSession: Button by lazy { findViewById<Button>(R.id.example_data_save_session) }
	private val counterButton: Button by lazy { findViewById<Button>(R.id.example_data_counter) }

	private val spinnerViewModel: SpinnerViewModel by viewModel()
	private val counterViewModel: CounterViewModel by viewModel()


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		attachViewModel(spinnerViewModel) { render(it) }
		attachViewModel(counterViewModel, Lifecycle.State.RESUMED) { render(it) }
	}

	override fun initViews() {
		setContentView(R.layout.activity_spinner)
		spinner.setOnRetryClickListener(View.OnClickListener { userActionsConsumer().accept(OnSpinnerRetryClicked()) })
	}

	private fun render(state: SpinnerState) {
		Log.d("Spinner", "State: $state")
		spinner.setState(state.list, state.loading, state.error)
		spinner.selectedId = state.selectedId ?: -1

		Signal.doAndConsume(state.signal) {
			Toast.makeText(this, "Signal received", Toast.LENGTH_LONG).show()
		}
	}

	private fun render(state: CounterState) {
		Log.d("Counter", "State: $state")
		counterButton.text = state.counter.toString()
	}

	override fun collectUserActions(): Observable<UserAction> {
		return Observable.merge(listOf(
				idle.clicks().map { OnSpinnerIdleClicked() },
				loading.clicks().map { OnSpinnerLoadingClicked() },
				error.clicks().map { OnSpinnerErrorClicked() },
				done.clicks().map { OnSpinnerDoneClicked() },
				secondPage.clicks().map { OnGoToSecondPageClicked() },
				saveSession.clicks().map { OnSaveSessionRequested("First") },
				counterButton.clicks().map { OnAdvanceCounterClicked() },
				spinner.userActions()
		)
		)
	}

	override fun interceptUserAction(action: UserAction): UserAction {
		return when (action) {
			is OnGoToSecondPageClicked -> {
				startActivity(Intent(this, SecondActivity::class.java))
				DoNotReactToThisAction()
			}
			else                       -> super.interceptUserAction(action)
		}
	}
}
