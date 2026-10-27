package com.jei.campusbooking.ui.rooms

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jei.campusbooking.data.AppDatabase
import com.jei.campusbooking.data.entity.StudyRoom
import com.jei.campusbooking.data.repository.RoomRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class RoomListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository =
        RoomRepository(AppDatabase.getDatabase(application).studyRoomDao())

    private val query = MutableStateFlow("")
    private val minCapacity = MutableStateFlow(0)

    /** null = still loading, otherwise the filtered list (may be empty). */
    val rooms: StateFlow<List<StudyRoom>?> =
        combine(repository.getAllRooms(), query, minCapacity) { all, q, min ->
            val text = q.trim()
            all.filter { room ->
                room.capacity >= min &&
                    (text.isEmpty() ||
                        room.roomName.contains(text, ignoreCase = true) ||
                        room.building.contains(text, ignoreCase = true))
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun setQuery(text: String) {
        query.value = text
    }

    fun setMinCapacity(value: Int) {
        minCapacity.value = value
    }
}
