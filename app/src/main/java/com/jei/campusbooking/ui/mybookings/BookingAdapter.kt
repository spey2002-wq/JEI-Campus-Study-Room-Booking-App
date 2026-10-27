package com.jei.campusbooking.ui.mybookings

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jei.campusbooking.R
import com.jei.campusbooking.data.entity.Reservation
import com.jei.campusbooking.data.entity.ReservationDetail
import com.jei.campusbooking.databinding.ItemBookingBinding

class BookingAdapter(
    private val onCancel: (ReservationDetail) -> Unit,
    private val onCheckOut: (ReservationDetail) -> Unit
) : ListAdapter<ReservationDetail, BookingAdapter.BookingViewHolder>(DIFF) {

    inner class BookingViewHolder(private val binding: ItemBookingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ReservationDetail) {
            val ctx = binding.root.context
            binding.tvBookingRoom.text = "${item.roomName} (${item.building})"
            binding.tvBookingTime.text = ctx.getString(
                R.string.time_range_format, item.reservationDate, item.startTime, item.endTime
            )
            binding.tvBookingPurpose.text = ctx.getString(R.string.purpose_format, item.purpose)

            binding.tvBookingStatus.text = item.status
            val color = when (item.status) {
                Reservation.Status.CONFIRMED -> R.color.status_confirmed
                Reservation.Status.COMPLETED -> R.color.status_completed
                else -> R.color.status_cancelled
            }
            binding.tvBookingStatus.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(ctx, color))

            // Cancel / Check-out only make sense for CONFIRMED bookings
            val active = item.status == Reservation.Status.CONFIRMED
            binding.layoutActions.visibility = if (active) View.VISIBLE else View.GONE
            binding.btnCancelBooking.setOnClickListener { onCancel(item) }
            binding.btnCheckOut.setOnClickListener { onCheckOut(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val binding = ItemBookingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<ReservationDetail>() {
            override fun areItemsTheSame(old: ReservationDetail, new: ReservationDetail) =
                old.reservationId == new.reservationId

            override fun areContentsTheSame(old: ReservationDetail, new: ReservationDetail) =
                old == new
        }
    }
}
