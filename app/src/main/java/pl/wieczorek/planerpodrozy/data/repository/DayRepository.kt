package pl.wieczorek.planerpodrozy.data.repository

import kotlinx.coroutines.flow.Flow
import pl.wieczorek.planerpodrozy.domain.model.DayDetail

/** Dostęp do pojedynczego dnia podróży (osobny, mały interfejs zamiast jednego „wszystkomającego”). */
interface DayRepository {
    /** Dzień ze zdjęciami; null, gdy dnia nie ma. */
    fun observeDay(dayId: Long): Flow<DayDetail?>
}
