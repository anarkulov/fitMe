package com.example.fitme.ui.alarm.pose

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fitme.databinding.ItemPoseBinding

class PoseBottomSheetAdapter(
    private val data: List<String>,
    private val click: (pose: String) -> Unit,
) : RecyclerView.Adapter<PoseBottomSheetAdapter.PoseBottomSheetViewHolder>() {

    class PoseBottomSheetViewHolder(val binding: ItemPoseBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(s: String) {
            binding.tvPoseName.text = s
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PoseBottomSheetViewHolder {
        return PoseBottomSheetViewHolder(ItemPoseBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false))
    }

    override fun onBindViewHolder(holder: PoseBottomSheetViewHolder, position: Int) {
        holder.onBind(data[position])
        holder.itemView.setOnClickListener {
            click(data[position])
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}