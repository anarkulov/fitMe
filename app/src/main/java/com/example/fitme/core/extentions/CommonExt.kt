package com.example.fitme.core.extentions

import android.os.Handler
import android.os.Looper

fun runAfter(delayMillis: Long, method: () -> Unit) {
    Handler(Looper.getMainLooper()).postDelayed({
        method()
    }, delayMillis)
}

private fun ignoreCaseOpt(ignoreCase: Boolean) =
    if (ignoreCase) setOf(RegexOption.IGNORE_CASE) else emptySet()

fun String?.indexesOf(pat: String, ignoreCase: Boolean = true): List<Int> =
    pat.toRegex(ignoreCaseOpt(ignoreCase))
        .findAll(this ?: "")
        .map { it.range.first }
        .toList()