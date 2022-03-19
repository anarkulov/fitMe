package com.example.fitme.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.fitme.core.ui.BaseFragment
import com.example.fitme.data.models.Workout
import com.example.fitme.databinding.FragmentActivityBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class ActivityFragment : BaseFragment<ActivityViewModel, FragmentActivityBinding>() {

    override val viewModel: ActivityViewModel by viewModel()

    private val workoutList = ArrayList<Workout>()

    override fun initViewModel() {
        super.initViewModel()
    }

    override fun initView() {
        super.initView()

        initWorkoutRecyclerView()
    }

    private fun initWorkoutRecyclerView() {
        for (i in 0 until 10) {
            workoutList.add(
                Workout("${i + i % (i + 1) * 2}",
                    "Fullbody Workout ${i+1}",
                "${i+1*i+1} Exercises}")
            )
        }

        val adapter = WorkoutListAdapter(workoutList, this::onWorkoutClick)

        binding.workoutRecyclerView.apply {
            this.adapter = adapter
            layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
        }
    }

    private fun onWorkoutClick(workout: Workout) {

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