package com.dadino.quickstart2.core.sample.di

import com.dadino.quickstart2.core.sample.repositories.ISessionRepository
import com.dadino.quickstart2.core.sample.repositories.MemorySessionRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class RepositoryModule {
	@Provides
	@Singleton
	fun providesSessionRepository(): ISessionRepository {
		return MemorySessionRepository()
	}

}