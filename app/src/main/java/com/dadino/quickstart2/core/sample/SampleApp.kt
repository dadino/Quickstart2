package com.dadino.quickstart2.core.sample

import android.app.Activity
import android.app.Application
import com.dadino.quickstart2.core.sample.di.AppInjector
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject


class SampleApp : Application(), HasActivityInjector {
	@Inject
	lateinit var activityDispatchingAndroidInjector: DispatchingAndroidInjector<Activity>


	override fun onCreate() {
		super.onCreate()
		AppInjector.init(this)
	}


	override fun activityInjector(): AndroidInjector<Activity> = activityDispatchingAndroidInjector
}