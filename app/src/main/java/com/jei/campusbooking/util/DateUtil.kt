package com.jei.campusbooking.util

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Date/time <-> TEXT helpers (the database stores dates as "yyyy-MM-dd" and times as "HH:mm"). */
object DateUtil {
    private val timeFormat = DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH)
    private val prettyDate = DateTimeFormatter.ofPattern("yyyy-MM-dd (EEE)", Locale.ENGLISH)

    fun dateToDb(date: LocalDate): String = date.toString() // ISO yyyy-MM-dd

    fun timeToDb(time: LocalTime): String = time.format(timeFormat)

    fun prettyDate(date: LocalDate): String = date.format(prettyDate)

    fun parseDateOrNull(text: String?): LocalDate? =
        try {
            if (text.isNullOrBlank()) null else LocalDate.parse(text)
        } catch (e: Exception) {
            null
        }
}
