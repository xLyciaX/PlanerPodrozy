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
import pl.wieczorek.planerpodrozy.ui.media.PhotoPickTracker
import pl.wieczorek.planerpodrozy.ui.media.PickedPhoto

class DayViewModel(
    private val dayId: Long,
    private val dayRepository: DayRepository,
    private val mediaStorage: MediaStorage,
) : ViewModel() {

    private val photoSourceCategory = MutableStateFlow<PhotoCategory?>(null)
    private val showPhotoError = MutableStateFlow(false)

    private val photoPick = PhotoPickTracker(mediaStorage)

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
    fun prepareCameraPhoto(category: PhotoCategory): Uri = photoPick.prepareCamera(category)

    // Zdjęcie dodane z widoku dnia zapisuje się od razu (bez przycisku „Zapisz dzień”).
    fun onCameraResult(success: Boolean) {
        viewModelScope.launch {
            photoPick.onCameraResult(success)?.let { save(it) }
        }
    }

    fun onGalleryPickStarted(category: PhotoCategory) {
        photoPick.onGalleryStarted(category)
    }

    /** [uri] = null, gdy użytkownik zamknął galerię bez wyboru. */
    fun onGalleryResult(uri: Uri?) {
        viewModelScope.launch {
            runCatching { photoPick.onGalleryResult(uri) }
                .onSuccess { picked -> picked?.let { save(it) } }
                .onFailure { showPhotoError.value = true }
        }
    }

    private suspend fun save(photo: PickedPhoto) {
        dayRepository.setPhoto(dayId, photo.category, photo.filePath)
    }

    fun onPhotoErrorShown() {
        showPhotoError.value = false
    }
}
