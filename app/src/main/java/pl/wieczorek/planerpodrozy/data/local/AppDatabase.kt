package pl.wieczorek.planerpodrozy.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

// exportSchema = false: nie przechowujemy historii schematu w repozytorium,
// bo to pierwsza wersja bazy i nie planujemy migracji.
@Database(
    entities = [TripEntity::class, TripDayEntity::class, PhotoEntity::class],
    version = 1,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
    abstract fun dayDao(): DayDao
    abstract fun photoDao(): PhotoDao

    companion object {
        fun build(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "planer_podrozy.db").build()
    }
}
