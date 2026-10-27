package pl.wieczorek.planerpodrozy

import android.app.Application

/** Klasa aplikacji: żyje przez cały czas działania i trzyma [AppContainer]. */
class PlanerApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
