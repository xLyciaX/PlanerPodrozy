package pl.wieczorek.planerpodrozy

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.video.VideoFrameDecoder

/**
 * Klasa aplikacji: żyje przez cały czas działania i trzyma [AppContainer].
 * Konfiguruje też Coil, żeby umiał pokazać miniaturę filmu (pierwszą klatkę).
 */
class PlanerApp : Application(), SingletonImageLoader.Factory {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }

    // Coil pyta o ten loader przy pierwszym AsyncImage. VideoFrameDecoder dekoduje klatkę z pliku wideo
    // (domyślnie pierwszą), więc AsyncImage(model = File("film.mp4")) pokazuje miniaturę.
    override fun newImageLoader(context: PlatformContext): ImageLoader =
        ImageLoader.Builder(context)
            .components { add(VideoFrameDecoder.Factory()) }
            .build()
}
