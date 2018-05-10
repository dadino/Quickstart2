package com.dadino.quickstart2.core.utils

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import com.dadino.quickstart2.core.BaseActivity
import com.dadino.quickstart2.core.components.BaseViewModel
import com.dadino.quickstart2.core.entities.UserActionable
import com.dadino.quickstart2.core.fragments.BaseFragment
import com.kizitonwose.android.disposebag.DisposeBag
import io.reactivex.rxkotlin.subscribeBy


object ViewModels {

	fun <S : Any, T : BaseViewModel<S>> get(fragment: BaseFragment, viewModelClass: Class<T>, render: (S) -> Unit): T {
		return get(factory = fragment.viewModelFactory, bag = fragment.bag, activity = fragment.requireActivity(), actionable = fragment, viewModelClass = viewModelClass, render = render)
	}

	fun <S : Any, T : BaseViewModel<S>> get(activity: BaseActivity, viewModelClass: Class<T>, render: (S) -> Unit): T {
		return get(factory = activity.viewModelFactory, bag = activity.bag, activity = activity, actionable = activity, viewModelClass = viewModelClass, render = render)
	}

	fun <S : Any, T : BaseViewModel<S>> get(fragment: Fragment, actionable: UserActionable, factory: ViewModelProvider.Factory, bag: DisposeBag, viewModelClass: Class<T>, render: (S) -> Unit): T {
		val viewModel = ViewModelProviders.of(fragment, factory).get(viewModelClass)
		attachComponents(bag, viewModel, render, actionable)
		return viewModel
	}

	fun <S : Any, T : BaseViewModel<S>> get(activity: FragmentActivity, actionable: UserActionable, factory: ViewModelProvider.Factory, bag: DisposeBag, viewModelClass: Class<T>, render: (S) -> Unit): T {
		val viewModel = ViewModelProviders.of(activity, factory).get(viewModelClass)
		attachComponents(bag, viewModel, render, actionable)
		return viewModel
	}

	private fun <S : Any, T : BaseViewModel<S>> attachComponents(bag: DisposeBag, viewModel: T, render: (S) -> Unit, actionable: UserActionable) {
		bag.add(viewModel.states().subscribeBy(onNext = { render(it) }))
		bag.add(actionable.userActions().subscribe(viewModel.userActionsConsumer()))
	}
}