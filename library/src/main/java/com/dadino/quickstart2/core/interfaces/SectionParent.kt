package com.dadino.quickstart2.core.interfaces

import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import com.dadino.quickstart2.core.entities.UserAction
import com.jakewharton.rxrelay2.PublishRelay


interface SectionParent : DisposableLifecycleHolder {
	fun getSectionContainer(): ViewGroup
	fun showError(error: Throwable)
	fun getActivity(): AppCompatActivity
	fun userActionsConsumer(): PublishRelay<UserAction>
}
