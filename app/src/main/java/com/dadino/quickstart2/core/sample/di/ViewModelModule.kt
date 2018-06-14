package com.dadino.quickstart2.core.sample.di

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.dadino.quickstart2.core.di.ViewModelFactory
import com.dadino.quickstart2.core.di.ViewModelKey
import com.dadino.quickstart2.core.sample.viewmodels.SpinnerViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module(includes = [RepositoryModule::class])
abstract class ViewModelModule {

	@Binds
	@IntoMap
	@ViewModelKey(SpinnerViewModel::class)
	abstract fun bindSpinnerViewModel(spinnerViewModel: SpinnerViewModel): ViewModel


	@Binds
	abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}

