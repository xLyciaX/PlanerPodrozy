package pl.wieczorek.planerpodrozy.domain.model

/** Dzień na kafelku w widoku podróży: okładka dnia (null = puste tło) i liczba zdjęć. */
data class DaySummary(
    val day: TripDay,
    val coverPath: String?,
    val photoCount: Int,
)

/** Wszystko, co pokazuje widok podróży (ekran 04). */
data class TripDetail(
    val trip: Trip,
    val coverPath: String?,
    val days: List<DaySummary>,
)

/** Zdjęcia całej podróży do galerii (ekran 07). */
data class TripGallery(
    val tripName: String,
    val photos: List<Photo>,
)

/** Dzień ze zdjęciami przypisanymi do kategorii (najwyżej jedno na kategorię). */
data class DayDetail(
    val day: TripDay,
    val photos: Map<PhotoCategory, Photo>,
)
