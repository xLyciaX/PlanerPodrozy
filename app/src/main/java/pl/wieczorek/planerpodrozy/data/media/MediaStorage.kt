package pl.wieczorek.planerpodrozy.data.media

import android.net.Uri

/** Plik na zdjęcie z aparatu: [uri] dostaje aplikacja aparatu, [path] zapisujemy w bazie. */
data class CameraPhoto(val path: String, val uri: Uri)

/**
 * Pliki multimediów w pamięci wewnętrznej aplikacji (filesDir).
 * Kopiujemy je tutaj, żeby nie stracić dostępu do zdjęć wybranych z galerii.
 */
interface MediaStorage {
    /** Kopiuje zdjęcie (np. z galerii) do pamięci aplikacji i zwraca ścieżkę kopii. */
    suspend fun importPhoto(source: Uri): String

    /** Tworzy pusty plik, do którego aplikacja aparatu zapisze zdjęcie. */
    fun createCameraPhoto(): CameraPhoto

    /** Usuwa plik (np. zastąpione lub usunięte zdjęcie). Brak pliku nie jest błędem. */
    suspend fun delete(path: String)
}
