package pl.wieczorek.planerpodrozy.domain.model

/** Kategoria kadru. Każdy dzień ma dokładnie jedno miejsce na zdjęcie z każdej kategorii. */
enum class PhotoCategory {
    LANDSCAPE,
    FOOD,
    SELFIE;

    companion object {
        /** Maksymalna liczba zdjęć w jednym dniu (po jednym na kategorię). */
        val PhotosPerDay: Int = entries.size
    }
}
