package com.example.audiorecorder

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import kotlin.experimental.and

private const val REQUEST_RECORD_AUDIO_PERMISSION = 200

class _Activity : AppCompatActivity() {

    private lateinit var recordButton: Button
    private var fileName: String = " "
    private lateinit var responseText: TextView
    private var isRecording = false
    private final var recordingThread: Thread? = null
    var BufferElements2Rec = 1024
    var BytesPerElement = 2 // 2 bytes in 16bit format


    private val recorder: AudioRecord
    private val RECORDER_SAMPLERATE = 44100
    private val RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO
    private val RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT
    private val bufferSize = AudioRecord.getMinBufferSize(
        RECORDER_SAMPLERATE,
        RECORDER_CHANNELS,
        RECORDER_AUDIO_ENCODING
    );

    init {
        recorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            RECORDER_SAMPLERATE, RECORDER_CHANNELS,
            RECORDER_AUDIO_ENCODING, bufferSize
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initiateViewsById()
        fileName = "${externalCacheDir?.absolutePath}"
        recordButton.setOnClickListener {
            recordButtonProcess()
        }

    }

    private fun recordButtonProcess() {
        if (!isRecording) {
            recordButton.text = "Recording"
            isRecording = true
            startRecording()
        } else {
            recordButton.text = "Stop"
            isRecording = false
            stopRecording()
        }
    }

    private fun startRecording() {
        if (recorder.state == AudioRecord.STATE_INITIALIZED) {
            recorder.startRecording()
            recordingThread = Thread(Runnable { writeAudioDataToFile() }, "AudioRecorder Thread")
        }
    }

    private fun initiateViewsById() {
        recordButton = findViewById(R.id.activity_main_record)
    }

    private fun stopRecording() {
        recorder.stop()
        recorder.release()
    }

    private fun writeAudioDataToFile() {
        // Write the output audio in byte
        val filePath = "${externalCacheDir?.absolutePath}"
        val sData = ShortArray(BufferElements2Rec)
        var os: FileOutputStream? = null
        try {
            os = FileOutputStream(filePath)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        while (isRecording) {
            // gets the voice output from microphone to byte format
            recorder.read(sData, 0, BufferElements2Rec)
            println("Short writing to file$sData")
            try {
                // // writes the data to file from buffer
                // // stores the voice buffer
                val bData: ByteArray = short2byte(sData)
                os?.write(bData, 0, BufferElements2Rec * BytesPerElement)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        try {
            os?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun short2byte(sData: ShortArray): ByteArray {
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