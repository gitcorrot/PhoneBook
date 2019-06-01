package com.example.phonebook.db

import android.net.Uri
import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromString(value: String?): Uri? {
        return if (value.isNullOrBlank()) null else Uri.parse(value)
    }

    @TypeConverter
    fun toString(value: Uri?): String? {
        return if (value == null) null else value.toString()
    }
}