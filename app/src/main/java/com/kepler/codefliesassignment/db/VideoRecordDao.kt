package com.kepler.codefliesassignment.db


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface VideoRecordDao {

    @Insert
    suspend fun insert(videoRecord: VideoRecord)

    @Query("SELECT * FROM videos ORDER BY timestamp Desc")
    fun getAllVideos(): LiveData<List<VideoRecord>>

    @Query("SELECT * FROM videos WHERE id = :selectedPosition")
    fun getVideoByPosition(selectedPosition: Long): LiveData<VideoRecord>

    @Query("UPDATE videos SET likesCount = :newQuantity WHERE id = :videoId")
    fun updateVideoQuantity(videoId: Long, newQuantity: Long)

    @Query("UPDATE videos SET comments = :updatedComments WHERE id = :videoId")
    fun updateCommentsById(videoId: Long, updatedComments: MutableList<CommentData>)

}