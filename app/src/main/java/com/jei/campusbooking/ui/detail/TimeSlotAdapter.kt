package com.jei.campusbooking.ui.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jei.campusbooking.data.entity.Reservation
import com.jei.campusbooking.databinding.ItemTimeSlotBinding

class TimeSlotAdapter :
    ListAdapter<Reservation, TimeSlotAdapter.SlotViewHolder>(DIFF) {

    class SlotViewHolder(private val binding: ItemTimeSlotBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Reservation) {
            binding.tvSlotTime.text = "${item.startTime} - ${item.endTime}"
            binding.tvSlotPurpose.text = item.purpose
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlotViewHolder {
        val binding = ItemTimeSlotBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SlotViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SlotViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Reservation>() {
            override fun areItemsTheSame(old: Reservation, new: Reservation) =
                old.reservationId == new.reservationId

            override fun areContentsTheSame(old: Reservation, new: Reservation) = old == new
        }
    }
}
