package pl.wieczorek.planerpodrozy.data.media

import android.content.Context
import android.media.MediaRecorder
import android.os.Build

/** Nagrywanie przez systemowy MediaRecorder: AAC w pliku .m4a. */
class AndroidAudioRecorder(private val context: Context) : AudioRecorder {

    private var recorder: MediaRecorder? = null

    override fun start(outputPath: String) {
        val newRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }
        try {
            newRecorder.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(128_000)
                setAudioSamplingRate(44_100)
                setOutputFile(outputPath)
                prepare()
                start()
            }
        } catch (e: Exception) {
            newRecorder.release()
            throw e
        }
        recorder = newRecorder
    }

    override fun stop() {
        val current = recorder ?: return
        recorder = null
        try {
            current.stop()
        } finally {
            current.release()
        }
    }

    override fun cancel() {
        val current = recorder ?: return
        recorder = null
        runCatching { current.stop() }
        current.release()
    }
}
