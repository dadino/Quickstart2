package com.dadino.quickstart2.core.sample.viewmodels

import com.dadino.quickstart2.core.components.BaseViewModel
import com.dadino.quickstart2.core.entities.ModelCommand
import com.dadino.quickstart2.core.entities.UserAction
import com.dadino.quickstart2.core.sample.entities.*
import com.dadino.quickstart2.core.sample.repositories.ISessionRepository
import com.dadino.quickstart2.core.utils.toAsync


class SpinnerViewModel constructor(private val sessionRepo: ISessionRepository) : BaseViewModel<SpinnerState>() {
	init {
		sessionRepo.getCurrentSession()
				.map<ModelCommand> {
					SetLoadSessionCompleted(it)
				}
				.startWith(SetLoadSessionInProgress())
				.onErrorReturn { SetLoadSessionError(it) }
				.toAsync()
				.subscribe(commandConsumer())
	}

	private fun saveSession(id: String) {
		sessionRepo.saveCurrentSession(Session(id))
				.subscribe()
	}

	override fun reactToUserAction(action: UserAction) {
		when (action) {
			is OnSpinnerRetryClicked   -> pushCommand(SetInProgress())
			is OnSpinnerIdleClicked    -> pushCommand(SetIdle())
			is OnSpinnerLoadingClicked -> pushCommand(SetInProgress())
			is OnSpinnerErrorClicked   -> pushCommand(SetError())
			is OnSpinnerDoneClicked    -> pushCommand(SetDone(listOf(ExampleData(1, "Mario Rossi"), ExampleData(2, "Franco Verdi"))))
			is OnExampleDataSelected   -> pushCommand(SetItemSelected(action.item?.id))
			is OnSaveSessionRequested  -> saveSession(action.id)
		}
	}

	override fun initialModel(): SpinnerState {
		return SpinnerState()
	}

	override fun reduce(previous: SpinnerState, command: ModelCommand): SpinnerState {
		return SpinnerState(
				list = when (command) {
					is SetDone       -> command.list
					is SetInProgress -> listOf()
					is SetError      -> listOf()
					else             -> previous.list
				},
				loading = when (command) {
					is SetDone       -> false
					is SetInProgress -> true
					is SetError      -> false
					else             -> previous.loading
				},
				error = when (command) {
					is SetDone       -> false
					is SetInProgress -> false
					is SetError      -> true
					else             -> previous.error
				},
				selectedId = when (command) {
					is SetItemSelected -> command.selectedId
					else               -> previous.selectedId
				},
				session = when (command) {
					is SetLoadSessionCompleted -> command.session
					else                       -> previous.session
				}
		)
	}
}

data class SpinnerState(
		val selectedId: Long? = 0,
		val session: Session? = null,
		val loading: Boolean = false,
		val error: Boolean = false,
		val list: List<ExampleData> = listOf())