package pl.wieczorek.planerpodrozy.domain.model

import java.time.Instant
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/** Podróż: cel, zakres dat i opcjonalne multimedia (ścieżki do kopii w pamięci aplikacji). */
data class Trip(
    val id: Long,
    val name: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val videoPath: String?,
    val voiceNotePath: String?,
    val createdAt: Instant,
) {
    /** Liczba dni podróży, łącznie z pierwszym i ostatnim. */
    val dayCount: Int
        get() = ChronoUnit.DAYS.between(startDate, endDate).toInt() + 1
}

/** Jeden dzień podróży. Tworzy się automatycznie przy zapisie podróży. */
data class TripDay(
    val id: Long,
    val tripId: Long,
    val dayNumber: Int,
    val date: LocalDate,
    val description: String,
    val locations: List<String>,
) {
    companion object {
        const val MaxDescriptionLength = 500
    }
}

/** Zdjęcie przypisane do dnia i kategorii. [addedAt] decyduje o okładkach. */
data class Photo(
    val id: Long,
    val dayId: Long,
    val category: PhotoCategory,
    val filePath: String,
    val addedAt: Instant,
)

/**
 * Zdjęcie w stanie roboczym edycji dnia (jeszcze niezapisane). [addedAt] to moment wybrania
 * zdjęcia, więc po zapisie decyduje o okładkach tak samo jak przy zwykłym dodaniu.
 */
data class DraftPhoto(
    val filePath: String,
    val addedAt: Instant,
)

/** Podróż razem z okładką do pokazania na liście (null = grafika pustej podróży). */
data class TripSummary(
    val trip: Trip,
    val coverPath: String?,
)
