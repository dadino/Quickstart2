package com.dadino.quickstart2.core.components

import android.util.Log
import com.dadino.quickstart2.core.entities.UserAction
import com.dadino.quickstart2.core.entities.UserActionable
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable

interface Actionable : UserActionable {
	val userActionsHandler: UserActionsHandler

	fun userActionsConsumer(): PublishRelay<UserAction> {
		return userActionsHandler.userActionsConsumer()
	}

	fun receiveUserAction(userAction: UserAction) {
		userActionsHandler.receiveUserAction(userAction)
	}

	override fun userActions(): Observable<UserAction> {
		return userActionsHandler.userActions()
	}

	fun collectUserActions(): Observable<UserAction> {
		return Observable.empty()
	}

	fun interceptUserAction(action: UserAction): UserAction {
		return action
	}
}

abstract class UserActionsHandler : UserActionable {
	private val userActionsRelay: PublishRelay<UserAction> by lazy { PublishRelay.create<UserAction>() }

	private val userActions: Observable<UserAction> by lazy {
		Observable.merge(collectUserActions(), userActionsRelay)
				.doOnNext { Log.d("UserAction", "Original: $it") }
				.map { interceptUserAction(it) }
				.doOnNext { Log.d("UserAction", "Intercepted: $it") }
				.publish()
				.refCount()
	}

	init {
		userActions().subscribe()
	}

	open fun collectUserActions(): Observable<UserAction> {
		return Observable.empty()
	}

	fun userActionsConsumer(): PublishRelay<UserAction> {
		return userActionsRelay
	}

	fun receiveUserAction(userAction: UserAction) {
		userActionsConsumer().accept(userAction)
	}

	final override fun userActions(): Observable<UserAction> {
		return userActions
	}

	protected open fun interceptUserAction(action: UserAction): UserAction {
		return action
	}

}