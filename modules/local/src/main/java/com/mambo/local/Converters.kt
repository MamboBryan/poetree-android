package com.mambo.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.mambo.data.models.Topic
import com.mambo.data.models.User
import java.util.*

class Converters {

    @TypeConverter
    fun fromUser(user: User?): String {
        return Gson().toJson(user)
    }

    @TypeConverter
    fun toUser(string: String): User? {
        return Gson().fromJson(string, User::class.java)
    }

    @TypeConverter
    fun fromDate(date: Date): String {
        return Gson().toJson(date)
    }

    @TypeConverter
    fun toDate(string: String): Date {
        return Gson().fromJson(string, Date::class.java)
    }

    @TypeConverter
    fun fromTopic(topic: Topic?): String {
        return Gson().toJson(topic)
    }

    @TypeConverter
    fun toTopic(string: String): Topic? {
        return Gson().fromJson(string, Topic::class.java)
    }


}