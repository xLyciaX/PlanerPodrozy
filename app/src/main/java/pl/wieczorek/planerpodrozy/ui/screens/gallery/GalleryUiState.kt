package pl.wieczorek.planerpodrozy.ui.screens.gallery

import pl.wieczorek.planerpodrozy.domain.model.Photo
import pl.wieczorek.planerpodrozy.domain.model.PhotoCategory

data class GalleryUiState(
    val tripName: String = "",
    /** Zdjęcia po zastosowaniu filtra. */
    val photos: List<Photo> = emptyList(),
    /** Wybrany filtr; null = „Wszystkie”. */
    val selectedCategory: PhotoCategory? = null,
    val isLoading: Boolean = true,
)
