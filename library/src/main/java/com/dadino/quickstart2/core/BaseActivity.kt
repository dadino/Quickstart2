package com.dadino.quickstart2.core

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.dadino.quickstart2.core.components.Actionable
import com.dadino.quickstart2.core.components.BaseViewModel
import com.dadino.quickstart2.core.components.UserActionsHandler
import com.dadino.quickstart2.core.di.Injectable
import com.dadino.quickstart2.core.entities.UserAction
import com.dadino.quickstart2.core.interfaces.DisposableLifecycleHolder
import com.dadino.quickstart2.core.utils.DisposableLifecycle
import com.dadino.quickstart2.core.utils.ViewModels
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

abstract class BaseActivity : AppCompatActivity(), Actionable, Injectable, DisposableLifecycleHolder {

	@Inject
	lateinit var viewModelFactory: ViewModelProvider.Factory

	override lateinit var userActionsHandler: UserActionsHandler

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		internalInitViews()
	}

	private fun internalInitViews() {
		initViews()
		userActionsHandler = object : UserActionsHandler() {
			override fun collectUserActions(): Observable<UserAction> {
				return this@BaseActivity.collectUserActions()
			}

			override fun interceptUserAction(action: UserAction): UserAction {
				return this@BaseActivity.interceptUserAction(action)
			}
		}
	}

	abstract fun initViews()

	@Deprecated("Use attachViewModel", ReplaceWith("attachViewModel(viewModelClass, render)"))
	protected fun <S : Any, T : BaseViewModel<S>> getViewModel(viewModelClass: Class<T>, render: (S) -> Unit): T {
		return attachViewModel(viewModelClass, render)
	}

	protected fun <S : Any, T : BaseViewModel<S>> attachViewModel(viewModelClass: Class<T>, render: (S) -> Unit): T {
		val viewModel = ViewModels.getViewModel(
				activity = this,
				factory = viewModelFactory,
				viewModelClass = viewModelClass)

		attachToLifecycle(viewModel, render)

		return viewModel
	}

	private fun <S : Any, T : BaseViewModel<S>> attachToLifecycle(viewModel: T, render: (S) -> Unit) {
		attachDisposableToResumePause { viewModel.states().subscribeBy(onNext = { render(it) }) }
		attachDisposableToResumePause { userActions().subscribe(viewModel.userActionsConsumer()) }
	}

	override fun attachDisposableToCreateDestroy(createDisposable: () -> Disposable) {
		DisposableLifecycle.attachToCreateDestroy(this, createDisposable)
	}

	override fun attachDisposableToStartStop(createDisposable: () -> Disposable) {
		DisposableLifecycle.attachToStartStop(this, createDisposable)
	}

	override fun attachDisposableToResumePause(createDisposable: () -> Disposable) {
		DisposableLifecycle.attachToResumePause(this, createDisposable)
	}
}