package com.dadino.quickstart2.core.interfaces


interface ErrorHandler {
	fun formatError(error: Throwable?): String
	fun getCustomErrorCode(error: Throwable?): Int
}