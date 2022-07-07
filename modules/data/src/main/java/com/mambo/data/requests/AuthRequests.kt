package com.mambo.data.requests

import com.google.gson.annotations.SerializedName

data class SignInRequest(
    val email: String,
    val password: String
)

data class SignUpRequest(
    val email: String,
    val password: String,
    @SerializedName("password_confirmation")
    val confirmPassword: String
)

