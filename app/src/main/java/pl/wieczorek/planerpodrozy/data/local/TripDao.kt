package pl.wieczorek.planerpodrozy.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {
    @Insert
    suspend fun insert(trip: TripEntity): Long

    @Query("SELECT * FROM trips")
    fun observeAll(): Flow<List<TripEntity>>

    @Query("SELECT * FROM trips WHERE id = :tripId")
    fun observeById(tripId: Long): Flow<TripEntity?>

    @Query("SELECT * FROM trips WHERE id = :tripId")
    suspend fun findById(tripId: Long): TripEntity?

    /** Dni i zdjęcia podróży znikają razem z nią (klucze obce z CASCADE). */
    @Query("DELETE FROM trips WHERE id = :tripId")
    suspend fun deleteById(tripId: Long)

    @Query("UPDATE trips SET videoPath = :videoPath WHERE id = :tripId")
    suspend fun updateVideo(tripId: Long, videoPath: String)

    @Query("UPDATE trips SET voiceNotePath = :voiceNotePath WHERE id = :tripId")
    suspend fun updateVoiceNote(tripId: Long, voiceNotePath: String)
}
