package pl.wieczorek.planerpodrozy.ui.screens.dayedit

import pl.wieczorek.planerpodrozy.domain.model.DraftPhoto
import pl.wieczorek.planerpodrozy.domain.model.PhotoCategory
import java.time.LocalDate

/** Stan roboczy edycji dnia: nic z tego nie trafia do bazy przed „Zapisz”. */
data class DayEditUiState(
    val isLoading: Boolean = true,
    val dayNumber: Int = 0,
    val date: LocalDate? = null,
    val locations: List<String> = emptyList(),
    val description: String = "",
    val photos: Map<PhotoCategory, DraftPhoto> = emptyMap(),
    val isAddLocationOpen: Boolean = false,
    /** Kadr, dla którego otwarty jest arkusz „Dodaj zdjęcie” (null = zamknięty). */
    val photoSourceCategory: PhotoCategory? = null,
    val showPhotoError: Boolean = false,
    val isSaving: Boolean = false,
    /** true po zapisie albo anulowaniu: ekran wraca do widoku dnia. */
    val isFinished: Boolean = false,
)
