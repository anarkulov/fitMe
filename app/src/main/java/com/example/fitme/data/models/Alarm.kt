package com.example.fitme.data.models

import java.io.Serializable

data class Alarm(
    var id: String = "",
    var docId: String = "",
    var time: String = "",
    var title: String = "",
    var days: ArrayList<Boolean> = ArrayList(),
    var challenge: String = "none",
    var isTurnedOn: Boolean = false,
    var timeInMs: Long = 0,
    var isRepeatable: Boolean = false,
    var isPlayed: Boolean = false,
    var isVibrated: Boolean = true,
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Alarm

        if (id != other.id) return false
        if (time != other.time) return false
        if (title != other.title) return false
        if (challenge != other.challenge) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + time.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + (challenge.hashCode() ?: 0)
        return result
    }
}


enum class Challenge {
    StandUp, Warrior, Cobra, Dog, Tree,
    Wave, Star
}

//enum class Pose {
//    StandUp, Warrior, Cobra, Dog, Tree
//}
//
//enum class WarmUp {
//    Wave, Star
//}
