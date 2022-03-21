package com.example.fitme.data.models

import java.io.Serializable

data class Exercise(
    var docId: String = "",
    val name: String = "",
    val description: String = "",
    val instructions: String = "",
    val minutes: Int = 0,
    val imageUrl: String = "",
    val videoUrl: String = "",
    val exercise: String = ""
): Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Exercise

        if (docId != other.docId) return false
        if (name != other.name) return false
        if (description != other.description) return false
        if (instructions != other.instructions) return false
        if (minutes != other.minutes) return false
        if (imageUrl != other.imageUrl) return false
        if (videoUrl != other.videoUrl) return false
        if (exercise != other.exercise) return false

        return true
    }

    override fun hashCode(): Int {
        var result = docId.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + instructions.hashCode()
        result = 31 * result + minutes
        result = 31 * result + imageUrl.hashCode()
        result = 31 * result + videoUrl.hashCode()
        result = 31 * result + exercise.hashCode()
        return result
    }
}