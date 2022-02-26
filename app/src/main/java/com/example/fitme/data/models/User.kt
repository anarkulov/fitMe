package com.example.fitme.data.models

data class User(
    var id: String,
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val phone: String?,
    val country: String?,
    val state: String?,
    val city: String?
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