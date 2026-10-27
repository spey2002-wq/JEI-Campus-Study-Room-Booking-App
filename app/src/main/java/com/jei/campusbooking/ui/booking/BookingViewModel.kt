package com.jei.campusbooking.ui.booking

import android.app.Application
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.jei.campusbooking.R
import com.jei.campusbooking.data.AppDatabase
import com.jei.campusbooking.data.entity.StudyRoom
import com.jei.campusbooking.data.repository.BookingRepository
import com.jei.campusbooking.data.repository.BookingResult
import com.jei.campusbooking.data.repository.RoomRepository
import com.jei.campusbooking.util.DateUtil
import com.jei.campusbooking.util.SessionManager
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/** One-time results shown by the booking form. */
sealed class BookingEvent {
    data class Error(@param:StringRes val messageRes: Int) : BookingEvent()
    object Saved : BookingEvent()
}

class BookingViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val roomId: Int = savedStateHandle["roomId"] ?: -1

    private val db = AppDatabase.getDatabase(application)
    private val bookingRepository = BookingRepository(db)
    private val roomRepository = RoomRepository(db.studyRoomDao())
    private val session = SessionManager(application)

    val room: StateFlow<StudyRoom?> =
        roomRepository.observeRoom(roomId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    // Form state (kept in the ViewModel so it survives screen rotation)
    val date = MutableStateFlow(initialDate(savedStateHandle["selectedDate"]))
    val startTime = MutableStateFlow<LocalTime?>(null)
    val endTime = MutableStateFlow<LocalTime?>(null)

    private val _events = Channel<BookingEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private fun initialDate(arg: String?): LocalDate {
        val parsed = DateUtil.parseDateOrNull(arg)
        val today = LocalDate.now()
        return if (parsed == null || parsed.isBefore(today)) today else parsed
    }

    /** Validates the form, checks for conflicts and saves the reservation. */
    fun submit(purpose: String) {
        val d = date.value
        val start = startTime.value
        val end = endTime.value

        if (start == null || end == null) {
            send(BookingEvent.Error(R.string.error_pick_all)); return
        }
        if (d.isBefore(LocalDate.now())) {
            send(BookingEvent.Error(R.string.error_past_date)); return
        }
        if (LocalDateTime.of(d, start).isBefore(LocalDateTime.now())) {
            send(BookingEvent.Error(R.string.error_past_time)); return
        }
        if (!end.isAfter(start)) {
            send(BookingEvent.Error(R.string.error_end_before_start)); return
        }
        if (Duration.between(start, end).toMinutes() > MAX_MINUTES) {
            send(BookingEvent.Error(R.string.error_too_long)); return
        }

        viewModelScope.launch {
            val result = bookingRepository.createBooking(
                userId = session.currentUserId,
                roomId = roomId,
                date = DateUtil.dateToDb(d),
                startTime = DateUtil.timeToDb(start),
                endTime = DateUtil.timeToDb(end),
                purpose = purpose
            )
            when (result) {
                is BookingResult.Success -> send(BookingEvent.Saved)
                BookingResult.Conflict -> send(BookingEvent.Error(R.string.error_conflict))
                BookingResult.RoomClosed -> send(BookingEvent.Error(R.string.error_room_closed))
            }
        }
    }

    private fun send(event: BookingEvent) {
        _events.trySend(event)
    }

    private companion object {
        const val MAX_MINUTES = 180L // 3 hours
    }
}
