package pl.wieczorek.planerpodrozy.data.media

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

class FileMediaStorage(private val context: Context) : MediaStorage {

    override suspend fun importPhoto(source: Uri): String = copyToAppStorage(source, newFile(PHOTOS_DIR, "jpg"))

    override suspend fun importVideo(source: Uri): String = copyToAppStorage(source, newFile(VIDEOS_DIR, "mp4"))

    override fun createCameraPhoto(): CameraPhoto {
        val file = newFile(PHOTOS_DIR, "jpg")
        // FileProvider zamienia prywatny plik na adres content://, który może zapisać aplikacja aparatu.
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        return CameraPhoto(path = file.absolutePath, uri = uri)
    }

    override fun createVoiceNoteFile(): String = newFile(AUDIO_DIR, "m4a").absolutePath

    override suspend fun delete(path: String) {
        withContext(Dispatchers.IO) { File(path).delete() }
    }

    // Dispatchers.IO: operacje na plikach w wątku w tle, żeby nie zamrozić interfejsu
    // (kopiowanie dużego filmu może potrwać kilka sekund).
    private suspend fun copyToAppStorage(source: Uri, target: File): String = withContext(Dispatchers.IO) {
        val input = context.contentResolver.openInputStream(source)
            ?: error("Nie można otworzyć $source")
        input.use { from -> target.outputStream().use { to -> from.copyTo(to) } }
        target.absolutePath
    }

    private fun newFile(directory: String, extension: String): File {
        val dir = File(context.filesDir, directory).apply { mkdirs() }
        return File(dir, "${UUID.randomUUID()}.$extension")
    }

    private companion object {
        const val PHOTOS_DIR = "photos"
        const val VIDEOS_DIR = "videos"
        const val AUDIO_DIR = "audio"
    }
}
