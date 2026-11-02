package pl.wieczorek.planerpodrozy.ui.screens.tripdetail

import pl.wieczorek.planerpodrozy.domain.model.TripDetail

data class TripDetailUiState(
    /** null w trakcie ładowania albo gdy podróży nie ma. */
    val detail: TripDetail? = null,
    val isLoading: Boolean = true,
)
