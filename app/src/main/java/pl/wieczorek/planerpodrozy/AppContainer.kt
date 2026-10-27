package pl.wieczorek.planerpodrozy

import android.content.Context
import pl.wieczorek.planerpodrozy.data.local.AppDatabase
import pl.wieczorek.planerpodrozy.data.repository.RoomTripRepository
import pl.wieczorek.planerpodrozy.data.repository.TripRepository

/**
 * Ręczne wstrzykiwanie zależności: jedno miejsce, które tworzy bazę i repozytoria.
 * ViewModele dostają stąd gotowe obiekty przez konstruktor.
 */
class AppContainer(context: Context) {
    private val database = AppDatabase.build(context)

    val tripRepository: TripRepository = RoomTripRepository(database)
}
