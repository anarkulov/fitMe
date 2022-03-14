package com.example.fitme.data.models

import java.io.Serializable

data class Alarm(
    val id: String,
    val timestamp: Long,
    val title: String,
    val frequency: Array<Int>,
    val isChallenge: Boolean,
    val challenge: String?,
    var isTurnedOn: Boolean
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Alarm

        if (id != other.id) return false
        if (timestamp != other.timestamp) return false
        if (title != other.title) return false
        if (!frequency.contentEquals(other.frequency)) return false
        if (isChallenge != other.isChallenge) return false
        if (challenge != other.challenge) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + timestamp.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + frequency.contentHashCode()
        result = 31 * result + isChallenge.hashCode()
        result = 31 * result + (challenge?.hashCode() ?: 0)
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
