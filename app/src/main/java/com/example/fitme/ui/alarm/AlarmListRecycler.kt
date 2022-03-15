package com.example.fitme.ui.alarm

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fitme.R
import com.example.fitme.data.models.Alarm
import com.example.fitme.databinding.ItemAlarmBinding

class AlarmListRecycler(
    private var items: List<Alarm>,
    private val onAlarmClick: (id: Alarm) -> Unit,
    private val onAlarmSwitch: (title: String, time: String, on: Boolean) -> Unit,
) : RecyclerView.Adapter<AlarmListRecycler.ViewHolder>() {

    inner class ViewHolder(val binding: ItemAlarmBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Alarm, onAlarmSwitch: (title: String, time: String, on: Boolean) -> Unit) {
            val splitTime = item.timestamp.split(":")
            val hour = splitTime[0].toInt()
            val minute = splitTime[1].toInt()
            val time = "${itemView.context.getString(R.string.hour_format, hour)}:${itemView.context.getString(R.string.hour_format, minute)}"
            binding.tvAlarmTime.text = time
            binding.tvAlarmTitle.text = item.title

            var days = ""
            for ((index, day) in item.days.withIndex()) {
                if (day) {
                    when(index) {
                        0 -> {days += "M"}
                        1 -> {days += " T"}
                        2 -> {days += " W"}
                        3 -> {days += " Th"}
                        4 -> {days += " F"}
                        5 -> {days += " S"}
                        6 -> {days += " S"}
                    }
                }
            }

            binding.tvDuration.text = days.trim()
            binding.btnSwitch.isChecked = item.isTurnedOn

            binding.btnSwitch.setOnCheckedChangeListener { _, isChecked ->
                onAlarmSwitch(item.title, item.timestamp, isChecked)
                item.isTurnedOn = isChecked
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemAlarmBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], onAlarmSwitch)
        holder.itemView.setOnClickListener {
            onAlarmClick(items[position])
        }
    }

    override fun getItemCount(): Int = items.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(item: List<Alarm>) {
        items = item
        notifyDataSetChanged()
    }
}