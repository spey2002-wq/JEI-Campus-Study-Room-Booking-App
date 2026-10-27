package com.jei.campusbooking.util

import android.content.Context

/**
 * Very small "session": remembers which user is using the app.
 * For the midterm/demo there is no login screen yet, so the seeded demo
 * student (user_id = 1, RAI SPANDAN) is used by default.
 */
class SessionManager(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences("campus_booking_session", Context.MODE_PRIVATE)

    var currentUserId: Int
        get() = prefs.getInt(KEY_USER_ID, DEFAULT_USER_ID)
        set(value) = prefs.edit().putInt(KEY_USER_ID, value).apply()

    companion object {
        private const val KEY_USER_ID = "user_id"
        const val DEFAULT_USER_ID = 1
    }
}
