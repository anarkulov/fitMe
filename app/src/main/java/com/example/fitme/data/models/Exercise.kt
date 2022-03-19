package com.example.fitme.data.models

data class Exercise(
    var docId: String,
    val name: String,
    val description: String,
    val instructions: String,
    val imageUrl: String,
    val videoUrl: String,
    val exercise: String
)