package com.example.fitme.data.models

import android.os.Parcel
import android.os.Parcelable

data class Activity(
    var id: String = "",
    var docId: String = "",
    var name: String = "",
    var description: String = "",
    var counters: Int = 0,
    var seconds: Int = 0,
    var calories: Int = 0,
    var workout: String = "",
    var imageUrl: String = "",
    var createdAt: Long = 0
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readLong(),
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(docId)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeInt(counters)
        parcel.writeInt(seconds)
        parcel.writeInt(calories)
        parcel.writeString(workout)
        parcel.writeString(imageUrl)
        parcel.writeLong(createdAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Activity> {
        override fun createFromParcel(parcel: Parcel): Activity {
            return Activity(parcel)
        }

        override fun newArray(size: Int): Array<Activity?> {
            return arrayOfNulls(size)
        }
    }
}