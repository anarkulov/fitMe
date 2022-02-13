package com.example.fitme.core.extentions

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.Toast
import androidx.core.content.ContextCompat

fun Context.fetchColor(id: Int): Int {
    return ContextCompat.getColor(this, id)
}

fun Context.fetchDrawable(id: Int): Drawable? {
    return ContextCompat.getDrawable(this, id)
}

fun Context.showMessage(message: Int) {
    Toast.makeText(this, getString(message), Toast.LENGTH_SHORT).show()
}