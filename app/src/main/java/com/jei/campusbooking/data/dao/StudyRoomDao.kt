package com.jei.campusbooking.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.jei.campusbooking.data.entity.StudyRoom
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyRoomDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertRoom(room: StudyRoom): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRooms(rooms: List<StudyRoom>): List<Long>

    @Update
    suspend fun updateRoom(room: StudyRoom): Int

    @Delete
    suspend fun deleteRoom(room: StudyRoom): Int

    @Query("SELECT * FROM study_rooms ORDER BY building ASC, room_name ASC")
    fun getAllRooms(): Flow<List<StudyRoom>>

    @Query("SELECT * FROM study_rooms WHERE is_available = 1 ORDER BY room_name ASC")
    fun getAvailableRooms(): Flow<List<StudyRoom>>

    @Query("SELECT * FROM study_rooms WHERE room_id = :roomId")
    suspend fun getRoomById(roomId: Int): StudyRoom?

    @Query("SELECT * FROM study_rooms WHERE room_id = :roomId")
    fun observeRoomById(roomId: Int): Flow<StudyRoom?>

    @Query("SELECT * FROM study_rooms WHERE building = :building AND is_available = 1")
    fun getRoomsByBuilding(building: String): Flow<List<StudyRoom>>

    @Query("SELECT * FROM study_rooms WHERE capacity >= :minCapacity AND is_available = 1 ORDER BY capacity ASC")
    fun getRoomsByMinCapacity(minCapacity: Int): Flow<List<StudyRoom>>

    @Query("SELECT * FROM study_rooms WHERE room_name LIKE :keyword OR building LIKE :keyword")
    fun searchRooms(keyword: String): Flow<List<StudyRoom>>

    @Query("UPDATE study_rooms SET is_available = :available WHERE room_id = :roomId")
    suspend fun setRoomAvailability(roomId: Int, available: Boolean): Int
}
