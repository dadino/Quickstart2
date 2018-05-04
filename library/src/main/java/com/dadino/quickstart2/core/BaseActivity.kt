package com.dadino.quickstart2.core

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.dadino.quickstart2.core.components.Actionable
import com.dadino.quickstart2.core.components.UserActionsHandler
import com.dadino.quickstart2.core.di.Injectable
import com.dadino.quickstart2.core.entities.UserAction
import io.reactivex.Observable

abstract class BaseActivity : AppCompatActivity(), Actionable, Injectable {
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
}