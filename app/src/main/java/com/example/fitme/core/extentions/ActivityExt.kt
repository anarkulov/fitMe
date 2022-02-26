package com.example.fitme.core.extentions

import android.app.Activity
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.fitme.R
import com.example.fitme.utils.Utils
import com.google.android.material.snackbar.Snackbar

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

fun Activity.showSnackBar(message: String?) {
    if (message == null) return
    val snackBar = Snackbar.make(window.decorView, message, Snackbar.LENGTH_LONG)
    val layoutParams = CoordinatorLayout.LayoutParams(snackBar.view.layoutParams)
    layoutParams.gravity = Gravity.TOP
    layoutParams.topMargin = Utils.toDp(25f, this)
    snackBar.view.layoutParams = layoutParams
    snackBar.setTextColor(fetchColor(R.color.white))
    snackBar.setActionTextColor(fetchColor(R.color.white))
    snackBar.setAction("X"){
        snackBar.dismiss()
    }.show()
}