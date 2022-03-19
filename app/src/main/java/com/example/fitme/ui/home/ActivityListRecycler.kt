package com.example.fitme.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fitme.data.models.Activity
import com.example.fitme.databinding.ItemActivityBinding
import java.util.*

class ActivityListRecycler(
    private val items: ArrayList<Activity>,
    private val onAlarmClick: (id: Activity) -> Unit,
) : RecyclerView.Adapter<ActivityListRecycler.ViewHolder>() {

    inner class ViewHolder(val binding: ItemActivityBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Activity) {
            binding.tvActivityTitle.text = item.name
            val description = "${item.calories} kcal were burn out since ${Date(item.createdAt)} | ${item.seconds} seconds"
            binding.tvDescription.text = description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemActivityBinding.inflate(
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
    fun updateItems(items: List<Activity>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }
}