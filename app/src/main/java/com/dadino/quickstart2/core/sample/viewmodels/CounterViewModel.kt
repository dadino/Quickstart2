package com.dadino.quickstart2.core.sample.viewmodels

import com.dadino.quickstart2.core.components.BaseViewModel
import com.dadino.quickstart2.core.entities.StateCommand
import com.dadino.quickstart2.core.entities.UserAction
import com.dadino.quickstart2.core.sample.entities.AddToCounter
import com.dadino.quickstart2.core.sample.entities.OnAdvanceCounterClicked


class CounterViewModel : BaseViewModel<CounterState>() {
	override fun reactToUserAction(action: UserAction) {
		when (action) {
			is OnAdvanceCounterClicked -> pushCommand(AddToCounter())
		}
	}

	override fun initialModel(): CounterState {
		return CounterState()
	}

	override fun reduce(previous: CounterState, command: StateCommand): CounterState {
		return CounterState(
				counter = when (command) {
					is AddToCounter -> previous.counter + 1
					else            -> previous.counter
				}
		)
	}
}

data class CounterState(
		val counter: Int = 0)