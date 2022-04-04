package com.example.fitme.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fitme.R
import com.example.fitme.core.extentions.loadUrl
import com.example.fitme.core.extentions.visible
import com.example.fitme.data.models.Activity
import com.example.fitme.databinding.ItemMyActivityBinding
import com.example.fitme.utils.Utils
import com.google.firebase.Timestamp

class ActivityListRecycler(
    private val items: ArrayList<Activity>,
    private val onAlarmClick: (id: String) -> Unit,
) : RecyclerView.Adapter<ActivityListRecycler.ViewHolder>() {

    inner class ViewHolder(val binding: ItemMyActivityBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Activity) {
            binding.tvActivityTitle.text = item.name
            val date = Utils.formatTimestampDate(Timestamp(item.createdAt/1000, 0))
            val description = itemView.context.getString(R.string.calories_format, item.calories) + " since $date | ${item.seconds} seconds"
            binding.tvDescription.text = description
            binding.ivActivity.loadUrl(item.imageUrl)
            binding.ivBackground.visible = item.imageUrl.isEmpty()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemMyActivityBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            onAlarmClick(items[position].id)
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