
package com.example.fitme.tf.ml

import android.graphics.Bitmap
import com.example.fitme.data.models.Person

interface PoseDetector : AutoCloseable {

    fun estimatePoses(bitmap: Bitmap): List<Person>

    fun lastInferenceTimeNanos(): Long
}
