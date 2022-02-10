package com.mambo.data.responses

data class Response<out T>(
    val success : Boolean,
    val message : String,
    val data: T
)