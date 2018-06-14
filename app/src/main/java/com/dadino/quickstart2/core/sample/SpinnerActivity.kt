package com.dadino.quickstart2.core.sample

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.dadino.quickstart2.core.BaseActivity
import com.dadino.quickstart2.core.entities.DoNotReactToThisAction
import com.dadino.quickstart2.core.entities.UserAction
import com.dadino.quickstart2.core.sample.entities.*
import com.dadino.quickstart2.core.sample.viewmodels.SpinnerState
import com.dadino.quickstart2.core.sample.viewmodels.SpinnerViewModel
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_spinner.*

class SpinnerActivity : BaseActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		attachViewModel(SpinnerViewModel::class.java) { render(it) }
	}

	override fun initViews() {
		setContentView(R.layout.activity_spinner)
		example_data_spinner.setOnRetryClickListener(View.OnClickListener { userActionsConsumer().accept(OnSpinnerRetryClicked()) })

	}

	private fun render(state: SpinnerState) {
		Log.d("Spinner", "State: $state")
		example_data_spinner.setState(state.list, state.loading, state.error)
		example_data_spinner.selectedId = state.selectedId ?: -1
	}

	override fun collectUserActions(): Observable<UserAction> {
		return Observable.merge(listOf(
				example_data_idle.clicks().map { OnSpinnerIdleClicked() },
				example_data_loading.clicks().map { OnSpinnerLoadingClicked() },
				example_data_error.clicks().map { OnSpinnerErrorClicked() },
				example_data_done.clicks().map { OnSpinnerDoneClicked() },
				example_data_go_to_second_page.clicks().map { OnGoToSecondPageClicked() },
				example_data_save_session.clicks().map { OnSaveSessionRequested("First") },
				example_data_spinner.userActions()
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
