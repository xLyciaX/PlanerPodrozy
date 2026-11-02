package pl.wieczorek.planerpodrozy.ui.screens.tripdetail

import android.net.Uri
import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import pl.wieczorek.planerpodrozy.data.media.AudioRecorder
import pl.wieczorek.planerpodrozy.data.media.MediaStorage
import pl.wieczorek.planerpodrozy.data.repository.TripRepository

class TripDetailViewModel(
    private val tripId: Long,
    private val tripRepository: TripRepository,
    private val mediaStorage: MediaStorage,
    private val audioRecorder: AudioRecorder,
) : ViewModel() {

    // Stan multimediów niezależny od bazy (arkusz, nagrywanie, błędy).
    private val mediaState = MutableStateFlow(TripDetailUiState())

    val uiState: StateFlow<TripDetailUiState> =
        combine(tripRepository.observeTripDetail(tripId), mediaState) { detail, media ->
            media.copy(detail = detail, isLoading = false)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TripDetailUiState(),
        )

    private var recordingPath: String? = null
    private var recordingTimer: Job? = null

    fun onOpenMediaSheet() {
        mediaState.update { it.copy(isMediaSheetOpen = true) }
    }

    fun onDismissMediaSheet() {
        mediaState.update { it.copy(isMediaSheetOpen = false) }
    }

    // --- Film ---

    /** [uri] = null, gdy galerię zamknięto bez wyboru. Nowy film zastępuje poprzedni. */
    fun onVideoPicked(uri: Uri?) {
        if (uri == null) return
        mediaState.update { it.copy(isImportingVideo = true) }
        viewModelScope.launch {
            runCatching { mediaStorage.importVideo(uri) }
                .onSuccess { path -> tripRepository.setVideo(tripId, path) }
                .onFailure { mediaState.update { it.copy(error = MediaError.VideoImport) } }
            mediaState.update { it.copy(isImportingVideo = false) }
        }
    }

    // --- Notatka głosowa ---

    fun onStartRecording() {
        if (recordingPath != null) return
        val path = mediaStorage.createVoiceNoteFile()
        try {
            audioRecorder.start(path)
        } catch (e: Exception) {
            viewModelScope.launch { mediaStorage.delete(path) }
            mediaState.update { it.copy(error = MediaError.Recording) }
            return
        }
        recordingPath = path
        val startedAt = SystemClock.elapsedRealtime()
        mediaState.update { it.copy(recordingElapsedMs = 0) }
        // Licznik czasu w dialogu nagrywania.
        recordingTimer = viewModelScope.launch {
            while (true) {
                delay(TimerTickMillis)
                mediaState.update { it.copy(recordingElapsedMs = SystemClock.elapsedRealtime() - startedAt) }
            }
        }
    }

    fun onStopRecording() {
        val path = finishRecording() ?: return
        viewModelScope.launch {
            val saved = runCatching { audioRecorder.stop() }.isSuccess
            if (saved) {
                tripRepository.setVoiceNote(tripId, path)
            } else {
                // Np. nagranie krótsze niż ułamek sekundy: plik jest pusty.
                mediaStorage.delete(path)
                mediaState.update { it.copy(error = MediaError.Recording) }
            }
        }
    }

    fun onCancelRecording() {
        val path = finishRecording() ?: return
        audioRecorder.cancel()
        viewModelScope.launch { mediaStorage.delete(path) }
    }

    fun onMicrophoneDenied() {
        mediaState.update { it.copy(error = MediaError.MicrophoneDenied) }
    }

    fun onErrorShown() {
        mediaState.update { it.copy(error = null) }
    }

    /** Zatrzymuje licznik i zwraca ścieżkę nagrania (null, gdy nic się nie nagrywało). */
    private fun finishRecording(): String? {
        val path = recordingPath ?: return null
        recordingPath = null
        recordingTimer?.cancel()
        mediaState.update { it.copy(recordingElapsedMs = null) }
        return path
    }

    // ViewModel jest niszczony (np. wyjście z ekranu): nie zostawiamy włączonego mikrofonu.
    // viewModelScope jest już wtedy anulowany, dlatego krótkie usunięcie pliku robimy w runBlocking.
    override fun onCleared() {
        val path = recordingPath ?: return
        audioRecorder.cancel()
        runBlocking { mediaStorage.delete(path) }
    }

    private companion object {
        const val TimerTickMillis = 200L
    }
}
