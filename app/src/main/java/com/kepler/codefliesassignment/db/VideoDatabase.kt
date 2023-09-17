package com.kepler.codefliesassignment.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [VideoRecord::class], version = 1, exportSchema = false)
@TypeConverters(CommentDataConverter::class)
abstract class VideoDatabase : RoomDatabase() {
    abstract fun videoDao(): VideoRecordDao

    companion object {
        private const val DATABASE_NAME = "app_database.db"

        @Volatile
        private var instance: VideoDatabase? = null

        fun getInstance(context: Context): VideoDatabase {
            return instance ?: synchronized(this) {
                val newInstance = Room.databaseBuilder(
                    context.applicationContext,
                    VideoDatabase::class.java,
                    DATABASE_NAME
                ).build()
                instance = newInstance
                newInstance
            }
        }
    }
}
