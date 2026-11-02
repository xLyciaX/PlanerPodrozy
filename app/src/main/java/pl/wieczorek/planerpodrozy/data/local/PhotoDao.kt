package pl.wieczorek.planerpodrozy.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import pl.wieczorek.planerpodrozy.domain.model.PhotoCategory

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

    /** Wszystkie zdjęcia jednej podróży (ze wszystkich jej dni), po kolei: dzień, potem czas dodania. */
    @Query(
        """
        SELECT photos.* FROM photos
        INNER JOIN trip_days ON trip_days.id = photos.dayId
        WHERE trip_days.tripId = :tripId
        ORDER BY trip_days.dayNumber, photos.addedAt
        """,
    )
    fun observeByTrip(tripId: Long): Flow<List<PhotoEntity>>

    @Query("SELECT * FROM photos WHERE dayId = :dayId")
    fun observeByDay(dayId: Long): Flow<List<PhotoEntity>>

    /** Ścieżki plików wszystkich zdjęć podróży (do usunięcia razem z podróżą). */
    @Query(
        """
        SELECT photos.filePath FROM photos
        INNER JOIN trip_days ON trip_days.id = photos.dayId
        WHERE trip_days.tripId = :tripId
        """,
    )
    suspend fun findFilePathsByTrip(tripId: Long): List<String>

    @Query("SELECT * FROM photos WHERE dayId = :dayId")
    suspend fun findByDay(dayId: Long): List<PhotoEntity>

    @Query("SELECT * FROM photos WHERE dayId = :dayId AND category = :category")
    suspend fun find(dayId: Long, category: PhotoCategory): PhotoEntity?

    @Insert
    suspend fun insert(photo: PhotoEntity): Long

    @Delete
    suspend fun delete(photo: PhotoEntity)
}
