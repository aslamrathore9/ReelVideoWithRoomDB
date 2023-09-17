package com.kepler.codefliesassignment.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.VideoView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.kepler.codefliesassignment.R
import com.kepler.codefliesassignment.db.VideoRecord

class ReelVIdeoAdapter(
    private var videoUrls: List<VideoRecord>,
    var onItemClickListener: OnClickCallBack
) :
    RecyclerView.Adapter<ReelVIdeoAdapter.VideoViewHolder>() {

    interface OnClickCallBack {
        fun onLikePost(id: Long, count: Long)
        fun onCommentPost(videoUrls: VideoRecord)
        fun onSharePost(id: String)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val likeTitle: AppCompatTextView = itemView.findViewById(R.id.image_view_option_like_title)
        val commentTitle: AppCompatTextView =
            itemView.findViewById(R.id.image_view_option_comment_title)
        val commentBtn: AppCompatImageView = itemView.findViewById(R.id.image_view_option_comment)
        val likeBtn: AppCompatImageView = itemView.findViewById(R.id.image_view_option_like)
        val shareBtn: AppCompatImageView = itemView.findViewById(R.id.image_view_option_share)
        val mVideoView: VideoView = itemView.findViewById(R.id.playerview11)


        init {

            mVideoView.setOnCompletionListener { mp ->
                mp.start()
            }

            mVideoView.setOnClickListener {
                if (mVideoView.isPlaying) {
                    mVideoView.pause()
                } else {
                    mVideoView.start()
                }
            }

        }


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.reel_video_item, parent, false)
        return VideoViewHolder(view)
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {


        if (videoUrls[position].likesCount == 1)
            holder.likeBtn.setImageResource(R.drawable.ic_heart_icon_red)
        else
            holder.likeBtn.setImageResource(R.drawable.ic_heart_icon)

        holder.likeTitle.text = videoUrls[position].likesCount.toString() + " Likes"
        holder.commentTitle.text = videoUrls[position].comments.size.toString() + " Comments"


        holder.mVideoView.stopPlayback() // Stop previous playback
        holder.mVideoView.setVideoPath(videoUrls[position].videoPath)
        holder.mVideoView.setOnPreparedListener { mp ->
            mp.isLooping = true // Loop the video
            mp.start()
            val videoRatio = mp.videoWidth.toFloat() / mp.videoHeight.toFloat()
            val screenRatio = holder.mVideoView.width.toFloat() / holder.mVideoView.height.toFloat()
            val scale = videoRatio / screenRatio
            if (scale >= 1f) {
                holder.mVideoView.scaleX = scale
            } else {
                holder.mVideoView.scaleY = 1f / scale
            }

        }



        holder.likeBtn.setOnClickListener {
            if (videoUrls[position].likesCount == 0)
                onItemClickListener.onLikePost(videoUrls[position].id, 1)
            else onItemClickListener.onLikePost(videoUrls[position].id, 0)

        }
        holder.commentBtn.setOnClickListener {
            onItemClickListener.onCommentPost(videoUrls[position])
        }//

        holder.shareBtn.setOnClickListener {
            onItemClickListener.onSharePost(videoUrls[position].videoPath.toString())
        }


    }

    override fun getItemCount(): Int {
        return videoUrls.size
    }


    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newData: List<VideoRecord>) {
        videoUrls = newData
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return videoUrls[position].id
    }


}

