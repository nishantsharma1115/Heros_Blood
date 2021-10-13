package com.nishant.herosblood.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nishant.herosblood.databinding.SinglePreviousBloodRequestsLayoutBinding
import com.nishant.herosblood.models.BloodRequestData

class PreviousBloodRequestAdapter :
    ListAdapter<BloodRequestData, PreviousBloodRequestAdapter.SingleBloodRequest>(DiffUtil()) {
    class DiffUtil : androidx.recyclerview.widget.DiffUtil.ItemCallback<BloodRequestData>() {
        override fun areItemsTheSame(
            oldItem: BloodRequestData,
            newItem: BloodRequestData
        ): Boolean {
            return oldItem.createdAt == newItem.createdAt
        }

        override fun areContentsTheSame(
            oldItem: BloodRequestData,
            newItem: BloodRequestData
        ): Boolean {
            return oldItem == newItem
        }
    }

    class SingleBloodRequest(val binding: SinglePreviousBloodRequestsLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(request: BloodRequestData) {
            if (!request.isCritical.toBoolean()) {
                binding.txtCritical.visibility = View.GONE
            } else {
                binding.txtCritical.visibility = View.VISIBLE
            }
            binding.bloodRequest = request
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleBloodRequest {
        val inflater = LayoutInflater.from(parent.context)
        val binding = SinglePreviousBloodRequestsLayoutBinding.inflate(inflater, parent, false)
        return SingleBloodRequest(binding)
    }

    override fun onBindViewHolder(holder: SingleBloodRequest, position: Int) {
        val current = currentList[position]
        holder.bind(current)
    }
}