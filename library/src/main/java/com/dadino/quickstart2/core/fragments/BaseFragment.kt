package com.dadino.quickstart2.core.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dadino.quickstart2.core.components.Actionable
import com.dadino.quickstart2.core.components.BaseViewModel
import com.dadino.quickstart2.core.components.UserActionsHandler
import com.dadino.quickstart2.core.entities.UserAction
import com.dadino.quickstart2.core.interfaces.DisposableLifecycleHolder
import com.dadino.quickstart2.core.utils.DisposableLifecycle
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy

abstract class BaseFragment : android.support.v4.app.Fragment(), Actionable, DisposableLifecycleHolder {

	override lateinit var userActionsHandler: UserActionsHandler

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return internalInitViews(inflater, container, savedInstanceState)
	}

	private fun internalInitViews(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		val view = initViews(inflater, container, savedInstanceState)
		userActionsHandler = object : UserActionsHandler() {
			override fun collectUserActions(): Observable<UserAction> {
				return this@BaseFragment.collectUserActions()
			}

			override fun interceptUserAction(action: UserAction): UserAction {
				return this@BaseFragment.interceptUserAction(action)
			}
		}
		return view
	}

	abstract fun initViews(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View

	protected fun <S : Any, T : BaseViewModel<S>> attachViewModel(viewModel: T, render: (S) -> Unit): T {
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