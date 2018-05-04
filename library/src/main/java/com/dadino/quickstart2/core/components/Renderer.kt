package com.dadino.quickstart2.core.components

import com.dadino.quickstart2.core.entities.UiState

interface Renderer<in STATE : UiState> {
	fun render(state: STATE)
}