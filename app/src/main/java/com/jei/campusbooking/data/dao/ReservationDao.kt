package com.jei.campusbooking.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.jei.campusbooking.data.entity.Reservation
import com.jei.campusbooking.data.entity.ReservationDetail
import kotlinx.coroutines.flow.Flow

@Dao
interface ReservationDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertReservation(reservation: Reservation): Long

    @Update
    suspend fun updateReservation(reservation: Reservation): Int

    @Delete
    suspend fun deleteReservation(reservation: Reservation): Int

    @Query("SELECT * FROM reservations WHERE reservation_id = :id")
    suspend fun getReservationById(id: Int): Reservation?

    @Query(
        """
        SELECT * FROM reservations
        WHERE user_id = :userId
        ORDER BY reservation_date DESC, start_time DESC
        """
    )
    fun getReservationsByUser(userId: Int): Flow<List<Reservation>>

    // Reservation joined with room & user info, for the "My Bookings" list
    @Query(
        """
        SELECT r.reservation_id, s.room_name, s.building, u.full_name,
               r.reservation_date, r.start_time, r.end_time,
               r.purpose, r.status
        FROM reservations r
        INNER JOIN study_rooms s ON r.room_id = s.room_id
        INNER JOIN users u       ON r.user_id = u.user_id
        WHERE r.user_id = :userId
        ORDER BY r.reservation_date DESC, r.start_time DESC
        """
    )
    fun getReservationDetailsByUser(userId: Int): Flow<List<ReservationDetail>>

    // Booked slots of one room on one day (for the room detail screen)
    @Query(
        """
        SELECT * FROM reservations
        WHERE room_id = :roomId
          AND reservation_date = :date
          AND status = 'CONFIRMED'
        ORDER BY start_time ASC
        """
    )
    fun getReservationsByRoomAndDate(roomId: Int, date: String): Flow<List<Reservation>>

    // Number of overlapping CONFIRMED bookings (0 = slot is free).
    // Two slots overlap when: newStart < existingEnd AND newEnd > existingStart
    @Query(
        """
        SELECT COUNT(*) FROM reservations
        WHERE room_id = :roomId
          AND reservation_date = :date
          AND status = 'CONFIRMED'
          AND :startTime < end_time
          AND :endTime   > start_time
        """
    )
    suspend fun countConflicts(
        roomId: Int,
        date: String,
        startTime: String,
        endTime: String
    ): Int

    @Query("UPDATE reservations SET status = 'CANCELLED' WHERE reservation_id = :id")
    suspend fun cancelReservation(id: Int): Int

    @Query("UPDATE reservations SET status = :status WHERE reservation_id = :id")
    suspend fun updateStatus(id: Int, status: String): Int

    @Query(
        """
        SELECT * FROM reservations
        WHERE user_id = :userId
          AND status = 'CONFIRMED'
          AND reservation_date >= :today
        ORDER BY reservation_date ASC, start_time ASC
        """
    )
    fun getUpcomingReservations(userId: Int, today: String): Flow<List<Reservation>>

    @Query("SELECT * FROM reservations ORDER BY reservation_date DESC, start_time DESC")
    fun getAllReservations(): Flow<List<Reservation>>
}
