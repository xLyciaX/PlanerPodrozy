package pl.wieczorek.planerpodrozy.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import pl.wieczorek.planerpodrozy.R
import pl.wieczorek.planerpodrozy.ui.screens.SkeletonScreen
import pl.wieczorek.planerpodrozy.ui.screens.newtrip.NewTripRoute
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
            val route = entry.toRoute<TripDetailDestination>()
            SkeletonScreen(
                title = "Podróż ${route.tripId}",
                onBack = goBack,
                links = listOf(
                    "Dzień 1" to { navController.navigate(DayDestination(dayId = 1)) },
                    "Galeria" to { navController.navigate(GalleryDestination(tripId = route.tripId)) },
                ),
            )
        }
        composable<DayDestination> { entry ->
            val route = entry.toRoute<DayDestination>()
            SkeletonScreen(
                title = stringResource(R.string.day_number, route.dayId.toInt()),
                onBack = goBack,
                links = listOf("Edycja dnia" to { navController.navigate(DayEditDestination(route.dayId)) }),
            )
        }
        composable<DayEditDestination> {
            SkeletonScreen(title = stringResource(R.string.day_edit_title), onBack = goBack)
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
