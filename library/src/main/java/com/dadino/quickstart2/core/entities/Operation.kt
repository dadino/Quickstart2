package com.dadino.quickstart2.core.entities

data class Operation(private val status: Int, val error: Throwable?) {

	val isInProgress: Boolean
		get() = status == STATUS_IN_PROGRESS

	val isDone: Boolean
		get() = status == STATUS_DONE

	val isIdle: Boolean
		get() = status == STATUS_IDLE

	val isInError: Boolean
		get() = error != null && status == STATUS_ERROR

	private fun statusName(status: Int): String {
		return when (status) {
			STATUS_IDLE        -> "IDLE"
			STATUS_ERROR       -> "ERROR"
			STATUS_IN_PROGRESS -> "IN_PROGRESS"
			STATUS_DONE        -> "DONE"
			else               -> "UKNONWN"
		}
	}

	override fun toString(): String {
		return "{error= $error, status= $status (${statusName(status)})}"
	}

	companion object {

		private val STATUS_IDLE = 0
		private val STATUS_IN_PROGRESS = 1
		private val STATUS_DONE = 2
		private val STATUS_ERROR = -1

		fun idle(): Operation {
			return Operation(STATUS_IDLE, null)
		}

		fun inProgress(): Operation {
			return Operation(STATUS_IN_PROGRESS, null)
		}

		fun done(): Operation {
			return Operation(STATUS_DONE, null)
		}

		fun error(error: Throwable): Operation {
			return Operation(STATUS_ERROR, error)
		}
	}
}
