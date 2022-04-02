package com.example.fitme.ui.workout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.fitme.core.extentions.visible
import com.example.fitme.core.network.result.Status
import com.example.fitme.core.ui.BaseFragment
import com.example.fitme.core.utils.Log
import com.example.fitme.data.models.Workout
import com.example.fitme.databinding.FragmentWorkoutBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class WorkoutFragment : BaseFragment<WorkoutViewModel, FragmentWorkoutBinding>() {

    private val myTag = "ActivityFragment"

    override val viewModel: WorkoutViewModel by viewModel()
    private val workoutAdapter = WorkoutListAdapter(this::onWorkoutClick, type = 0)

    override fun initViewModel() {
        super.initViewModel()

        viewModel.loading.observe(this) {
            binding.loading.visible = it
        }

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
                        workoutAdapter.updateWorkoutItems(it)
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
        findNavController().navigate(WorkoutFragmentDirections.actionActivityFragmentToWorkoutDetailsFragment(workout))
    }

    override fun initListeners() {
        super.initListeners()
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): FragmentWorkoutBinding {
        return FragmentWorkoutBinding.inflate(inflater, container, false)
    }

    override fun bindViewBinding(view: View): FragmentWorkoutBinding {
        return FragmentWorkoutBinding.bind(view)
    }

}