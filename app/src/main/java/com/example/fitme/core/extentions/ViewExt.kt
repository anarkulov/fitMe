package com.example.fitme.core.extentions

import android.app.Activity
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.annotation.MenuRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.example.fitme.core.utils.Log
import com.google.android.material.textfield.TextInputLayout
import com.squareup.picasso.Picasso
import java.text.DecimalFormat
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

fun View.showPopup(
    @MenuRes menu: Int,
    onOptionPick: (Int) -> Unit
) {
    PopupMenu(context, this).apply {
        inflate(menu)
        setOnMenuItemClickListener { item ->
            onOptionPick(item.itemId)
            return@setOnMenuItemClickListener true
        }
        show()
    }
}

var View.visible: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) View.VISIBLE else View.GONE
    }

var View.invisible: Boolean
    get() = visibility == View.INVISIBLE
    set(value) {
        visibility = if (value) View.INVISIBLE else View.VISIBLE
    }

fun View?.removeFocus() = try {
    val imm = this?.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager?
    imm?.hideSoftInputFromWindow(this?.windowToken, 0)
} catch (e: Exception) {
    Log.e(e)
}

fun View.showKeyboard(toggleKeyboard: Boolean = true) {
    try {
        if (context != null && context is Activity) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE)
            if (imm is InputMethodManager) {
                this.requestFocus()
                if (toggleKeyboard) {
                    imm.toggleSoftInput(
                        InputMethodManager.SHOW_IMPLICIT,
                        InputMethodManager.HIDE_IMPLICIT_ONLY
                    )
                } else {
                    imm.showSoftInput(this, 0)
                }
            }
        }
    } catch (e: Exception) {
        Log.e(e)
    }
}

fun View.below(view: View) {
    (this.layoutParams as? RelativeLayout.LayoutParams)?.addRule(RelativeLayout.BELOW, view.id)
}

fun View.setMargins(newLeft: Int, newTop: Int, newRight: Int, newBottom: Int) {
    (layoutParams as? ViewGroup.MarginLayoutParams)?.let {
        it.setMargins(newLeft, newTop, newRight, newBottom)
        this.layoutParams = it
    }
}

fun View.setTopMargin(top: Int) {
    (layoutParams as? ViewGroup.MarginLayoutParams)?.let {
        it.topMargin = top
        this.layoutParams = it
    }
}

fun ViewGroup.inflate(@LayoutRes layoutId: Int, attach: Boolean = false): View =
    LayoutInflater.from(context).inflate(layoutId, this, attach)

fun TextView.setTextColorRes(@ColorRes colorRes: Int) {
    setTextColor(ContextCompat.getColor(this.context, colorRes))
}

fun TextInputLayout.showError(@StringRes errorRes: Int, vibrate: Boolean = true) {
    val message = context.getString(errorRes)
    error = message
}

fun TextInputLayout.showError(message: String, vibrate: Boolean = false) {
    error = message
}

fun TextInputLayout.hideError() {
    error = null
    isErrorEnabled = false
}

fun EditText.setEditable(value: Boolean) {
    this.isFocusable = value
    this.isFocusableInTouchMode = value
    this.isClickable = value
}

fun EditText.getPhoneNumber(code: String): String {
    return "$code${text.toString().replace("\\D+".toRegex(), "")}"
}

fun ImageView.loadUrl(url: String?) {

    if (url == null) {
        return
    }

    Picasso.get()
        .load(url)
        .into(this)
}

fun ImageView.loadUrl(url: String, placeholderResId: Int) {

    if (url.isEmpty()) {
        return
    }

    Picasso.get()
        .load(url)
        .placeholder(placeholderResId)
        .into(this)
}

fun Int.formatCount(): String {
    val suffix = charArrayOf(' ', 'K', 'M', 'B', 'T', 'P', 'E')
    val numValue: Long = this.toLong()
    val value = floor(log10(numValue.toDouble())).toInt()
    val base = value / 3
    return if (value >= 3 && base < suffix.size) {
        DecimalFormat("#0").format(
            numValue / 10.0.pow((base * 3).toDouble())
        ) + suffix[base]
    } else {
        DecimalFormat("#,##0").format(numValue)
    }
}

fun TextView.setTintColor(colorRes: Int) {

    for (drawable in this.compoundDrawables) {
        if (drawable != null) {
            drawable.colorFilter = PorterDuffColorFilter(colorRes, PorterDuff.Mode.SRC_IN)
        }
    }
}