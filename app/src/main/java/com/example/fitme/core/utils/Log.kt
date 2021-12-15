package com.example.fitme.core.utils

import timber.log.Timber
import timber.log.Timber.DebugTree

object Log {
    private const val TAG = "Nooken"

    fun init() {
        Timber.plant(DebugTree())
    }

    fun d(message: String?, tag: String = TAG) {
        Timber.tag(tag).d(message)
    }

    fun d(message: String?, tag: Any?) {
        Timber.tag(tag?.javaClass?.simpleName ?: TAG).d(message)
    }

    fun i(message: String?, tag: String = TAG) {
        Timber.tag(tag).i(message)
    }

    fun e(message: String?, tag: String = TAG) {
        Timber.tag(tag).e(message)
    }

    fun e(message: Exception?, tag: String = TAG) {
        Timber.tag(tag).e(message?.printStackTrace().toString())
    }
}