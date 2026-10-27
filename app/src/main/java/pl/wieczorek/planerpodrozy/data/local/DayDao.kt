package pl.wieczorek.planerpodrozy.data.local

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface DayDao {
    @Insert
    suspend fun insertAll(days: List<TripDayEntity>)
}
