package com.mambo.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class User(
    val id: String,
    val username: String,
    val image: String,
    val dateOfBirth: Date,
    val bio:String,
    val gender: String
) : Parcelable