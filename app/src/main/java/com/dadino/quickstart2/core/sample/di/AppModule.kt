package com.dadino.quickstart2.core.sample.di

import com.dadino.quickstart2.core.sample.repositories.ISessionRepository
import com.dadino.quickstart2.core.sample.repositories.MemorySessionRepository
import com.dadino.quickstart2.core.sample.viewmodels.CounterViewModel
import com.dadino.quickstart2.core.sample.viewmodels.SpinnerViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module


object AppModule {
	val module: Module = module {
		single { MemorySessionRepository() as ISessionRepository }
		viewModel { SpinnerViewModel(get()) }
		viewModel { CounterViewModel() }
	}
}
