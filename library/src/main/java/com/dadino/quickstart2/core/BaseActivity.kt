package com.dadino.quickstart2.core

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.dadino.quickstart2.core.components.Actionable
import com.dadino.quickstart2.core.components.BaseViewModel
import com.dadino.quickstart2.core.components.UserActionsHandler
import com.dadino.quickstart2.core.di.Injectable
import com.dadino.quickstart2.core.entities.UserAction
import com.dadino.quickstart2.core.utils.ViewModels
import com.kizitonwose.android.disposebag.DisposeBag
import io.reactivex.Observable
import javax.inject.Inject

abstract class BaseActivity : AppCompatActivity(), Actionable, Injectable {

	@Inject
	lateinit var viewModelFactory: ViewModelProvider.Factory
	@Inject
	lateinit var bag: DisposeBag

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

	protected fun <S : Any, T : BaseViewModel<S>> getViewModel(viewModelClass: Class<T>, render: (S) -> Unit): T {
		return ViewModels.get(this, viewModelClass, render)
	}
}