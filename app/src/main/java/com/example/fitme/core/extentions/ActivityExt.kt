package com.example.fitme.core.extentions

import android.app.Activity
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.fitme.R

fun Activity.setLightStatusBar(isLight: Boolean) {
    WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = isLight
    window.statusBarColor = if (isLight) fetchColor(R.color.white) else fetchColor(R.color.black)
}

fun Activity.setStatusBarColor(color: Int) {
    WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
    window.statusBarColor = fetchColor(color)
}

fun Activity.fetchColor(id: Int): Int {
    return ContextCompat.getColor(this, id)
}

fun Activity.inflate(layout: Int): View? {
    return layoutInflater.inflate(layout, null)
}

fun Activity.showToast(message: String?) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}
fun Activity.showToast(@StringRes resId: Int) {
    Toast.makeText(this, this.getString(resId), Toast.LENGTH_LONG).show()
}