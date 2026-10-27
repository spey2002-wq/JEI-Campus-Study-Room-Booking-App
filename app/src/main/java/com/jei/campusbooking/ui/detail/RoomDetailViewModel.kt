package com.jei.campusbooking.ui.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.jei.campusbooking.data.AppDatabase
import com.jei.campusbooking.data.entity.Reservation
import com.jei.campusbooking.data.entity.StudyRoom
import com.jei.campusbooking.data.repository.BookingRepository
import com.jei.campusbooking.data.repository.RoomRepository
import com.jei.campusbooking.util.DateUtil
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

class RoomDetailViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    // "roomId" is the navigation argument (the Fragment's arguments feed the SavedStateHandle)
    private val roomId: Int = savedStateHandle["roomId"] ?: -1

    private val db = AppDatabase.getDatabase(application)
    private val roomRepository = RoomRepository(db.studyRoomDao())
    private val bookingRepository = BookingRepository(db)

    val room: StateFlow<StudyRoom?> =
        roomRepository.observeRoom(roomId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate

    @OptIn(ExperimentalCoroutinesApi::class)
    val bookedSlots: StateFlow<List<Reservation>> =
        _selectedDate
            .flatMapLatest { date -> bookingRepository.getBookedSlots(roomId, DateUtil.dateToDb(date)) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun nextDay() {
        _selectedDate.value = _selectedDate.value.plusDays(1)
    }

    fun previousDay() {
        // Booking in the past makes no sense, so don't go before today
        val prev = _selectedDate.value.minusDays(1)
        if (!prev.isBefore(LocalDate.now())) _selectedDate.value = prev
    }

    fun roomId(): Int = roomId
}
