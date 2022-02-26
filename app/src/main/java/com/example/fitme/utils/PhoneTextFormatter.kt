package com.example.fitme.utils

import android.text.Editable
import android.text.InputFilter.LengthFilter
import android.text.TextWatcher
import android.widget.EditText

class PhoneTextFormatter(private val mEditText: EditText, private val mPattern: String) :
    TextWatcher {
    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        val phone = StringBuilder(s)
        if (count > 0 && !isValid(phone.toString())) {
            for (i in phone.indices) {
                val digit = mPattern[i].toString()
                if (digit != "#" && digit != phone[i].toString()) {
                    phone.insert(i, digit)
                }
            }
            mEditText.setText(phone)
            mEditText.setSelection(mEditText.text.length)
        }
        mEditText.filters = arrayOf(LengthFilter(mPattern.length))
    }

    override fun afterTextChanged(s: Editable) {}
    private fun isValid(phone: String): Boolean {
        for (i in phone.indices) {
            try {
                val digit = mPattern[i].toString()
                if (digit == "#") continue
                if (digit != phone[i].toString()) {
                    return false
                }
            } catch (e: StringIndexOutOfBoundsException) {
                e.printStackTrace()
            }
        }
        return true
    }
}