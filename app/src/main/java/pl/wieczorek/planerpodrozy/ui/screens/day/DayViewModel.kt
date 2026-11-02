package pl.wieczorek.planerpodrozy.ui.screens.day

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import pl.wieczorek.planerpodrozy.data.repository.DayRepository

class DayViewModel(dayId: Long, repository: DayRepository) : ViewModel() {

    val uiState: StateFlow<DayUiState> =
        repository.observeDay(dayId)
            .map { DayUiState(detail = it, isLoading = false) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = DayUiState(),
            )
}
