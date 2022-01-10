package com.mambobryan.navigation.extensions

import androidx.fragment.app.Fragment
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController

fun Fragment.navigate(request: NavDeepLinkRequest){
    findNavController().navigate(request)
}