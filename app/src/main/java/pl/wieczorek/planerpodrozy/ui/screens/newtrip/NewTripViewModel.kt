package pl.wieczorek.planerpodrozy.ui.screens.newtrip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.wieczorek.planerpodrozy.data.repository.TripRepository
import java.time.LocalDate

class NewTripViewModel(private val repository: TripRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(NewTripUiState())
    val uiState: StateFlow<NewTripUiState> = _uiState.asStateFlow()

    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun onOpenDatePicker() {
        _uiState.update { it.copy(isDatePickerOpen = true) }
    }

    fun onDismissDatePicker() {
        _uiState.update { it.copy(isDatePickerOpen = false) }
    }

    fun onDatesSelected(start: LocalDate, end: LocalDate) {
        _uiState.update { it.copy(startDate = start, endDate = end, isDatePickerOpen = false) }
    }

    fun onSave() {
        val state = _uiState.value
        val start = state.startDate ?: return
        val end = state.endDate ?: return
        if (!state.canSave) return
        _uiState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            repository.createTrip(name = state.name.trim(), startDate = start, endDate = end)
            _uiState.update { it.copy(isSaving = false, isSaved = true) }
        }
    }
}
