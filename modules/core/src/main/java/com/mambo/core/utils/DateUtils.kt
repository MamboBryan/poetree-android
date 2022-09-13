package com.mambo.core.utils

import org.ocpsoft.prettytime.PrettyTime
import java.text.SimpleDateFormat
import java.util.*

val timeFormat = SimpleDateFormat("hh:mm a").also { it.isLenient = false }
val dateFormat = SimpleDateFormat("dd-MM-yyyy").also { it.isLenient = false }
val dateTimeFormat = SimpleDateFormat("dd-MM-yyyy'T'HH:mm:ss.SSSZ").also { it.isLenient = false }

fun Long?.toDateLong(): String? {
    if (this == null) return null
    return dateFormat.format(Date(this))
}

fun Long?.toDateAndTime(): String? {
    if (this == null) return null
    return dateTimeFormat.format(Date(this))
}

fun String?.toDateLong(): Long? {
    if (this == null) return null
    return try {
        dateFormat.parse(this).time
    } catch (e: Exception) {
        null
    }
}

fun String?.toDate(): Date? {
    if (this.isNullOrBlank()) return null
    return try {
        dateFormat.parse(this)
    } catch (e: Exception) {
        null
    }
}

fun String?.toDateTime(): Long? {
    if (this == null) return null
    return dateTimeFormat.parse(this).time
}

fun Calendar?.toDate(): Date? {
    return this?.time
}

fun Date?.toDateString(): String? {
    if (this == null) return null
    return dateFormat.format(this)
}

fun Date?.toDateTimeString(): String? {
    if (this == null) return null
    return dateTimeFormat.format(this)
}

fun Date?.toDaysAgo(): String? {
    if (this == null) return null
    return PrettyTime().format(this)
}

fun Date.isValidAge(): Boolean {
    val now = Calendar.getInstance()
    val then = Calendar.getInstance()
    then.time = this
    val years = now[Calendar.YEAR] - then[Calendar.YEAR]
    return years >= 15
}

fun dateToCalendar(date: Date): Calendar {
    val cal = Calendar.getInstance()
    cal.time = date
    return cal
}

fun calendarToDate(calendar: Calendar): Date {
    return calendar.time
}

fun fromStringToTime(date: String): Calendar {
    val cal = Calendar.getInstance()
    cal.time = timeFormat.parse(date)!!
    return cal
}

fun fromDateToTimeString(calendar: Calendar): String {
    return timeFormat.format(calendar.timeInMillis)
}

fun fromDateToTimeString(date: Date): String {
    return timeFormat.format(date)
}

fun fromStringToDate(date: String): Calendar {
    val cal = Calendar.getInstance()
    cal.time = dateFormat.parse(date)!!
    return cal
}

fun fromDateToString(calendar: Calendar): String {
    return dateFormat.format(calendar.timeInMillis)
}

fun fromDateToString(date: Date): String {
    return dateFormat.format(date)
}
