package com.example.fitme.core.utils

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

object Toast {
    fun show(context: Context?, message: String?) {
        context?.let { Toast.makeText(it, message, Toast.LENGTH_LONG).show() }
    }
    fun show(context: Context?, @StringRes resId: Int) {
        context?.let { Toast.makeText(it, it.getString(resId), Toast.LENGTH_LONG).show() }
    }

    fun showShort(context: Context?, message: String) {
        context?.let { Toast.makeText(it, message, Toast.LENGTH_SHORT).show() }
    }
    fun showShort(context: Context?, @StringRes resId: Int) {
        context?.let { Toast.makeText(it, it.getString(resId), Toast.LENGTH_SHORT).show() }
    }
}