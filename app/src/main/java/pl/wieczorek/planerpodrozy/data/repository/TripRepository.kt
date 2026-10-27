package pl.wieczorek.planerpodrozy.data.repository

import kotlinx.coroutines.flow.Flow
import pl.wieczorek.planerpodrozy.domain.model.TripSummary
import java.time.LocalDate

/** Dostęp do podróży. ViewModele znają tylko ten interfejs, a nie Room. */
interface TripRepository {
    /** Wszystkie podróże z okładkami; nowa wartość po każdej zmianie w bazie. */
    fun observeTrips(): Flow<List<TripSummary>>

    /** Zapisuje podróż i od razu tworzy jej dni (po jednym na datę). Zwraca id podróży. */
    suspend fun createTrip(name: String, startDate: LocalDate, endDate: LocalDate): Long
}
