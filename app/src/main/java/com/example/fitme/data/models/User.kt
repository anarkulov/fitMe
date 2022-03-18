package com.example.fitme.data.models

data class User(
    var id: String = "",
    var firstName: String? = null,
    var lastName: String?  = null,
    var email: String?  = null,
    var phone: String?  = null,
    var country: String?  = null,
    var state: String?  = null,
    var city: String?  = null,
    var age: String? = null,
    var height: String? = null,
    var weight: String? = null
)
{
    //    "firstName" to firstName,
//    "lastName" to lastName,
//    "email" to email,
//    "phone" to phone,
//    "country" to country,
//    "state" to state,
//    "city" to city
}