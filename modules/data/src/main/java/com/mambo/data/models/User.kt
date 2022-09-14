package com.mambo.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class User(
    var id: String? = null,
    var createdAt: String? = null,
    var updatedAt: String? = null,
    var name: String? = null,
    var image: String? = null,
    var email: String? = null,
    var bio: String? = null,
    var dateOfBirth: String? = null,
    var gender: Int? = null
) : Parcelable

@Serializable
@Parcelize
data class MinimalUser(
    val id: String,
    val createdAt: String,
    val name: String,
    val image: String?,
) : Parcelable