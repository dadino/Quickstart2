package com.dadino.quickstart2.core.sample

import android.app.Application
import com.dadino.quickstart2.core.sample.di.AppModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class SampleApp : Application() {
	override fun onCreate() {
		super.onCreate()

		startKoin {
			modules(AppModule.module)
			androidContext(this@SampleApp)
		}
	}


}