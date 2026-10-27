package pl.wieczorek.planerpodrozy.data.local

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {
    /** Wszystkie zdjęcia z id podróży, do której należą (potrzebne do okładek na liście). */
    @Query(
        """
        SELECT photos.*, trip_days.tripId AS tripId
        FROM photos
        INNER JOIN trip_days ON trip_days.id = photos.dayId
        """,
    )
    fun observeAllWithTripId(): Flow<List<PhotoWithTripId>>
}
