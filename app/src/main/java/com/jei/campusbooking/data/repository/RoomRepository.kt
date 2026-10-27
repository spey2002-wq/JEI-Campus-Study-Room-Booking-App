package com.jei.campusbooking.data.repository

import com.jei.campusbooking.data.dao.StudyRoomDao
import com.jei.campusbooking.data.entity.StudyRoom
import kotlinx.coroutines.flow.Flow

class RoomRepository(private val studyRoomDao: StudyRoomDao) {

    fun getAllRooms(): Flow<List<StudyRoom>> = studyRoomDao.getAllRooms()

    fun observeRoom(roomId: Int): Flow<StudyRoom?> = studyRoomDao.observeRoomById(roomId)

    suspend fun getRoom(roomId: Int): StudyRoom? = studyRoomDao.getRoomById(roomId)

    suspend fun setAvailability(roomId: Int, available: Boolean) {
        studyRoomDao.setRoomAvailability(roomId, available)
    }
}
