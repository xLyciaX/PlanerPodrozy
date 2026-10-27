package pl.wieczorek.planerpodrozy.ui.screens.triplist

import pl.wieczorek.planerpodrozy.domain.model.TripSummary

enum class SortOrder { ASCENDING, DESCENDING }

/** Wszystko, co ekran listy podróży potrzebuje do narysowania się. */
data class TripListUiState(
    val trips: List<TripSummary> = emptyList(),
    val sortOrder: SortOrder = SortOrder.DESCENDING,
    /** true do pierwszego odczytu z bazy, żeby nie mignął komunikat o pustej liście. */
    val isLoading: Boolean = true,
)
