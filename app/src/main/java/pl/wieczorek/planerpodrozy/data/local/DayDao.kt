package pl.wieczorek.planerpodrozy.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DayDao {
    @Insert
    suspend fun insertAll(days: List<TripDayEntity>)

    @Query("SELECT * FROM trip_days WHERE tripId = :tripId ORDER BY dayNumber")
    fun observeByTrip(tripId: Long): Flow<List<TripDayEntity>>

    @Query("SELECT * FROM trip_days WHERE id = :dayId")
    fun observeById(dayId: Long): Flow<TripDayEntity?>

    @Query("UPDATE trip_days SET description = :description, locations = :locations WHERE id = :dayId")
    suspend fun updateDetails(dayId: Long, description: String, locations: List<String>)
}
