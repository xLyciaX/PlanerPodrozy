package pl.wieczorek.planerpodrozy.ui.screens.day

import pl.wieczorek.planerpodrozy.domain.model.DayDetail

data class DayUiState(
    /** null w trakcie ładowania albo gdy dnia nie ma. */
    val detail: DayDetail? = null,
    val isLoading: Boolean = true,
)
