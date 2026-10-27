package pl.wieczorek.planerpodrozy.ui.screens.triplist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import pl.wieczorek.planerpodrozy.data.repository.TripRepository
import pl.wieczorek.planerpodrozy.domain.model.TripSummary

class TripListViewModel(repository: TripRepository) : ViewModel() {

    private val sortOrder = MutableStateFlow(SortOrder.DESCENDING)

    // Łączy podróże z bazy z wybranym kierunkiem sortowania. Zmiana któregokolwiek
    // z nich daje nowy stan, a ekran przerysowuje się sam.
    val uiState: StateFlow<TripListUiState> =
        combine(repository.observeTrips(), sortOrder) { trips, order ->
            TripListUiState(trips = trips.sortedBy(order), sortOrder = order, isLoading = false)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TripListUiState(),
        )

    fun onSortChange(order: SortOrder) {
        sortOrder.value = order
    }

    private fun List<TripSummary>.sortedBy(order: SortOrder): List<TripSummary> {
        // Przy tej samej dacie startu decyduje kolejność dodania.
        val ascending = compareBy<TripSummary>({ it.trip.startDate }, { it.trip.createdAt })
        return sortedWith(if (order == SortOrder.ASCENDING) ascending else ascending.reversed())
    }
}
