package com.dadino.quickstart2.core.repositories

import android.content.Context
import com.dadino.quickstart2.core.interfaces.IRepository
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single

abstract class PrefStringRepository(context: Context) : PrefRepository(context), IStringRepository {

	private var subject: BehaviorRelay<String> = BehaviorRelay.create()

	override protected fun listenOn(): String {
		return key
	}

	override protected fun onPrefChanged() {
		subject.accept(pref)
	}

	override fun retrieve(): Flowable<String> {
		if (subject.hasValue().not()) {
			subject.accept(pref)
		}
		return subject.toFlowable(BackpressureStrategy.LATEST)
	}

	override fun create(string: String): Single<Boolean> {
		return Single.just(editor().putString(key, string)
								   .commit())
	}

	override fun delete(): Single<Boolean> {
		return Single.just(editor().remove(key)
								   .commit())
	}

	override fun update(string: String): Single<Boolean> {
		return create(string)
	}

	private val pref: String
		get() = pref().getString(key, default)

	protected abstract val default: String
}

interface IStringRepository : IRepository {

	fun retrieve(): Flowable<String>
	fun create(string: String): Single<Boolean>
	fun delete(): Single<Boolean>
	fun update(string: String): Single<Boolean>
}