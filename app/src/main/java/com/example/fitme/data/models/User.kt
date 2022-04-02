package com.example.fitme.data.models

data class User(
    var id: String = "",
    var firstName: String? = "",
    var lastName: String?  = "",
    var email: String?  = "",
    var phone: String?  = "",
    var country: String?  = "",
    var state: String?  = "",
    var city: String?  = "",
    var age: Int? = 0,
    var gender: String? = "",
    var height: Float? = 0f,
    var weight: Float? = 0f,
    var plan: String? = ""
)