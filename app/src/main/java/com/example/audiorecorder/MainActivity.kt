package com.example.audiorecorder

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import kotlin.experimental.and

private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
private const val RECORDER_SAMPLERATE: Int = 44100
private const val RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO
private const val RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT

class MainActivity : AppCompatActivity() {

    private lateinit var file: FileOutputStream
    private var fileName: String = " "
    private var bufferSize: Int? = null
    private lateinit var recordButton: Button
    private var isRecording: Boolean = false
    private val permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private lateinit var audioRecorder: AudioRecord
    private var BufferElements2Rec = 1024*1024
    private var BytesPerElement = 2 // 2 bytes in 16bit format
    private lateinit var bufferByteArray:ByteArray



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fileName = "${externalCacheDir?.absolutePath}/audioRecordTest.pcm"
        bufferByteArray= ByteArray(BufferElements2Rec)
        bufferSize = AudioRecord.getMinBufferSize(
                RECORDER_SAMPLERATE,
                RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING
        )
        audioRecorder = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLERATE, RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING, bufferSize!!
        )
        recordButton = findViewById(R.id.activity_main_record)
        recordButton.setOnClickListener {
            permissionHandler()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_RECORD_AUDIO_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED))
                    listenButton()
                return
            }
            else -> return
        }
    }

    private fun permissionHandler() {
        when (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)) {
            PackageManager.PERMISSION_GRANTED -> listenButton()
            PackageManager.PERMISSION_DENIED -> {
                ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION)
            }
        }
    }

    private fun listenButton() {
        when (isRecording) {
            false -> startRecording()
            true -> stopRecording()
        }

    }

    private fun stopRecording() {
        isRecording = false
        audioRecorder!!.stop()
        writeAudioToFile()
    }

    private fun writeAudioToFile() {
        audioRecorder.read(bufferByteArray,0,BytesPerElement)
        try {
            file = FileOutputStream(fileName)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        file?.write(bufferByteArray)
        try {
            file?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun startRecording() {
        isRecording = true
        audioRecorder!!.startRecording()
    }



}