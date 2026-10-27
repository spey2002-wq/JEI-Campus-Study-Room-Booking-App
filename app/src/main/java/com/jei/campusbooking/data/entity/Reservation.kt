package com.jei.campusbooking.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reservations",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["user_id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = StudyRoom::class,
            parentColumns = ["room_id"],
            childColumns = ["room_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["room_id"])
    ]
)
data class Reservation(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "reservation_id")
    val reservationId: Int = 0,

    @ColumnInfo(name = "user_id")
    val userId: Int,

    @ColumnInfo(name = "room_id")
    val roomId: Int,

    @ColumnInfo(name = "reservation_date")
    val reservationDate: String,      // "yyyy-MM-dd"

    @ColumnInfo(name = "start_time")
    val startTime: String,            // "HH:mm"

    @ColumnInfo(name = "end_time")
    val endTime: String,              // "HH:mm"

    @ColumnInfo(name = "purpose")
    val purpose: String,

    @ColumnInfo(name = "status", defaultValue = "CONFIRMED")
    val status: String = Status.CONFIRMED, // CONFIRMED / CANCELLED / COMPLETED

    @ColumnInfo(name = "created_at")
    val createdAt: String
) {
    object Status {
        const val CONFIRMED = "CONFIRMED"
        const val CANCELLED = "CANCELLED"
        const val COMPLETED = "COMPLETED"
    }
}
