package com.example.fitme.ui.alarm

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fitme.R
import com.example.fitme.core.utils.Log
import com.example.fitme.data.models.Alarm
import com.example.fitme.databinding.ItemAlarmBinding

class AlarmListRecycler(
    private var items: ArrayList<Alarm>,
    private val onAlarmClick: (id: Alarm) -> Unit,
    private val onAlarmSwitch: (alarm: Alarm, checked: Boolean) -> Unit,
) : RecyclerView.Adapter<AlarmListRecycler.ViewHolder>() {

    inner class ViewHolder(val binding: ItemAlarmBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Alarm, onAlarmSwitch: (alarm: Alarm, isChecked: Boolean) -> Unit) {
            val splitTime = item.time.split(":")
            val hour = splitTime[0].toInt()
            val minute = splitTime[1].toInt()
            val time = "${itemView.context.getString(R.string.hour_format, hour)}:${itemView.context.getString(R.string.hour_format, minute)}"
            binding.tvAlarmTime.text = time
            binding.tvAlarmTitle.text = item.title
            Log.d("alarm: $item", "RecyclerAlarm")

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
                item.isTurnedOn = isChecked
                onAlarmSwitch(item, isChecked)
                updateItemValue(absoluteAdapterPosition, item)
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
        item.sortedBy {
            it.id
        }
        items.clear()
        items.addAll(item)
        notifyDataSetChanged()
    }

    private fun updateItemValue(position: Int, item: Alarm) {
        items[position] = item
    }
}