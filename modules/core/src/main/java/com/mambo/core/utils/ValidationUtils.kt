package com.mambo.core.utils

import android.text.TextUtils
import java.util.regex.Pattern

 val PASSWORD_PATTERN: Pattern = Pattern.compile("[a-zA-Z0-9\\!\\@\\#\\$]{8,24}")


fun String.isEmailValid(): Boolean {
    return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isValidPassword(): Boolean {
    return !TextUtils.isEmpty(this) && PASSWORD_PATTERN.matcher(this).matches()
}