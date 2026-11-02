package pl.wieczorek.planerpodrozy.ui.screens.triplist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pl.wieczorek.planerpodrozy.data.repository.TripRepository
import pl.wieczorek.planerpodrozy.domain.model.TripSummary

class TripListViewModel(private val repository: TripRepository) : ViewModel() {

    private val sortOrder = MutableStateFlow(SortOrder.DESCENDING)
    private val tripToDelete = MutableStateFlow<TripSummary?>(null)

    // Łączy podróże z bazy z wybranym kierunkiem sortowania. Zmiana któregokolwiek
    // z nich daje nowy stan, a ekran przerysowuje się sam.
    val uiState: StateFlow<TripListUiState> =
        combine(repository.observeTrips(), sortOrder, tripToDelete) { trips, order, toDelete ->
            TripListUiState(
                trips = trips.sortedBy(order),
                sortOrder = order,
                isLoading = false,
                tripToDelete = toDelete,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TripListUiState(),
        )

    fun onSortChange(order: SortOrder) {
        sortOrder.value = order
    }

    /** Przesunięcie karty: najpierw pytamy o potwierdzenie. */
    fun onRequestDelete(trip: TripSummary) {
        tripToDelete.value = trip
    }

    fun onDismissDelete() {
        tripToDelete.value = null
    }

    fun onConfirmDelete() {
        val trip = tripToDelete.value ?: return
        tripToDelete.value = null
        viewModelScope.launch { repository.deleteTrip(trip.trip.id) }
    }

    private fun List<TripSummary>.sortedBy(order: SortOrder): List<TripSummary> {
        // Przy tej samej dacie startu decyduje kolejność dodania.
        val ascending = compareBy<TripSummary>({ it.trip.startDate }, { it.trip.createdAt })
        return sortedWith(if (order == SortOrder.ASCENDING) ascending else ascending.reversed())
    }
}
