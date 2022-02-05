package com.mambo.core.extensions

fun String?.isNotNull() = this != null

fun String?.isNotNullOrEmpty() = this != null && this.isNotEmpty()