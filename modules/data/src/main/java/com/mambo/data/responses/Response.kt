package com.mambo.data.responses

import com.google.gson.annotations.SerializedName

data class Response<out T>(
    @SerializedName("success")
    val isSuccessful: Boolean,
    val message: String,
    val data: T
)