package com.example.fitme.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.fitme.core.extentions.showToast
import com.example.fitme.core.network.result.Status
import com.example.fitme.core.ui.BaseFragment
import com.example.fitme.core.utils.Log
import com.example.fitme.data.models.Workout
import com.example.fitme.databinding.FragmentActivityBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class WorkoutFragment : BaseFragment<WorkoutViewModel, FragmentActivityBinding>() {

    private val myTag = "ActivityFragment"

    override val viewModel: WorkoutViewModel by viewModel()
    private val workoutList = ArrayList<Workout>()
    private val workoutAdapter = WorkoutListAdapter(workoutList, this::onWorkoutClick)

    override fun initViewModel() {
        super.initViewModel()

        viewModel.getWorkoutList().observe(this) { response ->
            when (response.status) {
                Status.LOADING -> {
                    viewModel.loading.postValue(true)
                }
                Status.ERROR -> {
                    viewModel.loading.postValue(false)
                }
                Status.SUCCESS -> {
                    viewModel.loading.postValue(false)
                    response.data?.let {
                        Log.d("getWorkoutList: $it", myTag)
                        workoutAdapter.updateItems(it)
                    }
                }
            }
        }
    }

    override fun initView() {
        super.initView()

        initWorkoutRecyclerView()
    }

    private fun initWorkoutRecyclerView() {

        binding.workoutRecyclerView.apply {
            this.adapter = workoutAdapter
            layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
        }
    }

    private fun onWorkoutClick(workout: Workout) {
        showToast(workout.name)
    }

    override fun initListeners() {
        super.initListeners()
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): FragmentActivityBinding {
        return FragmentActivityBinding.inflate(inflater, container, false)
    }

    override fun bindViewBinding(view: View): FragmentActivityBinding {
        return FragmentActivityBinding.bind(view)
    }

}