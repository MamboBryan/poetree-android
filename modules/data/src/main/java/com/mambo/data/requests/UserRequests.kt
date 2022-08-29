package com.mambo.data.requests

import kotlinx.serialization.Serializable

@Serializable
data class SetupRequest(
    val username: String,
    val dateOfBirth: String,
    val gender: Int,
    val bio: String
)

@Serializable
data class UserUpdateRequest(
    val username: String?,
    val email: String?,
    val dateOfBirth: String?,
    val gender: Int?,
    val bio: String?
)


@Serializable
data class UpdatePasswordRequest(
    val oldPassword: String,
    val newPassword: String,
)