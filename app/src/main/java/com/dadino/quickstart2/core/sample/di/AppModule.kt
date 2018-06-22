package com.dadino.quickstart2.core.sample.di

import com.dadino.quickstart2.core.sample.repositories.ISessionRepository
import com.dadino.quickstart2.core.sample.repositories.MemorySessionRepository
import com.dadino.quickstart2.core.sample.viewmodels.SpinnerViewModel
import org.koin.android.architecture.ext.viewModel
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext


object AppModule {
	val module: Module = applicationContext {
		bean { MemorySessionRepository() as ISessionRepository }
		viewModel { SpinnerViewModel(get()) }
	}
}
