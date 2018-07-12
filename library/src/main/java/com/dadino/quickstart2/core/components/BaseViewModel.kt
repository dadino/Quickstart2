package com.dadino.quickstart2.core.components

import android.arch.lifecycle.ViewModel
import android.util.Log
import com.dadino.quickstart2.core.entities.StateCommand
import com.dadino.quickstart2.core.entities.UserAction
import com.dadino.quickstart2.core.utils.toAsync
import com.jakewharton.rx.replayingShare
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.functions.Consumer
import io.reactivex.rxkotlin.subscribeBy

abstract class BaseViewModel<STATE>
	: ViewModel() {

	private var model: STATE? = null

	private val userActionRelay: PublishRelay<UserAction> by lazy { PublishRelay.create<UserAction>() }
	private val userActionFlowable: Flowable<UserAction>  by lazy {
		userActionRelay.doOnNext { Log.d(modelName(), "<---- ${it.javaClass.simpleName}") }
				.replayingShare()
				.toFlowable(BackpressureStrategy.BUFFER)
				.retry()
				.doOnError { onError(it) }
				.toAsync()
	}

	private val stateCommandRelay: PublishRelay<StateCommand> by lazy { PublishRelay.create<StateCommand>() }
	private val stateObservable: Flowable<STATE>  by lazy {
		stateCommandRelay.doOnNext { Log.d(modelName(), "----> ${it.javaClass.simpleName}") }
				.map {
					model = reduce(state(), it)
					model!!
				}
				.replayingShare()
				.toFlowable(BackpressureStrategy.LATEST)
				.retry()
				.startWith(state())
				.doOnError { onError(it) }
				.toAsync()
	}

	init {
		userActionFlowable.subscribeBy(onNext = {
			reactToUserAction(it)
		})
	}


	fun states(): Flowable<STATE> {
		return stateObservable
	}

	fun receiveUserAction(action: UserAction) {
		userActionsConsumer().accept(action)
	}

	fun userActionsConsumer(): Consumer<UserAction> = userActionRelay

	fun commandConsumer(): Consumer<StateCommand> = stateCommandRelay

	protected fun pushCommand(command: StateCommand) {
		commandConsumer().accept(command)
	}

	fun state(): STATE {
		if (model == null) model = initialModel()
		return model!!
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
