package com.mambobryan.navigation.extensions

import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions

fun getNavOptionsPopUpTo(id: Int) = NavOptions.Builder().setPopUpTo(id, true).build()

fun Fragment.getNavOptionsPopUpToCurrent() =
    NavOptions.Builder().setPopUpTo(getLastDestination(), true).build()