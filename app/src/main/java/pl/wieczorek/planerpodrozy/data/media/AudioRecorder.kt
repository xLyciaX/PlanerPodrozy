package pl.wieczorek.planerpodrozy.data.media

/** Nagrywanie dźwięku z mikrofonu do pliku. ViewModel zna tylko ten interfejs, nie MediaRecorder. */
interface AudioRecorder {
    /** Zaczyna nagrywanie do pliku [outputPath]. Rzuca wyjątek, gdy mikrofon jest niedostępny. */
    fun start(outputPath: String)

    /** Kończy nagrywanie i zamyka plik. Rzuca wyjątek, gdy nagranie jest puste (np. za krótkie). */
    fun stop()

    /** Przerywa nagrywanie bez zachowania wyniku. */
    fun cancel()
}
