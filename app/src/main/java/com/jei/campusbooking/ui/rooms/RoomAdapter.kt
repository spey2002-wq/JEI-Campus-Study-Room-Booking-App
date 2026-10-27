package com.jei.campusbooking.ui.rooms

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jei.campusbooking.R
import com.jei.campusbooking.data.entity.StudyRoom
import com.jei.campusbooking.databinding.ItemRoomBinding

class RoomAdapter(
    private val onRoomClick: (StudyRoom) -> Unit
) : ListAdapter<StudyRoom, RoomAdapter.RoomViewHolder>(DIFF) {

    inner class RoomViewHolder(private val binding: ItemRoomBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(room: StudyRoom) {
            val ctx = binding.root.context
            binding.tvRoomName.text = room.roomName
            binding.tvRoomLocation.text =
                ctx.getString(R.string.room_floor_format, room.building, room.floor)
            binding.tvRoomCapacity.text = ctx.getString(R.string.capacity_format, room.capacity)

            val (label, color) = if (room.isAvailable) {
                R.string.badge_available to R.color.badge_available
            } else {
                R.string.badge_closed to R.color.badge_closed
            }
            binding.tvRoomStatus.setText(label)
            binding.tvRoomStatus.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(ctx, color))

            binding.cardRoom.setOnClickListener { onRoomClick(room) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val binding = ItemRoomBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RoomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<StudyRoom>() {
            override fun areItemsTheSame(old: StudyRoom, new: StudyRoom) = old.roomId == new.roomId
            override fun areContentsTheSame(old: StudyRoom, new: StudyRoom) = old == new
        }
    }
}
