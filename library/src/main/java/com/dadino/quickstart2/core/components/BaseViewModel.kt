package com.dadino.quickstart2.core.components

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.LiveDataReactiveStreams
import android.util.Log
import com.dadino.quickstart2.core.utils.toAsync
import com.jakewharton.rx.replayingShare
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.functions.Consumer
import java.util.concurrent.TimeUnit

abstract class BaseViewModel<MODEL, EVENT : Any>(application: Application) : AndroidViewModel(application) {

	private var model: MODEL? = null

	private val relay: PublishRelay<EVENT> by lazy { PublishRelay.create<EVENT>() }
	private val viewModelFlowable: Flowable<MODEL>  by lazy {
		relay.doOnNext { Log.d(modelName(), "----> ${it.javaClass.simpleName} ----> ${it.toString()}") }
				.map {
					model = reduce(model(), it)
					delayDoAfterReduce(model!!, it)
					model!!
				}
				.replayingShare()
				.toFlowable(BackpressureStrategy.LATEST)
				.retry()
				.startWith(model())
				.doOnError { onError(it) }
				.toAsync()
	}

	private fun delayDoAfterReduce(model: MODEL, ev: EVENT) {
		Single.just(ev)
				.delay(16, TimeUnit.MILLISECONDS)
				.toAsync()
				.subscribe({ event: EVENT ->
							   doAfterReduce(model!!, event)
						   })
	}

	fun livedata(): LiveData<MODEL> {
		return LiveDataReactiveStreams.fromPublisher(viewModelFlowable)
	}

	protected fun eventConsumer(): Consumer<EVENT> {
		return relay
	}

	protected fun pushEvent(event: EVENT) {
		eventConsumer().accept(event)
	}

	fun model(): MODEL {
		if (model == null) model = initialModel()
		return model!!
	}

	abstract fun initialModel(): MODEL

	abstract fun reduce(previous: MODEL, event: EVENT): MODEL

	protected open fun doAfterReduce(updatedModel: MODEL, event: EVENT) {

	}

	protected fun onError(error: Throwable) {
		ErrorDispatcherController.dispatcher?.dispatchError(error)
	}

	private fun modelName(): String {
		return javaClass.simpleName
	}
}

abstract class ALTERNATIVEBaseViewModel<MODEL, EVENT : Any>(application: Application) : AndroidViewModel(application) {
	private val relay: PublishRelay<EVENT> by lazy { PublishRelay.create<EVENT>() }
	private val viewModelFlowable: Flowable<MODEL>  by lazy {
		relay
				.doOnNext { Log.d(modelName(), "----> ${it.javaClass.simpleName} ----> ${it.toString()}") }
				.scan(initialModel(), { mod, event ->
					val model = reduce(mod, event)
					delayDoAfterReduce(model, event)
					model!!
				})
				.replayingShare()
				.toFlowable(BackpressureStrategy.LATEST)
				.retry()
				.doOnError { onError(it) }
				.toAsync()
	}

	private fun delayDoAfterReduce(model: MODEL, ev: EVENT) {
		Single.just(ev)
				.delay(16, TimeUnit.MILLISECONDS)
				.toAsync()
				.subscribe({ event: EVENT ->
							   doAfterReduce(model!!, event)
						   })
	}

	fun livedata(): LiveData<MODEL> {
		return LiveDataReactiveStreams.fromPublisher(viewModelFlowable)
	}

	protected fun eventConsumer(): Consumer<EVENT> {
		return relay
	}

	protected fun pushEvent(event: EVENT) {
		eventConsumer().accept(event)
	}

	fun model(): MODEL {
		return viewModelFlowable.blockingLatest().first()
	}

	abstract fun initialModel(): MODEL

	abstract fun reduce(previous: MODEL, event: EVENT): MODEL

	protected open fun doAfterReduce(updatedModel: MODEL, event: EVENT) {

	}

	protected fun onError(error: Throwable) {
		ErrorDispatcherController.dispatcher?.dispatchError(error)
	}

	private fun modelName(): String {
		return javaClass.simpleName
	}
}