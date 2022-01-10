package com.mambobryan.navigation

import android.content.Context
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.NavDeepLinkRequest

public fun Fragment.getDeeplink(id: Int): NavDeepLinkRequest {

    val uri = requireContext().get(id)

    return NavDeepLinkRequest.Builder
        .fromUri(uri.toUri())
        .build()
}

fun Context.get(id: Int): String {
    return this.getString(id)
}

object Destinations {

    val PROFILE = R.string.profile
    val COMPOSE = R.string.compose
    val POEM = R.string.poem

}