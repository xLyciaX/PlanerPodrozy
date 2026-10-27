package pl.wieczorek.planerpodrozy.ui.navigation

import kotlinx.serialization.Serializable

// Typowane trasy nawigacji: każdy ekran to klasa, a jej pola to argumenty.
// Dzięki temu kompilator pilnuje, że do ekranu dnia zawsze przekażemy dayId.

@Serializable
object TripListRoute

@Serializable
object NewTripRoute

@Serializable
data class TripDetailRoute(val tripId: Long)

@Serializable
data class DayRoute(val dayId: Long)

@Serializable
data class DayEditRoute(val dayId: Long)

@Serializable
data class GalleryRoute(val tripId: Long)
