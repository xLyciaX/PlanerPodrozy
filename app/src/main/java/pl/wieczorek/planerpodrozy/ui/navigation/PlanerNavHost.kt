package pl.wieczorek.planerpodrozy.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import pl.wieczorek.planerpodrozy.R
import pl.wieczorek.planerpodrozy.ui.screens.SkeletonScreen
import pl.wieczorek.planerpodrozy.ui.screens.day.DayRoute
import pl.wieczorek.planerpodrozy.ui.screens.dayedit.DayEditRoute
import pl.wieczorek.planerpodrozy.ui.screens.newtrip.NewTripRoute
import pl.wieczorek.planerpodrozy.ui.screens.tripdetail.TripDetailRoute
import pl.wieczorek.planerpodrozy.ui.screens.triplist.TripListRoute

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
        composable<DayEditDestination> { entry ->
            val destination = entry.toRoute<DayEditDestination>()
            DayEditRoute(dayId = destination.dayId, onClose = goBack)
        }
        composable<GalleryDestination> {
            SkeletonScreen(
                title = stringResource(R.string.gallery_title, "…"),
                onBack = goBack,
                centeredTitle = false,
            )
        }
    }
}
