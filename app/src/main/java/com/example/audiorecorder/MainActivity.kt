@file:Suppress(
    "SpellCheckingInspection",
    "PrivatePropertyName",
    "SetTextI18n",
    "BooleanLiteralArgument"
)

package com.example.audiorecorder

import android.Manifest
import android.content.pm.PackageManager
import android.media.*
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import kotlin.concurrent.thread
import kotlin.experimental.and

private const val RECORDER_SAMPLERATE: Int = 44100
private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
private const val RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO
private const val RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_8BIT

class MainActivity : AppCompatActivity() {
    /*

    */
    private var fileName: String = ""
    private var bufferSizeInBytes: Int? = null
    private var isRecording: Boolean = false
    private val BufferElementsToRec: Int = 1024
    private val bufferByteArray: ByteArray = ByteArray(BufferElementsToRec)
    private val permissions: Array<String> =
        arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private lateinit var recordButton: Button
    private lateinit var file: File
    private lateinit var recordingThread: Thread
    private lateinit var audioRecorder: AudioRecord

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        fileName = "${externalCacheDir?.absolutePath}/audioRecordTest.pcm"
        recordButton = findViewById(R.id.activity_main_record)
        permissionHandler()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_RECORD_AUDIO_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED))
                    listenButton()
                return
            }
            else -> return
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun permissionHandler() {
        when (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)) {
            PackageManager.PERMISSION_GRANTED -> listenButton()
            PackageManager.PERMISSION_DENIED -> ActivityCompat.requestPermissions(
                this,
                permissions,
                REQUEST_RECORD_AUDIO_PERMISSION
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun listenButton() {
        recordButton.setOnClickListener {
            when (isRecording) {
                false -> startRecording()
                true -> stopRecording()
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun startRecording() {
        bufferSizeInBytes = AudioRecord.getMinBufferSize(
            RECORDER_SAMPLERATE,
            RECORDER_CHANNELS,
            RECORDER_AUDIO_ENCODING
        )
        Log.d("bufferSizeInBytes", bufferSizeInBytes.toString())
        audioRecorder = AudioRecord.Builder()
            .setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION)
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(RECORDER_AUDIO_ENCODING)
                    .setSampleRate(RECORDER_SAMPLERATE)
                    .setChannelMask(RECORDER_CHANNELS)
                    .build()
            )
            .setBufferSizeInBytes(2 * bufferSizeInBytes!!)
            .build()
        Log.d("Info", "audioRecorder Initialized")
        if (audioRecorder.state == AudioRecord.STATE_INITIALIZED) {
            Log.d("AudioRecorder", "Started")
            isRecording = true
            recordButton.text = "Stop"
            audioRecorder.startRecording()
            recordingThread = thread(true, false, null, "Reacording", 1) { read() }
        }
    }

    private fun stopRecording() {
        isRecording = false
        audioRecorder.stop()
        audioRecorder.release()
        recordButton.text = "Record"
        Log.d("AudioRecorder", "Stopped")
//        playRecording()
    }

    private fun read() {
        try {
            file = File(fileName)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        while (isRecording)
            when (val bytesRead = audioRecorder.read(bufferByteArray, 0, BufferElementsToRec)) {
                AudioRecord.ERROR -> Log.e("ERROR", "Other error")
                AudioRecord.ERROR_BAD_VALUE -> Log.e(
                    "ERROR_BAD_VALUE",
                    "Parameters don't resolve to valid data and indexes"
                )
                AudioRecord.ERROR_DEAD_OBJECT -> Log.e(
                    "ERROR_DEAD_OBJECT",
                    "Object is not valid anymore and needs to be recreated."
                )
                AudioRecord.ERROR_INVALID_OPERATION -> Log.e(
                    "ERROR_INVALID_OPERATION",
                    " The object isn't properly initializedr"
                )
                else -> writeAudioToFile(bytesRead)
            }
    }

    private fun writeAudioToFile(bytesRead: Int) {
        Log.d("Bytes Read", bytesRead.toString())
        file.appendBytes(bufferByteArray)
    }

    private fun shortToByte(sData: ShortArray): ByteArray {
        val shortArrsize = sData.size
        val bytes = ByteArray(shortArrsize * 2)
        for (i in 0 until shortArrsize) {
            bytes[i * 2] = (sData[i] and 0x00FF).toByte()
            bytes[i * 2 + 1] = ((sData[i] and 0x00FF)).toByte()
            sData[i] = 0
        }
        return bytes
    }

}