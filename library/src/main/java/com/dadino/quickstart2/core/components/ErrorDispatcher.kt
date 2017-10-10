package com.dadino.quickstart2.core.components

object ErrorDispatcherController {
	var dispatcher: ErrorDispatcher? = null
}

interface ErrorDispatcher {
	fun dispatchError(error: Throwable)
}