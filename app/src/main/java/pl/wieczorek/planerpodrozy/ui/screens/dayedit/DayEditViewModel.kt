package pl.wieczorek.planerpodrozy.ui.screens.dayedit

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.wieczorek.planerpodrozy.data.media.MediaStorage
import pl.wieczorek.planerpodrozy.data.repository.DayRepository
import pl.wieczorek.planerpodrozy.domain.model.DraftPhoto
import pl.wieczorek.planerpodrozy.domain.model.PhotoCategory
import pl.wieczorek.planerpodrozy.domain.model.TripDay
import pl.wieczorek.planerpodrozy.ui.media.PhotoPickTracker
import pl.wieczorek.planerpodrozy.ui.media.PickedPhoto
import java.time.Instant

/**
 * Edycja dnia na kopii roboczej. Zmiany trafiają do bazy dopiero w [onSave];
 * [onCancel] (także systemowe „wstecz”) je odrzuca.
 */
class DayEditViewModel(
    private val dayId: Long,
    private val dayRepository: DayRepository,
    private val mediaStorage: MediaStorage,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DayEditUiState())
    val uiState: StateFlow<DayEditUiState> = _uiState.asStateFlow()

    private val photoPick = PhotoPickTracker(mediaStorage)

    // Pliki zdjęć dodanych w tej edycji. Po „Anuluj” trzeba je usunąć, bo nie trafią do bazy.
    private val filesAddedInSession = mutableSetOf<String>()

    init {
        // Kopia robocza powstaje raz, z aktualnego stanu dnia w bazie.
        viewModelScope.launch {
            val detail = dayRepository.observeDay(dayId).first() ?: return@launch
            _uiState.update {
                it.copy(
                    isLoading = false,
                    dayNumber = detail.day.dayNumber,
                    date = detail.day.date,
                    locations = detail.day.locations,
                    description = detail.day.description,
                    photos = detail.photos.mapValues { (_, photo) -> DraftPhoto(photo.filePath, photo.addedAt) },
                )
            }
        }
    }

    // --- Lokalizacje ---

    fun onOpenAddLocation() {
        _uiState.update { it.copy(isAddLocationOpen = true) }
    }

    fun onDismissAddLocation() {
        _uiState.update { it.copy(isAddLocationOpen = false) }
    }

    fun onAddLocation(location: String) {
        _uiState.update { it.copy(locations = it.locations + location, isAddLocationOpen = false) }
    }

    fun onRemoveLocation(index: Int) {
        _uiState.update { state ->
            state.copy(locations = state.locations.filterIndexed { i, _ -> i != index })
        }
    }

    // --- Opis ---

    fun onDescriptionChange(text: String) {
        _uiState.update { it.copy(description = text.take(TripDay.MaxDescriptionLength)) }
    }

    // --- Zdjęcia ---

    /** Pusty kadr albo „zmień” na zdjęciu: oba otwierają wybór źródła dla tej kategorii. */
    fun onChoosePhoto(category: PhotoCategory) {
        _uiState.update { it.copy(photoSourceCategory = category) }
    }

    fun onDismissPhotoSource() {
        _uiState.update { it.copy(photoSourceCategory = null) }
    }

    fun onDeletePhoto(category: PhotoCategory) {
        _uiState.update { it.copy(photos = it.photos - category) }
    }

    fun prepareCameraPhoto(category: PhotoCategory): Uri = photoPick.prepareCamera(category)

    fun onCameraResult(success: Boolean) {
        viewModelScope.launch {
            photoPick.onCameraResult(success)?.let { putInDraft(it) }
        }
    }

    fun onGalleryPickStarted(category: PhotoCategory) {
        photoPick.onGalleryStarted(category)
    }

    fun onGalleryResult(uri: Uri?) {
        viewModelScope.launch {
            runCatching { photoPick.onGalleryResult(uri) }
                .onSuccess { picked -> picked?.let { putInDraft(it) } }
                .onFailure { _uiState.update { it.copy(showPhotoError = true) } }
        }
    }

    fun onPhotoErrorShown() {
        _uiState.update { it.copy(showPhotoError = false) }
    }

    // Nowe zdjęcie zastępuje poprzednie w tym kadrze (tylko w kopii roboczej).
    private fun putInDraft(photo: PickedPhoto) {
        filesAddedInSession += photo.filePath
        _uiState.update {
            it.copy(photos = it.photos + (photo.category to DraftPhoto(photo.filePath, Instant.now())))
        }
    }

    // --- Zapis / anulowanie ---

    fun onSave() {
        val state = _uiState.value
        if (state.isLoading || state.isSaving) return
        _uiState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            dayRepository.saveDay(dayId, state.locations, state.description.trim(), state.photos)
            // Zdjęcia dodane w tej edycji, ale potem zastąpione lub usunięte, nie są nikomu potrzebne.
            val keptFiles = state.photos.values.map { it.filePath }.toSet()
            deleteSessionFiles(except = keptFiles)
            _uiState.update { it.copy(isSaving = false, isFinished = true) }
        }
    }

    fun onCancel() {
        viewModelScope.launch {
            deleteSessionFiles(except = emptySet())
            _uiState.update { it.copy(isFinished = true) }
        }
    }

    private suspend fun deleteSessionFiles(except: Set<String>) {
        filesAddedInSession.filterNot { it in except }.forEach { mediaStorage.delete(it) }
        filesAddedInSession.clear()
    }
}
