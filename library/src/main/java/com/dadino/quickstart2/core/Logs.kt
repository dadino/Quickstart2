package com.dadino.quickstart2.core

import android.text.TextUtils
import android.util.Log
import java.util.*

class Logs {

    companion object {
        private val DEBUG = 0
        private val INFO = 1
        private val WARNING = 2
        private val WTF = 3
        private val ERROR = 4
        private val VERBOSE = 5

        private val T_UI = "UI_CALL"
        private val T_RETROFIT = "RETROFIT"
        private val T_RX = "RX"
        private val T_PRESENTER = "PRESENTER"
        private val T_TOKEN = "AUTH"
        private val T_FCM = "FCM"
        private val T_ERROR = "ERROR"
        private val T_MODEL = "MODEL_CALL"
        private val T_LOGIN = "LOGIN_CALL"
        private val T_REPOSITORY = "REPOSITORY_CALL"
    }

    private val logger = Logger()

    fun setEnableLogging(enable: Boolean) {
        logger.isEnableLogging = enable
    }

    fun setEnabledTag(tag: String, enabled: Boolean) {
        logger.setEnabledTag(tag, enabled)
    }

    fun ui(message: String) {
        log(T_UI, message)
    }

    fun retrofit(message: String) {
        log(T_RETROFIT, message)
    }

    fun rx(message: String) {
        log(T_RX, message)
    }

    fun presenter(message: String) {
        log(T_PRESENTER, message)
    }

    fun token(message: String) {
        log(T_TOKEN, message)
    }

    fun fcm(message: String) {
        log(T_FCM, message)
    }

    fun error(message: String) {
        log(T_ERROR, message)
    }

    fun model(message: String) {
        log(T_MODEL, message)
    }

    fun login(message: String) {
        log(T_LOGIN, message)
    }

    fun repository(message: String) {
        log(T_REPOSITORY, message)
    }

    fun ui(message: String, logLevel: Int) {
        log(T_UI, message, logLevel)
    }

    fun retrofit(message: String, logLevel: Int) {
        log(T_RETROFIT, message, logLevel)
    }

    fun rx(message: String, logLevel: Int) {
        log(T_RX, message, logLevel)
    }

    fun presenter(message: String, logLevel: Int) {
        log(T_PRESENTER, message, logLevel)
    }

    fun token(message: String, logLevel: Int) {
        log(T_TOKEN, message, logLevel)
    }

    fun fcm(message: String, logLevel: Int) {
        log(T_FCM, message, logLevel)
    }

    fun error(message: String, logLevel: Int) {
        log(T_ERROR, message, logLevel)
    }

    fun model(message: String, logLevel: Int) {
        log(T_MODEL, message, logLevel)
    }

    fun login(message: String, logLevel: Int) {
        log(T_LOGIN, message, logLevel)
    }

    fun repository(message: String, logLevel: Int) {
        log(T_REPOSITORY, message, logLevel)
    }

    fun log(tag: String, message: String) {
        log(tag, message, DEBUG)
    }

    fun log(tag: String, message: String, logLevel: Int) {
        if (logger.canLog(tag) && !TextUtils.isEmpty(message)) {
            when (logLevel) {
                DEBUG -> Log.d(tag, message)
                ERROR -> Log.e(tag, message)
                INFO -> Log.i(tag, message)
                VERBOSE -> Log.v(tag, message)
                WARNING -> Log.w(tag, message)
                WTF -> Log.wtf(tag, message)
            }
        }
    }

    private class Logger internal constructor() {

        var isEnableLogging: Boolean = false
        private val enabledLogs: MutableMap<String, Boolean>

        init {
            enabledLogs = HashMap<String, Boolean>()
            isEnableLogging = true
            setEnabledTag(T_UI, true)
            setEnabledTag(T_RETROFIT, true)
            setEnabledTag(T_RX, true)
            setEnabledTag(T_PRESENTER, true)
            setEnabledTag(T_TOKEN, true)
            setEnabledTag(T_FCM, true)
            setEnabledTag(T_ERROR, true)
            setEnabledTag(T_MODEL, true)
            setEnabledTag(T_LOGIN, true)
            setEnabledTag(T_REPOSITORY, true)
        }

        internal fun canLog(tag: String): Boolean {
            return enabledLogs.containsKey(tag) && enabledLogs.getOrDefault(tag, false) && isEnableLogging
        }

        internal fun setEnabledTag(tag: String, enabled: Boolean) {
            enabledLogs.put(tag, enabled)
        }


    }
}