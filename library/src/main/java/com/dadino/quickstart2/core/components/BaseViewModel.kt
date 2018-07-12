package com.dadino.quickstart2.core.components

import android.arch.lifecycle.ViewModel
import android.util.Log
import com.dadino.quickstart2.core.entities.InitState
import com.dadino.quickstart2.core.entities.StateCommand
import com.dadino.quickstart2.core.entities.UserAction
import com.dadino.quickstart2.core.utils.toAsync
import com.jakewharton.rx.replayingShare
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.functions.Consumer
import io.reactivex.rxkotlin.subscribeBy

abstract class BaseViewModel<STATE> : ViewModel() {

	private var state: STATE? = null

	private val userActionRelay: PublishRelay<UserAction> by lazy { PublishRelay.create<UserAction>() }
	private val userActionFlowable: Flowable<UserAction>  by lazy {
		userActionRelay.doOnNext { Log.d(modelName(), "<---- ${it.javaClass.simpleName}") }
				.retry()
				.doOnError { onError(it) }
				.toFlowable(BackpressureStrategy.BUFFER)
				.toAsync()
				.replayingShare()
	}

	private val stateCommandRelay: PublishRelay<StateCommand> by lazy {
		PublishRelay.create<StateCommand>()
	}

	val states: Flowable<STATE>  by lazy {
		stateCommandRelay
				.startWith(InitState())
				.doOnNext { Log.d(modelName(), "----> ${it.javaClass.simpleName}") }
				.map {
					state = reduce(state(), it)
					state!!
				}
				.retry()
				.doOnError { onError(it) }
				.toFlowable(BackpressureStrategy.LATEST)
				.toAsync()
				.replayingShare()
	}

	init {
		userActionFlowable.subscribeBy(onNext = {
			reactToUserAction(it)
		})
	}

	@Deprecated(message = "Do not use this method anymore, use the attribute states", replaceWith = ReplaceWith("states"), level = DeprecationLevel.ERROR)
	fun states(): Flowable<STATE> {
		throw RuntimeException("Do not use this method anymore, use the attribute states")
	}

	fun receiveUserAction(action: UserAction) {
		userActionsConsumer().accept(action)
	}

	fun userActionsConsumer(): Consumer<UserAction> = userActionRelay

	protected fun commandConsumer(): Consumer<StateCommand> = stateCommandRelay

	protected fun pushCommand(command: StateCommand) {
		commandConsumer().accept(command)
	}

	fun state(): STATE {
		if (state == null) state = initialModel()
		return state!!
	}

	abstract protected fun reactToUserAction(action: UserAction)

	abstract fun initialModel(): STATE

	abstract fun reduce(previous: STATE, command: StateCommand): STATE

	protected fun onError(error: Throwable) {
		error.printStackTrace()
	}

	private fun modelName(): String {
		return javaClass.simpleName
	}

}
