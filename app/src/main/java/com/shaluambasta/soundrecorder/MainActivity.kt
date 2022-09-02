package com.shaluambasta.soundrecorder

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView

class MainActivity : AppCompatActivity() {


    private lateinit var imgBackground: ShapeableImageView
    private lateinit var btnStart: MaterialButton
    private lateinit var btnPause: MaterialButton
    private lateinit var btnPlay: MaterialButton


    private var mediaRecorder: MediaRecorder? = null
    private var recordState: Boolean = false
    private var fileName: String = ""
    private var pauseState: Boolean = false

    private var mediaPlayer: MediaPlayer? = null
    private var playState: Boolean = false

    private var count: Int = 1


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imgBackground = findViewById(R.id.img_background)
        btnStart = findViewById(R.id.btn_start)
        btnPause = findViewById(R.id.btn_pause)
        btnPlay = findViewById(R.id.btn_play)


        fileName = "${externalCacheDir?.absolutePath}/recording$count.mp3"
        count++


        btnStart.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                val permissions = arrayOf(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                ActivityCompat.requestPermissions(this, permissions, 0)
            } else {

                onRecord(!recordState)
            }

        }

        btnPause.setOnClickListener {
            pauseRecording()
        }


        btnPlay.setOnClickListener {
            onPlay(!playState)
        }


    }

    private fun onRecord(start: Boolean) = if (start) {
        startRecording()
    } else {
        stopRecording()
    }

    private fun startRecording() {

        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(fileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        }

        try {

            mediaRecorder!!.prepare()
            mediaRecorder!!.start()

            recordState = !recordState
            "Stop".also { btnStart.text = it }

            btnPause.visibility = View.VISIBLE
            btnPlay.visibility = View.GONE
            Toast.makeText(this, "Recording started!", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {

            e.printStackTrace()
            Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()

        }

    }

    private fun stopRecording() {

        if (recordState) {
            mediaRecorder?.apply {

                stop()
                release()
                recordState = !recordState
                "Start".also { btnStart.text = it }
                btnPause.visibility = View.GONE
                btnPlay.visibility = View.VISIBLE

            }
            Toast.makeText(this, "Recording Stopped!", Toast.LENGTH_SHORT).show()
            mediaRecorder = null
        }

    }

    private fun onPlay(start: Boolean) = if (start) {
        startPlaying()
    } else {
        stopPlaying()
    }


    private fun startPlaying() {

        try {

            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(fileName)
                prepare()
                start()

            }
        } catch (e: Exception) {

            e.printStackTrace()
            Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()
        }

    }


    private fun stopPlaying() {
        mediaPlayer?.release()
        mediaPlayer = null
    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun pauseRecording() {

        if (recordState) {
            if (!pauseState) {
                mediaRecorder?.pause()
                Toast.makeText(this, "Paused!", Toast.LENGTH_SHORT).show()
                pauseState = true
                "Resume".also { btnPause.text = it }
            } else {
                resumeRecording()
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun resumeRecording() {

        mediaRecorder?.resume()
        Toast.makeText(this, "Resumed!", Toast.LENGTH_SHORT).show()
        "Pause".also { btnPause.text = it }
        pauseState = false

    }

    override fun onStop() {
        super.onStop()
        mediaRecorder?.release()
        mediaRecorder = null

    }

}