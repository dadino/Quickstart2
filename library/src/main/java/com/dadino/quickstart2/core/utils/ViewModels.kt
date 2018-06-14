package com.dadino.quickstart2.core.utils

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import com.dadino.quickstart2.core.components.BaseViewModel


object ViewModels {

	fun <S : Any, T : BaseViewModel<S>> getViewModel(fragment: Fragment,
													 factory: ViewModelProvider.Factory,
													 viewModelClass: Class<T>
	): T {
		return ViewModelProviders.of(fragment, factory).get(viewModelClass)
	}

	fun <S : Any, T : BaseViewModel<S>> getViewModel(activity: FragmentActivity,
													 factory: ViewModelProvider.Factory,
													 viewModelClass: Class<T>
	): T {
		return ViewModelProviders.of(activity, factory).get(viewModelClass)

	}
}