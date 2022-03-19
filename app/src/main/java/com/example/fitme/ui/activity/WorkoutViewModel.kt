package com.example.fitme.ui.activity

import androidx.lifecycle.LiveData
import com.example.fitme.core.network.result.Resource
import com.example.fitme.core.ui.BaseViewModel
import com.example.fitme.data.models.Workout

class WorkoutViewModel(private val workoutRepository: WorkoutRepository): BaseViewModel() {

    fun getWorkoutList(): LiveData<Resource<List<Workout>>> {
        return workoutRepository.getWorkoutList()
    }
}