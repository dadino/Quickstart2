package com.dadino.quickstart2.core.sample.di

import android.content.Context
import com.dadino.quickstart2.core.sample.SampleApp
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class AppModule {

	@Provides
	@Singleton
	internal fun provideApplication(application: SampleApp): Context {
		return application
	}
}