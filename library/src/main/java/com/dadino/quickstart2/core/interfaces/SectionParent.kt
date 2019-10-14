package com.dadino.quickstart2.core.interfaces

import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.dadino.quickstart2.core.entities.UserAction
import com.jakewharton.rxrelay2.PublishRelay


interface SectionParent : DisposableLifecycleHolder {
	fun getSectionContainer(): ViewGroup
	fun showError(error: Throwable)
	fun getActivity(): AppCompatActivity
	fun userActionsConsumer(): PublishRelay<UserAction>
}
