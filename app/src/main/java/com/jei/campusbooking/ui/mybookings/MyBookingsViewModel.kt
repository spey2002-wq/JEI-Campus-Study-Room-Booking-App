package com.jei.campusbooking.ui.mybookings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jei.campusbooking.R
import com.jei.campusbooking.data.AppDatabase
import com.jei.campusbooking.data.entity.ReservationDetail
import com.jei.campusbooking.data.repository.BookingRepository
import com.jei.campusbooking.util.SessionManager
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MyBookingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = BookingRepository(AppDatabase.getDatabase(application))
    private val userId = SessionManager(application).currentUserId

    /** null = show all statuses. */
    private val statusFilter = MutableStateFlow<String?>(null)

    val bookings: StateFlow<List<ReservationDetail>?> =
        combine(repository.getMyBookings(userId), statusFilter) { all, status ->
            if (status == null) all else all.filter { it.status == status }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val _messages = Channel<Int>(Channel.BUFFERED)
    val messages = _messages.receiveAsFlow()

    fun setStatusFilter(status: String?) {
        statusFilter.value = status
    }

    fun cancel(reservationId: Int) {
        viewModelScope.launch {
            repository.cancel(reservationId)
            _messages.trySend(R.string.booking_cancelled)
        }
    }

    fun checkOut(reservationId: Int) {
        viewModelScope.launch {
            repository.checkOut(reservationId)
            _messages.trySend(R.string.booking_completed)
        }
    }
}
