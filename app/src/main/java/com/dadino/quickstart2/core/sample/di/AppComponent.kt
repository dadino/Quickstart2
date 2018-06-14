package com.dadino.quickstart2.core.sample.di

import com.dadino.quickstart2.core.sample.SampleApp
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
	AndroidInjectionModule::class,
	AppModule::class,
	ViewModelModule::class,
	ActivitiesBuilder::class
])
interface AppComponent {
	@Component.Builder
	interface Builder {
		@BindsInstance
		fun application(application: SampleApp): Builder

		fun build(): AppComponent
	}

	fun inject(androidApp: SampleApp)
}