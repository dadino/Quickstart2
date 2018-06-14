package com.dadino.quickstart2.core.sample.di

import com.dadino.quickstart2.core.di.ActivityScope
import com.dadino.quickstart2.core.sample.SecondActivity
import com.dadino.quickstart2.core.sample.SpinnerActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivitiesBuilder {
	@ActivityScope
	@ContributesAndroidInjector(modules = [SpinnerModule::class])
	internal abstract fun contributeSpinnerActivity(): SpinnerActivity

	@ActivityScope
	@ContributesAndroidInjector(modules = [SecondModule::class])
	internal abstract fun contributeSecondActivity(): SecondActivity

}