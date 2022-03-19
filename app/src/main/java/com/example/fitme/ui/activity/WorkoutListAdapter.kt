package com.example.fitme.ui.activity

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fitme.R
import com.example.fitme.core.extentions.loadUrl
import com.example.fitme.data.models.Workout
import com.example.fitme.databinding.ItemWorkoutBinding

class WorkoutListAdapter(
    private val items: ArrayList<Workout>,
    private val onMoreClick: (workout: Workout) -> Unit,
) : RecyclerView.Adapter<WorkoutListAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemWorkoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Workout, onMoreClick: (workout: Workout) -> Unit) {
            binding.tvActivityTitle.text = item.name
            binding.tvExercises.text = itemView.context.getString(R.string.exercises_format, item.exercises)
            binding.ivWorkout.loadUrl(item.imageUrl, R.drawable.ic_pushup)
            binding.btnViewMore.setOnClickListener {
                onMoreClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemWorkoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], onMoreClick)
    }

    override fun getItemCount(): Int = items.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(items: List<Workout>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }
}