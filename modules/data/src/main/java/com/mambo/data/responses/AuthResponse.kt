package com.mambo.data.responses

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: TokeResponse,
    val user: UserDetails
)

@Serializable
data class TokeResponse(
    val accessToken: String,
    val refreshToken: String
)