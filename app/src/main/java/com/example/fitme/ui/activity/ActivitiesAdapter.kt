package com.example.fitme.ui.activity

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fitme.R
import com.example.fitme.core.extentions.loadUrl
import com.example.fitme.data.models.Activity
import com.example.fitme.databinding.ItemActivityBinding
import com.example.fitme.utils.Utils
import com.google.firebase.Timestamp

class ActivitiesAdapter(
    private val items: ArrayList<Activity>
) : RecyclerView.Adapter<ActivitiesAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemActivityBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Activity) {
            binding.tvTitle.text = item.name
            val date = Utils.formatTimestampDate(Timestamp(item.createdAt/1000, 0))
            binding.tvDate.text = date
            binding.ivActivity.loadUrl(item.imageUrl)
            binding.tvCaloriesQuantity.text = itemView.context.getString(R.string.calories_format, item.calories)
            binding.tvCounterQuantity.text = itemView.context.getString(R.string.counter_format, item.counters)
            binding.tvTimeQuantity.text = itemView.context.getString(R.string.time_waste_format, item.seconds)
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