package pl.wieczorek.planerpodrozy.data.local

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import pl.wieczorek.planerpodrozy.domain.model.Photo
import pl.wieczorek.planerpodrozy.domain.model.PhotoCategory
import pl.wieczorek.planerpodrozy.domain.model.Trip
import pl.wieczorek.planerpodrozy.domain.model.TripDay
import java.time.Instant
import java.time.LocalDate

// Tabele bazy Room. Klasy *Entity znają tylko warstwę data; reszta aplikacji
// dostaje modele z domain/model (konwersja funkcjami toDomain() poniżej).

@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val videoPath: String? = null,
    val voiceNotePath: String? = null,
    val createdAt: Instant,
)

/** Usunięcie podróży usuwa jej dni (CASCADE). */
@Entity(
    tableName = "trip_days",
    foreignKeys = [
        ForeignKey(
            entity = TripEntity::class,
            parentColumns = ["id"],
            childColumns = ["tripId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("tripId")],
)
data class TripDayEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tripId: Long,
    val dayNumber: Int,
    val date: LocalDate,
    val description: String = "",
    val locations: List<String> = emptyList(),
)

/**
 * Unikalny indeks (dayId, category) pilnuje reguły „jedno zdjęcie na kategorię w dniu”
 * już na poziomie bazy: drugiego zdjęcia w tej samej kategorii nie da się zapisać.
 */
@Entity(
    tableName = "photos",
    foreignKeys = [
        ForeignKey(
            entity = TripDayEntity::class,
            parentColumns = ["id"],
            childColumns = ["dayId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["dayId", "category"], unique = true)],
)
data class PhotoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dayId: Long,
    val category: PhotoCategory,
    val filePath: String,
    val addedAt: Instant,
)

/** Zdjęcie razem z id podróży (wynik złączenia photos z trip_days). */
data class PhotoWithTripId(
    @Embedded val photo: PhotoEntity,
    @ColumnInfo(name = "tripId") val tripId: Long,
)

fun TripEntity.toDomain() = Trip(
    id = id,
    name = name,
    startDate = startDate,
    endDate = endDate,
    videoPath = videoPath,
    voiceNotePath = voiceNotePath,
    createdAt = createdAt,
)

fun TripDayEntity.toDomain() = TripDay(
    id = id,
    tripId = tripId,
    dayNumber = dayNumber,
    date = date,
    description = description,
    locations = locations,
)

fun PhotoEntity.toDomain() = Photo(
    id = id,
    dayId = dayId,
    category = category,
    filePath = filePath,
    addedAt = addedAt,
)
