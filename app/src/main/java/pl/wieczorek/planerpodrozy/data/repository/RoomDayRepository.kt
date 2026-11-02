package pl.wieczorek.planerpodrozy.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import pl.wieczorek.planerpodrozy.data.local.AppDatabase
import pl.wieczorek.planerpodrozy.data.local.toDomain
import pl.wieczorek.planerpodrozy.domain.model.DayDetail

class RoomDayRepository(database: AppDatabase) : DayRepository {

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
}
