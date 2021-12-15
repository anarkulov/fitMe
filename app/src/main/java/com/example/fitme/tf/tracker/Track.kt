package com.example.fitme.tf.tracker

import com.example.fitme.data.models.Person

data class Track(
    val person: Person,
    val lastTimestamp: Long
)
