package com.dadino.quickstart2.core.repositories

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.dadino.quickstart2.core.interfaces.IRepository

abstract class PrefRepository(context: Context) : IRepository, SharedPreferences.OnSharedPreferenceChangeListener {

	private val prefs: SharedPreferences

	protected var mContext: Context

	init {
		mContext = context.applicationContext
		prefs = PreferenceManager.getDefaultSharedPreferences(context)
		prefs.registerOnSharedPreferenceChangeListener(this)
	}

	override fun onDestroy() {
		prefs.unregisterOnSharedPreferenceChangeListener(this)
	}

	protected abstract fun listenOn(): String

	protected fun pref(): SharedPreferences {
		return prefs
	}

	protected fun editor(): SharedPreferences.Editor {
		return prefs.edit()
	}

	override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, s: String) {
		if (listenOn() == s) onPrefChanged()
	}

	protected abstract fun onPrefChanged()

	protected abstract val key: String
}