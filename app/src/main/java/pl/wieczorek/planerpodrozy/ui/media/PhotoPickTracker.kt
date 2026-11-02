package pl.wieczorek.planerpodrozy.ui.media

import android.net.Uri
import pl.wieczorek.planerpodrozy.data.media.MediaStorage
import pl.wieczorek.planerpodrozy.domain.model.PhotoCategory

/** Zdjęcie wybrane do kadru: plik już skopiowany do pamięci aplikacji. */
data class PickedPhoto(val category: PhotoCategory, val filePath: String)

/**
 * Pamięta, do którego kadru ma trafić zdjęcie, zanim aparat lub galeria (osobne aplikacje)
 * odeślą wynik. Wspólne dla ViewModeli widoku dnia i edycji dnia.
 */
class PhotoPickTracker(private val mediaStorage: MediaStorage) {

    private var pendingCamera: PickedPhoto? = null
    private var pendingGalleryCategory: PhotoCategory? = null

    /** Tworzy plik na zdjęcie i zwraca adres, pod który aparat ma je zapisać. */
    fun prepareCamera(category: PhotoCategory): Uri {
        val target = mediaStorage.createCameraPhoto()
        pendingCamera = PickedPhoto(category, target.path)
        return target.uri
    }

    /** Zdjęcie z aparatu albo null po anulowaniu (wtedy pusty plik jest usuwany). */
    suspend fun onCameraResult(success: Boolean): PickedPhoto? {
        val pending = pendingCamera ?: return null
        pendingCamera = null
        if (success) return pending
        mediaStorage.delete(pending.filePath)
        return null
    }

    fun onGalleryStarted(category: PhotoCategory) {
        pendingGalleryCategory = category
    }

    /**
     * Kopiuje zdjęcie wybrane w galerii; null, gdy galerię zamknięto bez wyboru.
     * Rzuca wyjątek, gdy kopiowanie się nie uda.
     */
    suspend fun onGalleryResult(uri: Uri?): PickedPhoto? {
        val category = pendingGalleryCategory ?: return null
        pendingGalleryCategory = null
        if (uri == null) return null
        return PickedPhoto(category, mediaStorage.importPhoto(uri))
    }
}
