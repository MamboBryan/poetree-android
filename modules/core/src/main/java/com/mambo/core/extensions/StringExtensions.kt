package com.mambo.core.extensions

import java.text.SimpleDateFormat
import java.util.*

fun String?.isNotNull() = this != null

fun String?.isNotNullOrEmpty() = this != null && this.isNotEmpty()

fun String?.toDate(): Date? {
    if (isNullOrEmpty()) return null
    val dateFormat = SimpleDateFormat("dd/mm/yyyy", Locale.getDefault())
    return dateFormat.parse(this)
}