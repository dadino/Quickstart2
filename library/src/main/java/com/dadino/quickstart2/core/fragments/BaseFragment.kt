package com.dadino.quickstart2.core.fragments

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dadino.quickstart2.core.components.Actionable
import com.dadino.quickstart2.core.components.BaseViewModel
import com.dadino.quickstart2.core.components.UserActionsHandler
import com.dadino.quickstart2.core.di.Injectable
import com.dadino.quickstart2.core.entities.UserAction
import com.dadino.quickstart2.core.utils.ViewModels
import com.kizitonwose.android.disposebag.DisposeBag
import io.reactivex.Observable
import javax.inject.Inject

abstract class BaseFragment : android.support.v4.app.Fragment(), Actionable, Injectable {

	@Inject
	lateinit var viewModelFactory: ViewModelProvider.Factory
	@Inject
	lateinit var bag: DisposeBag

	override lateinit var userActionsHandler: UserActionsHandler

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return internalInitViews()
	}

	private fun internalInitViews(): View {
		val view = initViews()
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

	abstract fun initViews(): View

	protected fun <S : Any, T : BaseViewModel<S>> getViewModel(viewModelClass: Class<T>, render: (S) -> Unit): T {
		return ViewModels.get(this, viewModelClass, render)
	}
}