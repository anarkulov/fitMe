package com.example.fitme.ui.workout

import androidx.lifecycle.MutableLiveData
import com.example.fitme.core.network.result.Resource
import com.example.fitme.data.models.Exercise
import com.example.fitme.data.models.Workout
import com.example.fitme.repo.UserDatabase

class WorkoutRepository(private val db: UserDatabase) {

    fun getWorkoutList(): MutableLiveData<Resource<List<Workout>>> {
        return db.getWorkouts()
    }

    fun getExerciseList(id: String): MutableLiveData<Resource<List<Exercise>>> {
        return db.getExercisesByWorkoutId(id)
    }
}