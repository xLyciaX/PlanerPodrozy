package pl.wieczorek.planerpodrozy.ui.navigation

import kotlinx.serialization.Serializable

// Typowane trasy nawigacji (cele, „destinations”): każdy ekran to klasa, a jej pola to argumenty.
// Nazwy kończą się na Destination, bo słowo Route mają composable XxxRoute z ViewModelem.
// Dzięki temu kompilator pilnuje, że do ekranu dnia zawsze przekażemy dayId.

@Serializable
object TripListDestination

@Serializable
object NewTripDestination

@Serializable
data class TripDetailDestination(val tripId: Long)

@Serializable
data class DayDestination(val dayId: Long)

@Serializable
data class DayEditDestination(val dayId: Long)

@Serializable
data class GalleryDestination(val tripId: Long)
