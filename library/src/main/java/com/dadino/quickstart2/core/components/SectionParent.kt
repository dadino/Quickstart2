package com.dadino.quickstart2.core.components

import android.arch.lifecycle.ViewModelProvider
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import com.dadino.quickstart2.core.entities.UserAction
import com.jakewharton.rxrelay2.PublishRelay
import com.kizitonwose.android.disposebag.DisposeBag


interface SectionParent : ViewModelFactoryHolder, DisposeBagHolder {
	fun getSectionContainer(): ViewGroup
	fun showError(error: Throwable)
	fun getActivity(): AppCompatActivity
	fun userActionsConsumer(): PublishRelay<UserAction>
}

interface ViewModelFactoryHolder {
	fun viewModelFactory(): ViewModelProvider.Factory
}

interface DisposeBagHolder {
	fun disposeBag(): DisposeBag
}