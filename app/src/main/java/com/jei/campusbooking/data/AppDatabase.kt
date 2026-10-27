package com.jei.campusbooking.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.jei.campusbooking.data.dao.ReservationDao
import com.jei.campusbooking.data.dao.StudyRoomDao
import com.jei.campusbooking.data.dao.UserDao
import com.jei.campusbooking.data.entity.Reservation
import com.jei.campusbooking.data.entity.StudyRoom
import com.jei.campusbooking.data.entity.User
import com.jei.campusbooking.util.PasswordUtil

@Database(
    entities = [User::class, StudyRoom::class, Reservation::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun studyRoomDao(): StudyRoomDao
    abstract fun reservationDao(): ReservationDao

    companion object {
        const val DATABASE_NAME = "campus_booking_database"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .addCallback(SeedCallback)
                    .fallbackToDestructiveMigration(dropAllTables = true) // OK for a course project
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    /** Pre-loads a demo user, an admin and sample study rooms the first time the DB is created. */
    private object SeedCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            val hash = PasswordUtil.sha256("demo1234")

            db.execSQL(
                "INSERT INTO users (student_number, full_name, email, password_hash, department, role, created_at) VALUES " +
                    "('2025621055', 'RAI SPANDAN', 'spandan@jei.ac.kr', '$hash', 'Computer Software', 'STUDENT', '2026-10-26 09:00:00'), " +
                    "('20231045', 'Lee Soyeon', 'soyeon@jei.ac.kr', '$hash', 'Computer Software', 'STUDENT', '2026-10-26 09:05:00'), " +
                    "('99000001', 'Admin Library', 'admin@jei.ac.kr', '$hash', 'Library Office', 'ADMIN', '2026-10-26 08:00:00')"
            )

            db.execSQL(
                "INSERT INTO study_rooms (room_name, building, floor, capacity, equipment, is_available) VALUES " +
                    "('Study Room A-101', 'Main Library', 1, 4, 'Whiteboard,Power Outlet', 1), " +
                    "('Study Room A-202', 'Main Library', 2, 8, 'Projector,Whiteboard,Wi-Fi', 1), " +
                    "('Group Lab B-305', 'Engineering Hall', 3, 12, 'Monitor,Whiteboard,Wi-Fi', 1), " +
                    "('Discussion Room B-110', 'Engineering Hall', 1, 6, 'Whiteboard,Monitor', 1), " +
                    "('Seminar Room D-401', 'Humanities Hall', 4, 20, 'Projector,Microphone,Wi-Fi', 1), " +
                    "('Quiet Room C-001', 'Student Center', 0, 2, 'Power Outlet', 0)"
            )
        }
    }
}
