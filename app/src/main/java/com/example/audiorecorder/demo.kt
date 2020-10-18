package com.example.audiorecorder
//
//import android.media.AudioFormat
//import android.media.AudioRecord
//import android.media.MediaRecorder
//import android.os.Bundle
//import android.widget.Button
//import android.widget.TextView
//import androidx.appcompat.app.AppCompatActivity
//import java.io.FileNotFoundException
//import java.io.FileOutputStream
//import java.io.IOException
//import kotlin.experimental.and
//import android.os.Environment
//import java.io.BufferedOutputStream
//import java.io.File
//import java.io.FileInputStream
//private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
//
//class _Activity : AppCompatActivity() {
//
//    private lateinit var recordButton: Button
//    private var fileName: String = " "
//    private lateinit var responseText: TextView
//    private var isRecording = false
//    private final var recordingThread: Thread? = null
//    var BufferElements2Rec = 1024
//    var BytesPerElement = 2 // 2 bytes in 16bit format
//
//
//    private val recorder: AudioRecord
//    private val RECORDER_SAMPLERATE = 44100
//    private val RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO
//    private val RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT
//    private val bufferSize = AudioRecord.getMinBufferSize(
//        RECORDER_SAMPLERATE,
//        RECORDER_CHANNELS,
//        RECORDER_AUDIO_ENCODING
//    );
//
//    init {
//        recorder = AudioRecord(
//            MediaRecorder.AudioSource.MIC,
//            RECORDER_SAMPLERATE, RECORDER_CHANNELS,
//            RECORDER_AUDIO_ENCODING, bufferSize
//        )
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//        initiateViewsById()
//        fileName = "${externalCacheDir?.absolutePath}"
//        recordButton.setOnClickListener {
//            recordButtonProcess()
//        }
//
//    }
//
//    private fun recordButtonProcess() {
//        if (!isRecording) {
//            recordButton.text = "Recording"
//            isRecording = true
//            startRecording()
//        } else {
//            recordButton.text = "Stop"
//            isRecording = false
//            stopRecording()
//        }
//    }
//
//    private fun startRecording() {
//        if (recorder.state == AudioRecord.STATE_INITIALIZED) {
//            recorder.startRecording()
//            recordingThread = Thread(Runnable { writeAudioDataToFile() }, "AudioRecorder Thread")
//        }
//    }
//
//    private fun initiateViewsById() {
//        recordButton = findViewById(R.id.activity_main_record)
//    }
//
//    private fun stopRecording() {
//        recorder.stop()
//        recorder.release()
//    }
//
//    private fun writeAudioDataToFile() {
//        // Write the output audio in byte
//        val filePath = "${externalCacheDir?.absolutePath}"
//        val sData = ShortArray(BufferElements2Rec)
//        var os: FileOutputStream? = null
//        try {
//            os = FileOutputStream(filePath)
//        } catch (e: FileNotFoundException) {
//            e.printStackTrace()
//        }
//        while (isRecording) {
//            // gets the voice output from microphone to byte format
//            recorder.read(sData, 0, BufferElements2Rec)
//            println("Short writing to file$sData")
//            try {
//                // // writes the data to file from buffer
//                // // stores the voice buffer
//                val bData: ByteArray = short2byte(sData)
//                os?.write(bData, 0, BufferElements2Rec * BytesPerElement)
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//        }
//        try {
//            os?.close()
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//    }
//
//    private fun short2byte(sData: ShortArray): ByteArray {
//        val shortArrsize = sData.size
//        val bytes = ByteArray(shortArrsize * 2)
//
//        for (i in 0 until shortArrsize) {
//            bytes[i * 2] = (sData[i] and 0x00FF).toByte()
//            bytes[i * 2 + 1] = ((sData[i] and 0x00FF)).toByte()
//            sData[i] = 0
//        }
//        return bytes
//    }
//}
//
//
//
//
///**
// *@author :
// *@date : 2020/1/17
// *@description :Audio only class
// */
//class OnlyAudioRecorder private constructor(){
//    //1. Set recording related parameters, audio acquisition source, sampling rate, channel and data format
//    //2. Calculate the minimum recording buffer size
//    //3. Create audioRecord object
//    //4. Start recording
//    //5. Create files to save PCM files
//    //6. Close recording and release related resources after recording
//    //7. Convert pcm file to WAV file
//
//    companion object{
//        private const val TAG:String = "OnlyAudioRecorder"
//        private const val AudioSource = MediaRecorder.AudioSource.MIC//Student source
//        private const val SampleRate = 16000//sampling rate
//        private const val Channel = AudioFormat.CHANNEL_IN_MONO//Mono channel
//        private const val EncodingType = AudioFormat.ENCODING_PCM_16BIT//data format
//        private val PCMPath = Environment.getExternalStorageDirectory().path.toString()+"/zzz/RawAudio.pcm"
//        private val WAVPath = Environment.getExternalStorageDirectory().path.toString()+"/zzz/FinalAudio.wav"
//        //Single example of double check
//        val instance:OnlyAudioRecorder by lazy (mode = LazyThreadSafetyMode.SYNCHRONIZED){
//            OnlyAudioRecorder()
//        }
//    }
//
//    private var bufferSizeInByte:Int = 0//Minimum recording buffer
//    private var audioRecorder:AudioRecord? = null//Recording object
//    private var isRecord = false
//
//    private fun initRecorder() {//Initializing the audioRecord object
//
//        bufferSizeInByte = AudioRecord.getMinBufferSize(SampleRate, Channel, EncodingType)
//        audioRecorder = AudioRecord(AudioSource, SampleRate, Channel,
//            EncodingType, bufferSizeInByte)
//    }
//
//    fun startRecord():Int {
//
//        if (isRecord) {
//            return -1
//        } else{
//
//            audioRecorder?: initRecorder()
//            audioRecorder?.startRecording()
//            isRecord = true
//
//            AudioRecordToFile().start()
//            return 0
//        }
//    }
//
//    fun stopRecord() {
//
//        audioRecorder?.stop()
//        audioRecorder?.release()
//        isRecord = false
//        audioRecorder = null
//    }
//
//    private fun writeDateTOFile() {
//
//        var audioData = ByteArray(bufferSizeInByte)
//        val file = File(PCMPath)
//        if (!file.parentFile.exists()) {
//
//            file.parentFile.mkdirs()
//        }
//        if (file.exists()) {
//            file.delete()
//        }
//        file.createNewFile()
//        val out = BufferedOutputStream(FileOutputStream(file))
//        var length = 0
//        while (isRecord && audioRecorder!=null) {
//            length = audioRecorder!!.read(audioData, 0, bufferSizeInByte)//Get audio data
//            if (AudioRecord.ERROR_INVALID_OPERATION != length) {
//                out.write(audioData, 0, length)//write file
//                out.flush()
//            }
//        }
//        out.close()
//    }
//
//    //Converting pcm files to WAV files
//    private fun copyWaveFile(pcmPath: String, wavPath: String) {
//
//        var fileIn = FileInputStream(pcmPath)
//        var fileOut = FileOutputStream(wavPath)
//        val data = ByteArray(bufferSizeInByte)
//        val totalAudioLen = fileIn.channel.size()
//        val totalDataLen = totalAudioLen + 36
//        writeWaveFileHeader(fileOut, totalAudioLen, totalDataLen)
//        var count = fileIn.read(data, 0, bufferSizeInByte)
//        while (count != -1) {
//            fileOut.write(data, 0, count)
//            fileOut.flush()
//            count = fileIn.read(data, 0, bufferSizeInByte)
//        }
//        fileIn.close()
//        fileOut.close()
//    }
//
//    //Add file header in WAV format
//    private fun writeWaveFileHeader(out:FileOutputStream , totalAudioLen:Long,
//                                    totalDataLen:Long){
//
//        val channels = 1
//        val byteRate = 16 * SampleRate * channels / 8
//        val header = ByteArray(44)
//        header[0] = 'R'.toByte()
//        header[1] = 'I'.toByte()
//        header[2] = 'F'.toByte()
//        header[3] = 'F'.toByte()
//        header[4] = (totalDataLen and 0xff).toByte()
//        header[5] = (totalDataLen shr 8 and 0xff).toByte()
//        header[6] = (totalDataLen shr 16 and 0xff).toByte()
//        header[7] = (totalDataLen shr 24 and 0xff).toByte()
//        header[8] = 'W'.toByte()
//        header[9] = 'A'.toByte()
//        header[10] = 'V'.toByte()
//        header[11] = 'E'.toByte()
//        header[12] = 'f'.toByte() // 'fmt ' chunk
//        header[13] = 'm'.toByte()
//        header[14] = 't'.toByte()
//        header[15] = ' '.toByte()
//        header[16] = 16 // 4 bytes: size of 'fmt ' chunk
//        header[17] = 0
//        header[18] = 0
//        header[19] = 0
//        header[20] = 1 // format = 1
//        header[21] = 0
//        header[22] = channels.toByte()
//        header[23] = 0
//        header[24] = (SampleRate and 0xff).toByte()
//        header[25] = (SampleRate shr 8 and 0xff).toByte()
//        header[26] = (SampleRate shr 16 and 0xff).toByte()
//        header[27] = (SampleRate shr 24 and 0xff).toByte()
//        header[28] = (byteRate and 0xff).toByte()
//        header[29] = (byteRate shr 8 and 0xff).toByte()
//        header[30] = (byteRate shr 16 and 0xff).toByte()
//        header[31] = (byteRate shr 24 and 0xff).toByte()
//        header[32] = (2 * 16 / 8).toByte() // block align
//        header[33] = 0
//        header[34] = 16 // bits per sample
//        header[35] = 0
//        header[36] = 'd'.toByte()
//        header[37] = 'a'.toByte()
//        header[38] = 't'.toByte()
//        header[39] = 'a'.toByte()
//        header[40] = (totalAudioLen and 0xff).toByte()
//        header[41] = (totalAudioLen shr 8 and 0xff).toByte()
//        header[42] = (totalAudioLen shr 16 and 0xff).toByte()
//        header[43] = (totalAudioLen shr 24 and 0xff).toByte()
//        out.write(header, 0, 44)
//    }
//
//    private inner class AudioRecordToFile : Thread() {
//
//        override fun run() {
//            super.run()
//
//            writeDateTOFile()
//            copyWaveFile(PCMPath, WAVPath)
//        }
//    }
//}