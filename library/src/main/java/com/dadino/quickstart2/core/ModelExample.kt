package com.dadino.quickstart2.core

import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy

class ModelExample {

	fun example() {
		Flowable.just(1)
				.map { ExampleModel.success(it) }
				.onErrorReturn { ExampleModel.error(it) }
				.observeOn(AndroidSchedulers.mainThread())
				.startWith(ExampleModel.inProgress())
				.subscribeBy(
						onNext = { println(it.item) },
						onError = { it.printStackTrace() },
						onComplete = { println("Done!") }
							)

	}
}

class ExampleModel(number: Int) : Model<Int>(number) {

	companion object : StateCreator<Int> {
		override fun success(item: Int): Model<Int> {
			return ExampleModel(item)
		}
	}
}