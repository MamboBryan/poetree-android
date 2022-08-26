package com.mambo.data.responses

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: TokenResponse,
    val user: UserDetails
)

@Serializable
data class TokenResponse(
    val accessToken: String,
    val refreshToken: String
)