package com.mambo.data.requests

data class SetupRequest(
    val username: String,
    val dateOfBirth: String,
    val gender: String,
    val bio: String
)