package pl.wieczorek.planerpodrozy.data.repository

import kotlinx.coroutines.flow.Flow
import pl.wieczorek.planerpodrozy.domain.model.TripDetail
import pl.wieczorek.planerpodrozy.domain.model.TripGallery
import pl.wieczorek.planerpodrozy.domain.model.TripSummary
import java.time.LocalDate

/** Dostęp do podróży. ViewModele znają tylko ten interfejs, a nie Room. */
interface TripRepository {
    /** Wszystkie podróże z okładkami; nowa wartość po każdej zmianie w bazie. */
    fun observeTrips(): Flow<List<TripSummary>>

    /** Podróż z dniami i okładkami; null, gdy podróży nie ma. */
    fun observeTripDetail(tripId: Long): Flow<TripDetail?>

    /** Nazwa podróży i wszystkie jej zdjęcia; null, gdy podróży nie ma. */
    fun observeGallery(tripId: Long): Flow<TripGallery?>

    /** Zapisuje podróż i od razu tworzy jej dni (po jednym na datę). Zwraca id podróży. */
    /** Ustawia film podróży; poprzedni film (i jego plik) jest zastępowany. */
    suspend fun setVideo(tripId: Long, filePath: String)

    /** Ustawia notatkę głosową podróży; poprzednia notatka (i jej plik) jest zastępowana. */
    suspend fun setVoiceNote(tripId: Long, filePath: String)

    /** Usuwa podróż razem z dniami, zdjęciami, filmem i notatką głosową (także z plikami). */
    suspend fun deleteTrip(tripId: Long)

    suspend fun createTrip(name: String, startDate: LocalDate, endDate: LocalDate): Long
}
