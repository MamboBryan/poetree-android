package com.mambo.data.responses.auth

import com.google.gson.annotations.SerializedName

data class SignUpData(
    val message: String,
    @SerializedName("success")
    val isSuccessful: Boolean,
    val token: String
) {
}