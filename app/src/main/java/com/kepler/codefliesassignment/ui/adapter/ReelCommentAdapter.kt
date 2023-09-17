package com.kepler.codefliesassignment.ui.adapter


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.kepler.codefliesassignment.R
import com.kepler.codefliesassignment.db.CommentData
import com.kepler.codefliesassignment.utils.Utility


class ReelCommentAdapter(
    private var commentsList: List<CommentData>
) : RecyclerView.Adapter<ReelCommentAdapter.VideoViewHolder>() {


    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val timestamp: AppCompatTextView = itemView.findViewById(R.id.timestamp)
        val comments: AppCompatTextView = itemView.findViewById(R.id.editTextCommentview)

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_comment, parent, false)
        return VideoViewHolder(view)
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {

        holder.timestamp.setText(Utility.formatTimeAgo(commentsList[position].timestamp))
        holder.comments.setText(commentsList[position].content)

    }

    override fun getItemCount(): Int {
        return commentsList.size
    }



}

