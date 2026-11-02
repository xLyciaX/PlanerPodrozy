package pl.wieczorek.planerpodrozy.data.repository

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import pl.wieczorek.planerpodrozy.data.local.AppDatabase
import pl.wieczorek.planerpodrozy.data.local.TripDayEntity
import pl.wieczorek.planerpodrozy.data.local.TripEntity
import pl.wieczorek.planerpodrozy.data.local.toDomain
import pl.wieczorek.planerpodrozy.data.media.MediaStorage
import pl.wieczorek.planerpodrozy.domain.coverPhoto
import pl.wieczorek.planerpodrozy.domain.model.DaySummary
import pl.wieczorek.planerpodrozy.domain.model.TripDetail
import pl.wieczorek.planerpodrozy.domain.model.TripGallery
import pl.wieczorek.planerpodrozy.domain.model.TripSummary
import pl.wieczorek.planerpodrozy.domain.tripDates
import java.time.Instant
import java.time.LocalDate

class RoomTripRepository(
    private val database: AppDatabase,
    private val mediaStorage: MediaStorage,
) : TripRepository {

    private val tripDao = database.tripDao()
    private val dayDao = database.dayDao()
    private val photoDao = database.photoDao()

    override fun observeTrips(): Flow<List<TripSummary>> =
        combine(tripDao.observeAll(), photoDao.observeAllWithTripId()) { trips, photos ->
            val photosByTrip = photos.groupBy({ it.tripId }, { it.photo.toDomain() })
            trips.map { trip ->
                TripSummary(
                    trip = trip.toDomain(),
                    coverPath = coverPhoto(photosByTrip[trip.id].orEmpty())?.filePath,
                )
            }
        }

    override fun observeTripDetail(tripId: Long): Flow<TripDetail?> =
        combine(
            tripDao.observeById(tripId),
            dayDao.observeByTrip(tripId),
            photoDao.observeByTrip(tripId),
        ) { trip, days, photos ->
            if (trip == null) return@combine null
            val tripPhotos = photos.map { it.toDomain() }
            val photosByDay = tripPhotos.groupBy { it.dayId }
            TripDetail(
                trip = trip.toDomain(),
                // Okładka podróży: najwcześniej dodane zdjęcie z całej podróży.
                coverPath = coverPhoto(tripPhotos)?.filePath,
                days = days.map { day ->
                    val dayPhotos = photosByDay[day.id].orEmpty()
                    DaySummary(
                        day = day.toDomain(),
                        // Okładka dnia: najwcześniej dodane zdjęcie tego dnia.
                        coverPath = coverPhoto(dayPhotos)?.filePath,
                        photoCount = dayPhotos.size,
                    )
                },
            )
        }

    override fun observeGallery(tripId: Long): Flow<TripGallery?> =
        combine(tripDao.observeById(tripId), photoDao.observeByTrip(tripId)) { trip, photos ->
            trip?.let { TripGallery(tripName = it.name, photos = photos.map { photo -> photo.toDomain() }) }
        }

    override suspend fun setVideo(tripId: Long, filePath: String) {
        val previous = tripDao.findById(tripId)?.videoPath
        tripDao.updateVideo(tripId, filePath)
        if (previous != null && previous != filePath) mediaStorage.delete(previous)
    }

    override suspend fun setVoiceNote(tripId: Long, filePath: String) {
        val previous = tripDao.findById(tripId)?.voiceNotePath
        tripDao.updateVoiceNote(tripId, filePath)
        if (previous != null && previous != filePath) mediaStorage.delete(previous)
    }

    // Transakcja: albo zapisze się podróż razem ze wszystkimi dniami, albo nic.
    override suspend fun createTrip(name: String, startDate: LocalDate, endDate: LocalDate): Long =
        database.withTransaction {
            val tripId = tripDao.insert(
                TripEntity(
                    name = name,
                    startDate = startDate,
                    endDate = endDate,
                    createdAt = Instant.now(),
                ),
            )
            val days = tripDates(startDate, endDate).mapIndexed { index, date ->
                TripDayEntity(tripId = tripId, dayNumber = index + 1, date = date)
            }
            dayDao.insertAll(days)
            tripId
        }
}
