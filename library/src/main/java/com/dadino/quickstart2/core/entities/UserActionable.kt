package com.dadino.quickstart2.core.entities

import io.reactivex.Observable

interface UserActionable {
	fun userActions(): Observable<UserAction>
}