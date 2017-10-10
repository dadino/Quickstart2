package com.dadino.quickstart2.core.entities

class OneOffAction {
	private var consumed: Boolean = false

	fun doAndConsume(action: () -> Unit) {
		if (consumed.not()) {
			action()
			consumed = true
		}
	}

	override fun toString(): String {
		return "{consumed: $consumed}"
	}

	companion object {
		fun doAndConsume(oneOff: OneOffAction?, action: () -> Unit) {
			oneOff?.let { it.doAndConsume { action() } }
		}
	}
}

class OneOffActionWithValue<out T>(private val value: T) {
	private var consumed: Boolean = false

	fun doAndConsume(action: (T) -> Unit) {
		if (consumed.not()) {
			action(value)
			consumed = true
		}
	}

	override fun toString(): String {
		return "{value: $value, consumed: $consumed}"
	}

	companion object {
		fun <T> doAndConsume(oneOff: OneOffActionWithValue<T>?, action: (T) -> Unit) {
			oneOff?.let { it.doAndConsume { action(it) } }
		}
	}
}

