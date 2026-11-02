package pl.wieczorek.planerpodrozy.ui.screens.tripdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import pl.wieczorek.planerpodrozy.data.repository.TripRepository

class TripDetailViewModel(tripId: Long, repository: TripRepository) : ViewModel() {

    val uiState: StateFlow<TripDetailUiState> =
        repository.observeTripDetail(tripId)
            .map { TripDetailUiState(detail = it, isLoading = false) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = TripDetailUiState(),
            )
}
