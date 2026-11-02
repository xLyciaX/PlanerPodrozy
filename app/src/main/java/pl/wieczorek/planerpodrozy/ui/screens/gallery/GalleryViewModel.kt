package pl.wieczorek.planerpodrozy.ui.screens.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import pl.wieczorek.planerpodrozy.data.repository.TripRepository
import pl.wieczorek.planerpodrozy.domain.model.PhotoCategory

class GalleryViewModel(tripId: Long, repository: TripRepository) : ViewModel() {

    private val selectedCategory = MutableStateFlow<PhotoCategory?>(null)

    val uiState: StateFlow<GalleryUiState> =
        combine(repository.observeGallery(tripId), selectedCategory) { gallery, category ->
            GalleryUiState(
                tripName = gallery?.tripName.orEmpty(),
                photos = gallery?.photos.orEmpty().filter { category == null || it.category == category },
                selectedCategory = category,
                isLoading = false,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = GalleryUiState(),
        )

    /** Jeden filtr naraz; null = „Wszystkie”. */
    fun onSelectCategory(category: PhotoCategory?) {
        selectedCategory.value = category
    }
}
