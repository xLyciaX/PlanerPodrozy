package pl.wieczorek.planerpodrozy.data.repository

import kotlinx.coroutines.flow.Flow
import pl.wieczorek.planerpodrozy.domain.model.DayDetail
import pl.wieczorek.planerpodrozy.domain.model.DraftPhoto
import pl.wieczorek.planerpodrozy.domain.model.PhotoCategory

/** Dostęp do pojedynczego dnia podróży (osobny, mały interfejs zamiast jednego „wszystkomającego”). */
interface DayRepository {
    /** Dzień ze zdjęciami; null, gdy dnia nie ma. */
    fun observeDay(dayId: Long): Flow<DayDetail?>

    /**
     * Zapisuje zdjęcie w kadrze danej kategorii. Jeśli kadr był zajęty, poprzednie zdjęcie
     * (i jego plik) jest zastępowane, więc dzień nigdy nie ma więcej niż 3 zdjęć.
     */
    suspend fun setPhoto(dayId: Long, category: PhotoCategory, filePath: String)

    /**
     * Zapisuje wynik edycji dnia: lokalizacje, opis i docelowy zestaw zdjęć [photos].
     * Zdjęcia, których nie ma już w [photos] (usunięte lub zastąpione), znikają z bazy razem z plikami.
     */
    suspend fun saveDay(
        dayId: Long,
        locations: List<String>,
        description: String,
        photos: Map<PhotoCategory, DraftPhoto>,
    )
}
