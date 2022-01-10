package com.mambobryan.navigation.extensions

import android.content.Context
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController

fun Fragment.navigate(request: NavDeepLinkRequest){
    findNavController().navigate(request)
}

fun Fragment.getDeeplink(id: Int): NavDeepLinkRequest {

    val uri = requireContext().get(id)

    return NavDeepLinkRequest.Builder
        .fromUri(uri.toUri())
        .build()
}

fun Context.get(id: Int): String {
    return this.getString(id)
}