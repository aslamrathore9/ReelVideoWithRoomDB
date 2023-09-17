package com.kepler.codefliesassignment.db

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VideoViewModel(application: Application) : AndroidViewModel(application) {
    private val videoDao = VideoDatabase.getInstance(application).videoDao()

    fun getAllVideos(): LiveData<List<VideoRecord>> {
        return videoDao.getAllVideos()
    }


    fun getVideoByIDVideos(videoID: Int): LiveData<VideoRecord> {
        return videoDao.getVideoByPosition(videoID.toLong())
    }


    fun updateLikeQuantity(id: Long, likeCount: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            videoDao.updateVideoQuantity(id, likeCount)
        }
    }


    fun updateCommentsById(id: Long, updatedComments: MutableList<CommentData>) {
        viewModelScope.launch(Dispatchers.IO) {
            videoDao.updateCommentsById(id, updatedComments)
        }
    }

    fun insertVideo(video: VideoRecord) {
        viewModelScope.launch(Dispatchers.IO) {
            videoDao.insert(video)
        }
    }


}
