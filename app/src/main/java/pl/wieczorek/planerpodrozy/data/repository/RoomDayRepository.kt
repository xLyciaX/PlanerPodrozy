package pl.wieczorek.planerpodrozy.data.repository

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import pl.wieczorek.planerpodrozy.data.local.AppDatabase
import pl.wieczorek.planerpodrozy.data.local.PhotoEntity
import pl.wieczorek.planerpodrozy.data.local.toDomain
import pl.wieczorek.planerpodrozy.data.media.MediaStorage
import pl.wieczorek.planerpodrozy.domain.model.DayDetail
import pl.wieczorek.planerpodrozy.domain.model.PhotoCategory
import java.time.Instant

class RoomDayRepository(
    private val database: AppDatabase,
    private val mediaStorage: MediaStorage,
) : DayRepository {

    private val dayDao = database.dayDao()
    private val photoDao = database.photoDao()

    override fun observeDay(dayId: Long): Flow<DayDetail?> =
        combine(dayDao.observeById(dayId), photoDao.observeByDay(dayId)) { day, photos ->
            day?.let {
                DayDetail(
                    day = it.toDomain(),
                    // Unikalny indeks (dayId, category) gwarantuje najwyżej jedno zdjęcie na kategorię.
                    photos = photos.map { photo -> photo.toDomain() }.associateBy { photo -> photo.category },
                )
            }
        }

    override suspend fun setPhoto(dayId: Long, category: PhotoCategory, filePath: String) {
        // W transakcji: usuń wpis poprzedniego zdjęcia z tej kategorii i dodaj nowy.
        val replaced = database.withTransaction {
            val previous = photoDao.find(dayId, category)
            if (previous != null) photoDao.delete(previous)
            photoDao.insert(
                PhotoEntity(dayId = dayId, category = category, filePath = filePath, addedAt = Instant.now()),
            )
            previous
        }
        // Plik usuwamy dopiero po udanym zapisie w bazie.
        if (replaced != null && replaced.filePath != filePath) mediaStorage.delete(replaced.filePath)
    }
}
