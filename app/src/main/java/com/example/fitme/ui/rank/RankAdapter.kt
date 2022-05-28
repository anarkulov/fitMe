package com.example.fitme.ui.rank

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fitme.core.extentions.loadUrl
import com.example.fitme.data.models.User
import com.example.fitme.databinding.ItemRankBinding

class RankAdapter(
    private val data: List<User>,
    private val click: (pose: String) -> Unit,
) : RecyclerView.Adapter<RankAdapter.PoseBottomSheetViewHolder>() {

    class PoseBottomSheetViewHolder(val binding: ItemRankBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(user: User) {
            val text = user.lastName + " " + user.firstName
            binding.tvName.text = text
            binding.ivAvatar.loadUrl(user.image)
            binding.tvRank.text = user.rank.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PoseBottomSheetViewHolder {
        return PoseBottomSheetViewHolder(ItemRankBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false))
    }

    override fun onBindViewHolder(holder: PoseBottomSheetViewHolder, position: Int) {
        holder.onBind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }
}