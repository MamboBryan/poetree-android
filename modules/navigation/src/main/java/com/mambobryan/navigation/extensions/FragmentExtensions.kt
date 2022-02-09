package com.mambobryan.navigation.extensions

import android.content.Context
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController

fun Fragment.navigate(request: NavDeepLinkRequest){
    findNavController().navigate(request)
}

fun Fragment.navigate(request: NavDeepLinkRequest, options: NavOptions){
    findNavController().navigate(request, options)
}

fun Fragment.getDeeplink(id: Int): NavDeepLinkRequest {

    val uri = requireContext().get(id)

    return NavDeepLinkRequest.Builder
        .fromUri(uri.toUri())
        .build()
}

fun Fragment.getLastDestination() = findNavController().backStack.last.destination.id

fun Context.get(id: Int): String {
    return this.getString(id)
}