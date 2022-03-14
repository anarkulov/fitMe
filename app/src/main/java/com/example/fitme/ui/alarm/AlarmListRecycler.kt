package com.example.fitme.ui.alarm

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fitme.data.models.Alarm
import com.example.fitme.databinding.ItemAlarmBinding
import com.example.fitme.utils.Utils

class AlarmListRecycler(
    private val items: ArrayList<Alarm>,
    private val onAlarmClick: (id: Alarm) -> Unit,
    private val onAlarmSwitch: (title: String, time: Long, on: Boolean) -> Unit,
) : RecyclerView.Adapter<AlarmListRecycler.ViewHolder>() {

    inner class ViewHolder(val binding: ItemAlarmBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Alarm, onAlarmSwitch: (title: String, time: Long, on: Boolean) -> Unit) {
            binding.tvAlarmTime.text = Utils.getDateTime(item.timestamp)
            binding.tvAlarmTitle.text = item.title
            binding.tvDuration.text =
                when (item.frequency.size) {
                    7 -> {
                        item.frequency.sort()
                        "${item.frequency[0]} - ${item.frequency[6]}"
                    }
                    else -> {
                        var text = ""
                        for ((index, day) in item.frequency.withIndex()) {
                            if (index == item.frequency.size - 1) {
                                text += day
                            } else {
                                text += "$day, "
                            }
                        }
                        text
                    }
                }

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
    fun updateItems() {
        notifyDataSetChanged()
    }
}