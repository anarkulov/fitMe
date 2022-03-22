package com.example.fitme.ui.workout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitme.core.extentions.loadUrl
import com.example.fitme.core.extentions.visible
import com.example.fitme.core.network.result.Status
import com.example.fitme.core.ui.BaseFragment
import com.example.fitme.core.utils.Log
import com.example.fitme.data.models.Exercise
import com.example.fitme.databinding.FragmentWorkoutDetailsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class WorkoutDetailsFragment : BaseFragment<WorkoutViewModel, FragmentWorkoutDetailsBinding>(){

    private val myTag = "WorkoutDetailsFragment"

    override val viewModel: WorkoutViewModel by viewModel()
    private val args: WorkoutDetailsFragmentArgs by navArgs()

    private val exerciseAdapter = WorkoutListAdapter(onExerciseClick = this::onExerciseClick, type = 1)


    override fun initViewModel() {
        super.initViewModel()

        viewModel.loading.observe(this) {
            binding.loading.visible = it
        }

        viewModel.getExerciseList(args.workout.docId).observe(this) { response ->
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
                        exerciseAdapter.updateExerciseItems(it)
                    }
                }
            }
        }
    }

    override fun initView() {
        super.initView()

        initRecyclerView()
        setData()
    }

    private fun setData() {
        val data = args.workout

        binding.ivActivity.loadUrl(data.imageUrl)
        binding.tvTitle.text = data.name
        binding.tvWorkoutDescription.text = data.description
    }

    private fun initRecyclerView() {
        binding.workoutRecyclerView.apply {
            this.adapter = exerciseAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    override fun initListeners() {
        super.initListeners()

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun onExerciseClick(exercise: Exercise) {
        findNavController().navigate(WorkoutDetailsFragmentDirections.actionWorkoutDetailsFragmentToExerciseFragment(exercise))
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentWorkoutDetailsBinding {
        return FragmentWorkoutDetailsBinding.inflate(inflater, container, false)
    }

    override fun bindViewBinding(view: View): FragmentWorkoutDetailsBinding {
        return FragmentWorkoutDetailsBinding.bind(view)
    }
}