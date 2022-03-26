package com.example.fitme.ui.workout

import androidx.lifecycle.LiveData
import com.example.fitme.core.network.result.Resource
import com.example.fitme.core.ui.BaseViewModel
import com.example.fitme.data.models.Exercise
import com.example.fitme.data.models.Workout

class WorkoutViewModel(private val workoutRepository: WorkoutRepository): BaseViewModel() {

    fun getWorkoutList(): LiveData<Resource<List<Workout>>> {
        return workoutRepository.getWorkoutList()
    }

    fun getExerciseList(id: String): LiveData<Resource<List<Exercise>>> {
        return workoutRepository.getExerciseList(id)
    }
}