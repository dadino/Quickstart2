package com.dadino.quickstart2.core.components

import com.dadino.quickstart2.core.entities.StateCommand


interface Reducer<STATE> {
	fun reduce(previous: STATE, command: StateCommand): STATE
}