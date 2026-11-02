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
}
