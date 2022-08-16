package com.mambo.data.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ServerResponse<T>(
    @SerialName("success")
    val isSuccessful: Boolean,
    val message: String,
    val data: T? = null
)