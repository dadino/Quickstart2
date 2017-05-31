package com.dadino.quickstart2.core

open class Model<T>(val item: T? = null, val inProgress: Boolean = false, val error: Throwable? = null) : StateCreator<T> {
	override fun success(item: T): Model<T> {
		throw RuntimeException("success not implemented")
	}

}

interface StateCreator<T> {
	fun success(item: T): Model<T>

	fun error(error: Throwable): Model<T> {
		return Model(error = error)
	}

	fun inProgress(): Model<T> {
		return Model(inProgress = true)
	}

	fun idle(): Model<T> {
		return Model()
	}
}