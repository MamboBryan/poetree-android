package com.mambobryan.navigation.extensions

import android.app.Activity
import androidx.core.net.toUri
import androidx.navigation.NavDeepLinkRequest

fun Activity.getDeeplink(id: Int): NavDeepLinkRequest {

    val uri = this.get(id)

    return NavDeepLinkRequest.Builder
        .fromUri(uri.toUri())
        .build()
}