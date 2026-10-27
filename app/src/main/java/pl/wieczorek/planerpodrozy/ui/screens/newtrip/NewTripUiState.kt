package pl.wieczorek.planerpodrozy.ui.screens.newtrip

import java.time.LocalDate

data class NewTripUiState(
    val name: String = "",
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val isDatePickerOpen: Boolean = false,
    val isSaving: Boolean = false,
    /** Po zapisie ekran wraca do listy. */
    val isSaved: Boolean = false,
) {
    /** „Zapisz” jest aktywne dopiero, gdy podano nazwę i oba końce zakresu. */
    val canSave: Boolean
        get() = name.isNotBlank() && startDate != null && endDate != null && !isSaving
}
