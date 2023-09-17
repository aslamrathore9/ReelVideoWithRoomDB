package com.kepler.codefliesassignment.ui

import android.Manifest
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.common.util.concurrent.ListenableFuture
import com.kepler.codefliesassignment.R
import com.kepler.codefliesassignment.databinding.ActivityReelRecorderBinding
import com.kepler.codefliesassignment.db.CommentData
import com.kepler.codefliesassignment.db.VideoRecord
import com.kepler.codefliesassignment.db.VideoViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ExecutionException

class ReelRecorderActivity : AppCompatActivity() {

    private var recordingTimer: CountDownTimer? = null
    private val maxRecordingTimeMillis = 15 * 1000L // 15 seconds in milliseconds
    val mutableEmptyList = mutableListOf<CommentData>()


    private lateinit var binding: ActivityReelRecorderBinding
    var videoCapture: VideoCapture<Recorder>? = null
    var recording: Recording? = null
    var cameraFacing = CameraSelector.LENS_FACING_BACK

    private lateinit var outputFilePath: String

    private lateinit var videoViewModel: VideoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReelRecorderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        videoViewModel = ViewModelProvider(this).get(VideoViewModel::class.java)

        outputFilePath = getOutputFilePath()

        binding.capture.setOnClickListener {
            if (allPermissionsGranted()) {
                startRecording()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO
                    ),
                    REQUEST_CODE_PERMISSIONS
                )
            }
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            activityResultLauncher.launch(Manifest.permission.CAMERA)
        } else {
            startCamera(cameraFacing)
        }

        binding.flipCamera.setOnClickListener {
            cameraFacing = if (cameraFacing == CameraSelector.LENS_FACING_BACK) {
                CameraSelector.LENS_FACING_FRONT
            } else {
                CameraSelector.LENS_FACING_BACK
            }
            startCamera(cameraFacing)
        }


    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun startRecording() {
        binding.capture.setImageResource(R.drawable.round_stop_circle_24)
        val recording1 = recording
        if (recording1 != null) {
            recording1.stop()
            recording = null
            return
        }

        val name = SimpleDateFormat(
            "yyyy-MM-dd-HH-mm-ss-SSS",
            Locale.getDefault()
        ).format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/CameraReel-Video")
        }

        val options = MediaStoreOutputOptions.Builder(
            contentResolver,
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        )
            .setContentValues(contentValues)
            .build()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }


        recording = videoCapture!!.output.prepareRecording(this, options)
            .withAudioEnabled()
            .start(ContextCompat.getMainExecutor(this)) { videoRecordEvent ->
                when (videoRecordEvent) {
                    is VideoRecordEvent.Start -> {
                        binding.capture.isEnabled = true
                    }

                    is VideoRecordEvent.Finalize -> {
                        if (!videoRecordEvent.hasError()) {
                            //  val msg = "Video capture succeeded: ${videoRecordEvent.outputResults.outputUri}"
                            // Video recording is complete, you can handle the saved video file here
                            val filePath =
                                getRealPathFromUri(this, videoRecordEvent.outputResults.outputUri)!!
                            saveVideoToDatabase(filePath)
                         //   Toast.makeText(this, filePath, Toast.LENGTH_SHORT).show()
                        } else {
                            recording?.close()
                            recording = null
                            val msg = "Error: ${videoRecordEvent.error}"
                            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                        }
                        binding.capture.setImageResource(R.drawable.round_fiber_manual_record_24)
                    }
                }
            }

        // Set 15 second timer
        recordingTimer = object : CountDownTimer(maxRecordingTimeMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                val minutes = secondsRemaining / 60
                val seconds = secondsRemaining % 60
                val timerText = String.format("%02d:%02d", minutes, seconds)
                binding.timerTextView.text = timerText
            }

            override fun onFinish() {
                // Stop recording when the timer finishes
                recording?.close()
                recording = null
                recordingTimer?.cancel()
                binding.timerTextView.text = "00:00"

                return
            }
        }.start()



    }


    private fun saveVideoToDatabase(videoFilePath: String) {
        val currentTimeMillis: Long = System.currentTimeMillis()

        val videoRecord = VideoRecord( videoPath = videoFilePath,likesCount =0, timestamp = currentTimeMillis, comments = mutableEmptyList)
        videoViewModel.insertVideo(videoRecord)
        Toast.makeText(this, "Success was recorded on the reel..", Toast.LENGTH_SHORT).show()
        onBackPressed()
    }

    private fun getOutputFilePath(): String {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "VIDEO_${timeStamp}.mp4"
        return File(getExternalFilesDir(null), fileName).absolutePath
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO
        )
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    fun startCamera(cameraFacing: Int) {
        val processCameraProvider: ListenableFuture<ProcessCameraProvider> =
            ProcessCameraProvider.getInstance(this)

        processCameraProvider.addListener({
            try {
                val cameraProvider: ProcessCameraProvider = processCameraProvider.get()
                val preview = Preview.Builder().build()
                preview.setSurfaceProvider(binding.viewFinder.surfaceProvider)

                val recorder = Recorder.Builder()
                    .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
                    .build()
                videoCapture = VideoCapture.withOutput(recorder)

                cameraProvider.unbindAll()

                val cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(cameraFacing)
                    .build()

                val camera =
                    cameraProvider.bindToLifecycle(this, cameraSelector, preview, videoCapture)

                binding.toggleFlash.setOnClickListener { toggleFlash(camera) }
            } catch (e: ExecutionException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))
    }


    private fun toggleFlash(camera: Camera) {
        if (camera.cameraInfo.hasFlashUnit()) {
            if (camera.cameraInfo.torchState.value == 0) {
                camera.cameraControl.enableTorch(true)
                binding.toggleFlash.setImageResource(R.drawable.round_flash_off_24)
            } else {
                camera.cameraControl.enableTorch(false)
                binding.toggleFlash.setImageResource(R.drawable.round_flash_on_24)
            }
        } else {
            runOnUiThread {
                Toast.makeText(this, "Flash is not available currently", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private val activityResultLauncher: ActivityResultLauncher<String> = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { result ->
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera(cameraFacing)
        }
    }

    fun getRealPathFromUri(context: Context, uri: Uri): String? {
        var filePath: String? = null
        val scheme = uri.scheme
        if (scheme == ContentResolver.SCHEME_FILE) {
            filePath = uri.path
        } else if (scheme == ContentResolver.SCHEME_CONTENT) {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    filePath = it.getString(columnIndex)
                }
            }
        }
        return filePath
    }

}
