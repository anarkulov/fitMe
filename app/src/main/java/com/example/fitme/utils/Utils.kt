package com.example.fitme.utils

import com.example.fitme.core.utils.CoreUtils

object Utils: CoreUtils() {

    fun getTestPhoneNumber(): String {
        return "+7 (999) 999-9999"
    }
    fun getTestPhoneNumberOTP(): String {
        return "999999"
    }

    fun formatPrice(price: Int): String {
        return String.format("%, d", price).replace(",", " ")
    }

}