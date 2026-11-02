package pl.wieczorek.planerpodrozy.ui.screens.day

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pl.wieczorek.planerpodrozy.data.media.MediaStorage
import pl.wieczorek.planerpodrozy.data.repository.DayRepository
import pl.wieczorek.planerpodrozy.domain.model.PhotoCategory

class DayViewModel(
    private val dayId: Long,
    private val dayRepository: DayRepository,
    private val mediaStorage: MediaStorage,
) : ViewModel() {

    private val photoSourceCategory = MutableStateFlow<PhotoCategory?>(null)
    private val showPhotoError = MutableStateFlow(false)

    // Dokąd ma trafić wynik z aparatu lub galerii (aparat i galeria to osobne aplikacje,
    // więc między wysłaniem prośby a wynikiem trzeba zapamiętać kategorię kadru).
    private var pendingCamera: Pair<PhotoCategory, String>? = null
    private var pendingGalleryCategory: PhotoCategory? = null

    val uiState: StateFlow<DayUiState> =
        combine(dayRepository.observeDay(dayId), photoSourceCategory, showPhotoError) { day, category, error ->
            DayUiState(detail = day, isLoading = false, photoSourceCategory = category, showPhotoError = error)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DayUiState(),
        )

    fun onAddPhoto(category: PhotoCategory) {
        photoSourceCategory.value = category
    }

    fun onDismissPhotoSource() {
        photoSourceCategory.value = null
    }

    /** Tworzy plik na zdjęcie i zwraca adres, pod który aparat ma je zapisać. */
    fun prepareCameraPhoto(category: PhotoCategory): Uri {
        val target = mediaStorage.createCameraPhoto()
        pendingCamera = category to target.path
        return target.uri
    }

    fun onCameraResult(success: Boolean) {
        val (category, path) = pendingCamera ?: return
        pendingCamera = null
        viewModelScope.launch {
            if (success) {
                dayRepository.setPhoto(dayId, category, path)
            } else {
                // Anulowano zdjęcie: pusty plik nie jest potrzebny.
                mediaStorage.delete(path)
            }
        }
    }

    fun onGalleryPickStarted(category: PhotoCategory) {
        pendingGalleryCategory = category
    }

    /** [uri] = null, gdy użytkownik zamknął galerię bez wyboru. */
    fun onGalleryResult(uri: Uri?) {
        val category = pendingGalleryCategory ?: return
        pendingGalleryCategory = null
        if (uri == null) return
        viewModelScope.launch {
            runCatching { mediaStorage.importPhoto(uri) }
                .onSuccess { path -> dayRepository.setPhoto(dayId, category, path) }
                .onFailure { showPhotoError.value = true }
        }
    }

    fun onPhotoErrorShown() {
        showPhotoError.value = false
    }
}
