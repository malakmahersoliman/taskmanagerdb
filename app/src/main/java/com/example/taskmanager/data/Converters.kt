package com.example.taskmanager.data

import androidx.room.TypeConverter
import java.util.Date

class Converters {
    @TypeConverter
    fun listToString(list: List<String>?): String = list?.joinToString(",") ?: ""

    @TypeConverter
    fun stringToList(csv: String?): List<String> =
        csv?.takeIf { it.isNotBlank() }?.split(",")?.map { it.trim() } ?: emptyList()

    @TypeConverter
    fun dateToLong(date: Date?): Long? = date?.time

    @TypeConverter
    fun longToDate(value: Long?): Date? = value?.let { Date(it) }
}
