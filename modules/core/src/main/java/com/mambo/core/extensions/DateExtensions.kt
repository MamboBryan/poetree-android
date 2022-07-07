package com.mambo.core.extensions

import java.text.SimpleDateFormat
import java.util.*

/**
 * Poetree
 * @author Mambo Bryan
 * @email mambobryan@gmail.com
 * Created 4/18/22 at 9:03 AM
 */

fun Date?.getString(): String{
    if (this == null) return ""
    val dateFormat = SimpleDateFormat("dd/mm/yyyy", Locale.getDefault())
    return dateFormat.format(this)
}