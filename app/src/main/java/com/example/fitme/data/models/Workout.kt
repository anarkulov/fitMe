package com.example.fitme.data.models

data class Workout(
    var docId: String = "",
    var name: String = "",
    var description: String ="",
    var exercises: Int = 0,
    var imageUrl: String = ""
)