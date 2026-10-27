package com.jei.campusbooking.data.entity

import androidx.room.ColumnInfo

/**
 * Result of a JOIN query (reservation + room + user). This is NOT a table.
 */
data class ReservationDetail(
    @ColumnInfo(name = "reservation_id") val reservationId: Int,
    @ColumnInfo(name = "room_name") val roomName: String,
    @ColumnInfo(name = "building") val building: String,
    @ColumnInfo(name = "full_name") val fullName: String,
    @ColumnInfo(name = "reservation_date") val reservationDate: String,
    @ColumnInfo(name = "start_time") val startTime: String,
    @ColumnInfo(name = "end_time") val endTime: String,
    @ColumnInfo(name = "purpose") val purpose: String,
    @ColumnInfo(name = "status") val status: String
)
