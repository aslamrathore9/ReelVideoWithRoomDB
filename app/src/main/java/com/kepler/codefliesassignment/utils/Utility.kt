package com.kepler.codefliesassignment.utils

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File
import java.util.concurrent.TimeUnit

object Utility {


    fun formatTimeAgo(timestampMillis: Long): String {
        val currentTimeMillis = System.currentTimeMillis()
        val timeDifferenceMillis = currentTimeMillis - timestampMillis

        val days = TimeUnit.MILLISECONDS.toDays(timeDifferenceMillis)
        val hours = TimeUnit.MILLISECONDS.toHours(timeDifferenceMillis) % 24
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeDifferenceMillis) % 60

        return when {
            days > 0 -> "$days day${if (days > 1) "s" else ""} ago"
            hours > 0 -> "$hours hour${if (hours > 1) "s" else ""} ago"
            else -> "$minutes minute${if (minutes > 1) "s" else ""} ago"
        }
    }


    fun shareVideoSrc(context:Context,path:String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "video/*"
        val contentUri = FileProvider.getUriForFile(
            context,
            "com.kepler.codefliesassignment.provider",
            File(path)
        )
        intent.putExtra(Intent.EXTRA_STREAM, contentUri)
        context.startActivity(Intent.createChooser(intent, "Share Video"))
    }


}