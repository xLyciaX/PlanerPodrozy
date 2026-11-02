package pl.wieczorek.planerpodrozy.ui.screens.tripdetail

import pl.wieczorek.planerpodrozy.domain.model.TripDetail

/** Błędy multimediów pokazywane krótkim komunikatem. */
enum class MediaError { VideoImport, Recording, MicrophoneDenied }

data class TripDetailUiState(
    /** null w trakcie ładowania albo gdy podróży nie ma. */
    val detail: TripDetail? = null,
    val isLoading: Boolean = true,
    /** Otwarty arkusz „+” z opcjami „Dodaj film” / „Nagraj notatkę głosową”. */
    val isMediaSheetOpen: Boolean = false,
    /** Trwa kopiowanie wybranego filmu do pamięci aplikacji. */
    val isImportingVideo: Boolean = false,
    /** Czas trwającego nagrania; null = nie nagrywamy. */
    val recordingElapsedMs: Long? = null,
    val error: MediaError? = null,
)
