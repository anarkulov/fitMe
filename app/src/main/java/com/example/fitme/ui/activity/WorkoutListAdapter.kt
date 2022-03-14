package com.example.fitme.ui.activity

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fitme.data.models.Workout
import com.example.fitme.databinding.ItemWorkoutBinding

class WorkoutListAdapter(
    private val items: ArrayList<Workout>,
    private val onAlarmClick: (id: Workout) -> Unit,
) : RecyclerView.Adapter<WorkoutListAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemWorkoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Workout) {
            binding.tvActivityTitle.text = item.title
            binding.tvDescription.text = item.description
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
        holder.itemView.setOnClickListener {
            onAlarmClick(items[position])
        }
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems() {
        notifyDataSetChanged()
    }
}