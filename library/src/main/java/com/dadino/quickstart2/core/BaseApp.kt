package com.dadino.quickstart2.core

import android.app.Activity
import android.app.Application
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject


class BaseApp : Application(), HasActivityInjector {
	@Inject
	lateinit var activityDispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

	override fun activityInjector(): AndroidInjector<Activity> = activityDispatchingAndroidInjector

}