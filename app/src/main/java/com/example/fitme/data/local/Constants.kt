package com.example.fitme.data.local

class Constants {

    object Date {
        const val TIME_FORMAT = "hh:mmaa"
        const val DATE_FORMAT = "dd MMM hh:mmaa"
//        const val DATE_FORMAT_SERVER = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        const val DATE_FORMAT_SERVER = "ddd MMM HH:mm:ss.SSS'Z'"
    }

    object Home {
        const val PERIOD_MONTH = 0
        const val PERIOD_WEEK = 1
        const val PERIOD_DAY = 3
        const val PERIOD_ALL_TIME = 3

        const val TYPE_CALORIE = 0
        const val TYPE_COUNTERS = 1
        const val TYPE_SECONDS = 2
    }
}