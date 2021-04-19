package com.mambo.poetree.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.mambo.poetree.data.model.Emotion
import com.mambo.poetree.data.model.Topic
import com.mambo.poetree.data.model.User
import java.util.*

class Converters {

    @TypeConverter
    fun fromUser(user: User): String {
        return Gson().toJson(user)
    }

    @TypeConverter
    fun toUser(string: String): User {
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
    fun fromEmotion(emotion: Emotion?): String {
        return Gson().toJson(emotion)
    }

    @TypeConverter
    fun toEmotion(string: String): Emotion? {
        return Gson().fromJson(string, Emotion::class.java)
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