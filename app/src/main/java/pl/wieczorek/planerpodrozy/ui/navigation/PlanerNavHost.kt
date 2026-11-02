package pl.wieczorek.planerpodrozy.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import pl.wieczorek.planerpodrozy.ui.screens.day.DayRoute
import pl.wieczorek.planerpodrozy.ui.screens.dayedit.DayEditRoute
import pl.wieczorek.planerpodrozy.ui.screens.gallery.GalleryRoute
import pl.wieczorek.planerpodrozy.ui.screens.newtrip.NewTripRoute
import pl.wieczorek.planerpodrozy.ui.screens.tripdetail.TripDetailRoute
import pl.wieczorek.planerpodrozy.ui.screens.triplist.TripListRoute

private const val EditTransitionMillis = 300

/** Mapa ekranów aplikacji: która trasa pokazuje który ekran i dokąd z niego można przejść. */
@Composable
fun PlanerNavHost() {
    val navController = rememberNavController()
    val goBack: () -> Unit = { navController.popBackStack() }

    NavHost(navController = navController, startDestination = TripListDestination) {
        composable<TripListDestination> {
            TripListRoute(
                onAddTrip = { navController.navigate(NewTripDestination) },
                onOpenTrip = { tripId -> navController.navigate(TripDetailDestination(tripId)) },
            )
        }
        composable<NewTripDestination> {
            NewTripRoute(onClose = goBack)
        }
        composable<TripDetailDestination> { entry ->
            val destination = entry.toRoute<TripDetailDestination>()
            TripDetailRoute(
                tripId = destination.tripId,
                onBack = goBack,
                onOpenGallery = { navController.navigate(GalleryDestination(destination.tripId)) },
                onOpenDay = { dayId -> navController.navigate(DayDestination(dayId)) },
            )
        }
        composable<DayDestination> { entry ->
            val destination = entry.toRoute<DayDestination>()
            DayRoute(
                dayId = destination.dayId,
                onBack = goBack,
                onEdit = { navController.navigate(DayEditDestination(destination.dayId)) },
            )
        }
        // Edycja dnia wysuwa się od dołu nad widok dnia i chowa w dół przy powrocie (05 ↔ 05b).
        composable<DayEditDestination>(
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, tween(EditTransitionMillis)) +
                    fadeIn(tween(EditTransitionMillis))
            },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, tween(EditTransitionMillis)) +
                    fadeOut(tween(EditTransitionMillis))
            },
        ) { entry ->
            val destination = entry.toRoute<DayEditDestination>()
            DayEditRoute(dayId = destination.dayId, onClose = goBack)
        }
        composable<GalleryDestination> { entry ->
            val destination = entry.toRoute<GalleryDestination>()
            GalleryRoute(tripId = destination.tripId, onBack = goBack)
        }
    }
}
