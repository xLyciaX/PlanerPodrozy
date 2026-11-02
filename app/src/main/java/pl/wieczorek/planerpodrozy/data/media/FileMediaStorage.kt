package pl.wieczorek.planerpodrozy.data.media

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

class FileMediaStorage(private val context: Context) : MediaStorage {

    private val photosDir: File
        get() = File(context.filesDir, PHOTOS_DIR).apply { mkdirs() }

    // Dispatchers.IO: operacje na plikach w wątku w tle, żeby nie zamrozić interfejsu.
    override suspend fun importPhoto(source: Uri): String = withContext(Dispatchers.IO) {
        val target = newPhotoFile()
        val input = context.contentResolver.openInputStream(source)
            ?: error("Nie można otworzyć $source")
        input.use { from -> target.outputStream().use { to -> from.copyTo(to) } }
        target.absolutePath
    }

    override fun createCameraPhoto(): CameraPhoto {
        val file = newPhotoFile()
        // FileProvider zamienia prywatny plik na adres content://, który może zapisać aplikacja aparatu.
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        return CameraPhoto(path = file.absolutePath, uri = uri)
    }

    override suspend fun delete(path: String) {
        withContext(Dispatchers.IO) { File(path).delete() }
    }

    private fun newPhotoFile(): File = File(photosDir, "${UUID.randomUUID()}.jpg")

    private companion object {
        const val PHOTOS_DIR = "photos"
    }
}
