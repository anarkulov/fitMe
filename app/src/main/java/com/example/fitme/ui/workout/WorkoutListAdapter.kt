package com.example.fitme.ui.workout

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fitme.R
import com.example.fitme.core.extentions.loadUrl
import com.example.fitme.data.models.Exercise
import com.example.fitme.data.models.Workout
import com.example.fitme.databinding.ItemExerciseBinding
import com.example.fitme.databinding.ItemWorkoutBinding

class WorkoutListAdapter(
    private val onMoreClick: ((Workout) -> Unit)? = null,
    private val onExerciseClick: ((Exercise) -> Unit)? = null,
    private val type: Int
) : RecyclerView.Adapter<WorkoutListAdapter.ViewHolder>() {

    private val workoutItems = ArrayList<Workout>()
    private val exerciseItems = ArrayList<Exercise>()

    inner class ViewHolder : RecyclerView.ViewHolder {
        lateinit var workoutBinding: ItemWorkoutBinding
        lateinit var exerciseBinding: ItemExerciseBinding

        constructor(workoutBinding: ItemWorkoutBinding) : super(workoutBinding.root) {
            this.workoutBinding = workoutBinding
        }

        fun bind(item: Workout, onMoreClick: (workout: Workout) -> Unit) {
            workoutBinding.tvActivityTitle.text = item.name
            workoutBinding.tvExercises.text =
                itemView.context.getString(R.string.exercises_format, item.exercises)
            workoutBinding.ivWorkout.loadUrl(item.imageUrl, R.drawable.ic_pushup)
            workoutBinding.btnViewMore.setOnClickListener {
                onMoreClick(item)
            }
        }

        constructor(exerciseBinding: ItemExerciseBinding) : super(exerciseBinding.root) {
            this.exerciseBinding = exerciseBinding
        }

        fun bind(item: Exercise) {
            exerciseBinding.tvTitle.text = item.name
            exerciseBinding.tvTime.text =
                itemView.context.getString(R.string.exercises_format, item.minutes)
            exerciseBinding.ivExercise.loadUrl(item.imageUrl, R.drawable.ic_pushup)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (type) {
            0 -> {
                ViewHolder(
                    ItemWorkoutBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            else -> {
                ViewHolder(
                    ItemExerciseBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (type) {
            0 -> {
                if (onMoreClick != null) {
                    holder.bind(workoutItems[position], onMoreClick)
                }
            }
            else -> {
                onExerciseClick?.let { onExerciseClick ->
                    holder.bind(exerciseItems[position])
                    holder.itemView.setOnClickListener {
                        onExerciseClick(exerciseItems[position])
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = if (type == 0) workoutItems.size else exerciseItems.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateWorkoutItems(items: List<Workout>) {
        this.workoutItems.clear()
        this.workoutItems.addAll(items)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateExerciseItems(items: List<Exercise>) {
        this.exerciseItems.clear()
        this.exerciseItems.addAll(items)
        notifyDataSetChanged()
    }
}