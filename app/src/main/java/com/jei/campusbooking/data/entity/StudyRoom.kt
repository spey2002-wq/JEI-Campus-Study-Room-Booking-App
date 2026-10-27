package com.jei.campusbooking.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "study_rooms",
    indices = [Index(value = ["room_name"], unique = true)]
)
data class StudyRoom(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "room_id")
    val roomId: Int = 0,

    @ColumnInfo(name = "room_name")
    val roomName: String,

    @ColumnInfo(name = "building")
    val building: String,

    @ColumnInfo(name = "floor")
    val floor: Int,

    @ColumnInfo(name = "capacity")
    val capacity: Int,

    @ColumnInfo(name = "equipment", defaultValue = "")
    val equipment: String = "",

    // Room stores Boolean as INTEGER (true = 1, false = 0)
    @ColumnInfo(name = "is_available", defaultValue = "1")
    val isAvailable: Boolean = true
)
