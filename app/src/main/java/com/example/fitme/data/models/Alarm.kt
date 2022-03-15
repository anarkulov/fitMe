package com.example.fitme.data.models

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

data class Alarm(
    var id: String = "",
    var timestamp: String = "",
    var title: String = "",
    var days: ArrayList<Boolean> = ArrayList(),
    var challenge: String = "none",
    var isTurnedOn: Boolean = false,
    var timeInMs: Long = 0,
    var userId: String = ""
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Alarm

        if (id != other.id) return false
        if (timestamp != other.timestamp) return false
        if (title != other.title) return false
        if (challenge != other.challenge) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + timestamp.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + (challenge.hashCode() ?: 0)
        return result
    }

    fun convertHMtoMS(time: String, isRepeating: Boolean, days: TreeMap<Int, String>) : Long{
        val splitTime = time.split(":")
        val hour = splitTime[0].toInt()
        val minute = splitTime[1].toInt()

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }

        val simpleDateFormat = SimpleDateFormat("HH:mm:ss")
        val nowTime = simpleDateFormat.format(Date())

        val currentTime = nowTime.split(":")
        val hr = currentTime[0].toInt()

        if (hr >= 12) {
            calendar.set(Calendar.AM_PM, Calendar.AM)
        }

        timeInMs = calendar.timeInMillis

        if (isRepeating) {
            val timeSet = TreeSet<Long>()
            for (i in 0 until 7) {
                var repeatTime = timeInMs

                if (days.containsKey(i)) {
                    val calendar = Calendar.getInstance()
                    var currentDay = calendar.get(Calendar.DAY_OF_WEEK)
                    currentDay--
                    if ((repeatTime < System.currentTimeMillis() && currentDay == i) || currentDay != i) {
                        if (i > currentDay) {
                            repeatTime += TimeUnit.MICROSECONDS.convert((i - currentDay).toLong(), TimeUnit.DAYS)
                        } else {
                            repeatTime += TimeUnit.MICROSECONDS.convert((7 - currentDay).toLong(), TimeUnit.DAYS)
                            TimeUnit.MICROSECONDS.convert(i.toLong(), TimeUnit.DAYS)
                        }
                        timeSet.add(repeatTime)
                    } else if (currentDay == i) {
                        timeSet.add(repeatTime)
                    }
                }
            }

            if (timeSet.isNotEmpty()) {
                return timeSet.first()
            }
        }

        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DAY_OF_WEEK, 1)
        }

        timeInMs = calendar.timeInMillis
        return timeInMs
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
