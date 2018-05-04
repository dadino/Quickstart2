package com.dadino.quickstart2.core.components

import android.arch.lifecycle.ViewModel
import android.util.Log
import com.dadino.quickstart2.core.entities.ModelCommand
import com.dadino.quickstart2.core.entities.UserAction
import com.dadino.quickstart2.core.utils.toAsync
import com.jakewharton.rx.replayingShare
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.functions.Consumer
import io.reactivex.rxkotlin.subscribeBy
import java.util.concurrent.TimeUnit

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

	private val modelCommandRelay: BehaviorRelay<ModelCommand> by lazy { BehaviorRelay.create<ModelCommand>() }
	private val stateObservable: Flowable<STATE>  by lazy {
		modelCommandRelay.doOnNext { Log.d(modelName(), "----> ${it.javaClass.simpleName}") }
				.map {
					model = reduce(state(), it)
					delayDoAfterReduce(model!!, it)
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

	private fun delayDoAfterReduce(state: STATE, ev: ModelCommand) {
		Single.just(ev)
				.delay(16, TimeUnit.MILLISECONDS)
				.toAsync()
				.subscribe({ command: ModelCommand ->
					doAfterReduce(state!!, command)
				})
	}

	fun states(): Flowable<STATE> {
		return stateObservable
	}

	fun receiveUserAction(action: UserAction) {
		userActionsConsumer().accept(action)
	}

	fun userActionsConsumer(): Consumer<UserAction> = userActionRelay

	fun commandConsumer(): Consumer<ModelCommand> = modelCommandRelay

	protected fun pushCommand(command: ModelCommand) {
		commandConsumer().accept(command)
	}

	fun state(): STATE {
		if (model == null) model = initialModel()
		return model!!
	}

	abstract protected fun reactToUserAction(action: UserAction)

	abstract fun initialModel(): STATE

	abstract fun reduce(previous: STATE, command: ModelCommand): STATE

	protected open fun doAfterReduce(updatedModel: STATE, command: ModelCommand) {

	}

	protected fun onError(error: Throwable) {
		error.printStackTrace()
	}

	private fun modelName(): String {
		return javaClass.simpleName
	}

}
