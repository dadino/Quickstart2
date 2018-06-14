package com.dadino.quickstart2.core.interfaces

import android.arch.lifecycle.ViewModelProvider
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import com.dadino.quickstart2.core.entities.UserAction
import com.jakewharton.rxrelay2.PublishRelay


interface SectionParent : ViewModelFactoryHolder, DisposableLifecycleHolder {
	fun getSectionContainer(): ViewGroup
	fun showError(error: Throwable)
	fun getActivity(): AppCompatActivity
	fun userActionsConsumer(): PublishRelay<UserAction>
}

interface ViewModelFactoryHolder {
	fun viewModelFactory(): ViewModelProvider.Factory
}

