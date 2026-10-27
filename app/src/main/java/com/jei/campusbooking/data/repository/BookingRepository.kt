package com.jei.campusbooking.data.repository

import androidx.room.withTransaction
import com.jei.campusbooking.data.AppDatabase
import com.jei.campusbooking.data.entity.Reservation
import com.jei.campusbooking.data.entity.ReservationDetail
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/** Outcome of trying to create a booking. */
sealed class BookingResult {
    data class Success(val reservationId: Long) : BookingResult()
    object Conflict : BookingResult()
    object RoomClosed : BookingResult()
}

class BookingRepository(private val db: AppDatabase) {

    private val reservationDao = db.reservationDao()
    private val roomDao = db.studyRoomDao()

    fun getBookedSlots(roomId: Int, date: String): Flow<List<Reservation>> =
        reservationDao.getReservationsByRoomAndDate(roomId, date)

    fun getMyBookings(userId: Int): Flow<List<ReservationDetail>> =
        reservationDao.getReservationDetailsByUser(userId)

    /**
     * Checks the room is open and the slot is free, then inserts the reservation.
     * Both steps run in ONE transaction so two bookings cannot sneak in between.
     */
    suspend fun createBooking(
        userId: Int,
        roomId: Int,
        date: String,
        startTime: String,
        endTime: String,
        purpose: String
    ): BookingResult = db.withTransaction {
        val room = roomDao.getRoomById(roomId)
        if (room == null || !room.isAvailable) {
            return@withTransaction BookingResult.RoomClosed
        }

        val conflicts = reservationDao.countConflicts(roomId, date, startTime, endTime)
        if (conflicts > 0) {
            return@withTransaction BookingResult.Conflict
        }

        val now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        val id = reservationDao.insertReservation(
            Reservation(
                userId = userId,
                roomId = roomId,
                reservationDate = date,
                startTime = startTime,
                endTime = endTime,
                purpose = purpose,
                createdAt = now
            )
        )
        BookingResult.Success(id)
    }

    suspend fun cancel(reservationId: Int) {
        reservationDao.updateStatus(reservationId, Reservation.Status.CANCELLED)
    }

    suspend fun checkOut(reservationId: Int) {
        reservationDao.updateStatus(reservationId, Reservation.Status.COMPLETED)
    }
}
