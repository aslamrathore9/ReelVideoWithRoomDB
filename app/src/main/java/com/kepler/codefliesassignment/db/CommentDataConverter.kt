package com.kepler.codefliesassignment.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CommentDataConverter {
    @TypeConverter
    fun fromString(value: String): List<CommentData> {
        val listType = object : TypeToken<List<CommentData>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: List<CommentData>): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}
