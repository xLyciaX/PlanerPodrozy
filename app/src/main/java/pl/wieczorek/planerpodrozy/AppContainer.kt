package pl.wieczorek.planerpodrozy

import android.content.Context
import pl.wieczorek.planerpodrozy.data.local.AppDatabase
import pl.wieczorek.planerpodrozy.data.media.FileMediaStorage
import pl.wieczorek.planerpodrozy.data.media.MediaStorage
import pl.wieczorek.planerpodrozy.data.repository.DayRepository
import pl.wieczorek.planerpodrozy.data.repository.RoomDayRepository
import pl.wieczorek.planerpodrozy.data.repository.RoomTripRepository
import pl.wieczorek.planerpodrozy.data.repository.TripRepository

/**
 * Ręczne wstrzykiwanie zależności: jedno miejsce, które tworzy bazę i repozytoria.
 * ViewModele dostają stąd gotowe obiekty przez konstruktor.
 */
class AppContainer(context: Context) {
    private val database = AppDatabase.build(context)

    val mediaStorage: MediaStorage = FileMediaStorage(context)

    val tripRepository: TripRepository = RoomTripRepository(database)
    val dayRepository: DayRepository = RoomDayRepository(database, mediaStorage)
}
