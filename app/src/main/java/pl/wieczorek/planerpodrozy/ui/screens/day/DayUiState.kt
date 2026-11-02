package pl.wieczorek.planerpodrozy.ui.screens.day

import pl.wieczorek.planerpodrozy.domain.model.DayDetail
import pl.wieczorek.planerpodrozy.domain.model.PhotoCategory

data class DayUiState(
    /** null w trakcie ładowania albo gdy dnia nie ma. */
    val detail: DayDetail? = null,
    val isLoading: Boolean = true,
    /** Kategoria pustego kadru, dla którego otwarty jest arkusz „Dodaj zdjęcie” (null = zamknięty). */
    val photoSourceCategory: PhotoCategory? = null,
    /** true, gdy nie udało się dodać zdjęcia (pokazujemy krótki komunikat). */
    val showPhotoError: Boolean = false,
)
