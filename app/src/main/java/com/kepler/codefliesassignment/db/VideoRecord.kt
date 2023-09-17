package com.kepler.codefliesassignment.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.io.Serializable

@Entity(tableName = "videos")
data class VideoRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val videoPath: String,
    val likesCount: Int,
    val timestamp: Long,
    @TypeConverters(CommentDataConverter::class)
    val comments: MutableList<CommentData>

) : Serializable


data class CommentData(
    var author: String,
    var content: String,
    var timestamp: Long
)

