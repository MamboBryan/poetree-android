package com.mambo.core.utils

import java.text.SimpleDateFormat
import java.util.*

private fun getTimeFormat(): SimpleDateFormat {
    return SimpleDateFormat("hh:mm a", Locale.getDefault())
}

private fun getDateFormat(): SimpleDateFormat {
    return SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault())
}

private fun getDateTimeFormat(): SimpleDateFormat {
    return SimpleDateFormat("yyMMddHHmmssZ", Locale.getDefault())
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
    cal.time = getTimeFormat().parse(date)!!
    return cal
}

fun fromDateToTimeString(calendar: Calendar): String {
    return getTimeFormat().format(calendar.timeInMillis)
}

fun fromDateToTimeString(date: Date): String {
    return getTimeFormat().format(date)
}

fun fromStringToDate(date: String): Calendar {
    val cal = Calendar.getInstance()
    cal.time = getDateFormat().parse(date)!!
    return cal
}

fun fromDateToString(calendar: Calendar): String {
    return getDateFormat().format(calendar.timeInMillis)
}

fun fromDateToString(date: Date): String {
    return getDateFormat().format(date)
}
