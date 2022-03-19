package com.example.fitme.utils

class Constants {

    object Config {
        const val PHONE_PATTERN_DEFAULT = "(###) ###-####"
        const val PHONE_PATTERN_KG = "(###) ###-###"
        const val PHONE_PATTERN_RU = "(###) ###-##-##"
        const val PHONE_PATTERN_BR = "(##) ####-####"
        const val SEARCH_MIN_KEYS = 3
        const val SEARCH_DELAY = 1000L
    }

    object Date {
        const val TIME_FORMAT = "hh:mmaa"
        const val DATE_FORMAT = "dd MMM hh:mmaa"
        const val DATE_FORMAT_SERVER = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    }

    object DEFAULT {
        const val EMAIL = "EMAIL"
        const val PASSWORD = "PASSWORD"
        const val FIRST_NAME = "FIRST_NAME"
        const val LAST_NAME = "LAST_NAME"
        const val UID = "UID"
        const val PHONE = "PHONE"
        const val COUNTRY = "COUNTRY"
        const val STATE = "STATE"
        const val CITY = "CITY"
    }

    object Collection {
        const val USERS_PATH = "users"
        const val TIMESTAMP_FIELD = "time"
        const val NAME_FIELD = "name"
        const val PHONE_FIELD = "phone number"

        const val MILESTONE = "milestone"
        const val PORTFOLIO = "portfolio"
        const val PROJECTS = "projects"
        const val REVISION = "revision"
        const val USERS = "users"
    }

}