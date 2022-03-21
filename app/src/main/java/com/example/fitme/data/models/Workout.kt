package com.example.fitme.data.models

import java.io.Serializable

data class Workout(
    var docId: String = "",
    var name: String = "",
    var description: String ="",
    var exercises: Int = 0,
    var imageUrl: String = ""
):Serializable