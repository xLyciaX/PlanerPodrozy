package pl.wieczorek.planerpodrozy.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import pl.wieczorek.planerpodrozy.R
import pl.wieczorek.planerpodrozy.ui.screens.SkeletonScreen
import pl.wieczorek.planerpodrozy.ui.screens.triplist.SortOrder
import pl.wieczorek.planerpodrozy.ui.screens.triplist.TripListScreen

/** Mapa ekranów aplikacji: która trasa pokazuje który ekran i dokąd z niego można przejść. */
@Composable
fun PlanerNavHost() {
    val navController = rememberNavController()
    val goBack: () -> Unit = { navController.popBackStack() }

    NavHost(navController = navController, startDestination = TripListRoute) {
        composable<TripListRoute> {
            // TODO etap 2: kierunek sortowania przeniesie się do TripListViewModel.
            var sortOrder by rememberSaveable { mutableStateOf(SortOrder.DESCENDING) }
            TripListScreen(
                sortOrder = sortOrder,
                onAddTrip = { navController.navigate(NewTripRoute) },
                onSortChange = { sortOrder = it },
                onOpenSampleTrip = { navController.navigate(TripDetailRoute(tripId = 1)) },
            )
        }
        composable<NewTripRoute> {
            SkeletonScreen(
                title = stringResource(R.string.new_trip_title),
                onBack = goBack,
            )
        }
        composable<TripDetailRoute> { entry ->
            val route = entry.toRoute<TripDetailRoute>()
            SkeletonScreen(
                title = "Podróż ${route.tripId}",
                onBack = goBack,
                links = listOf(
                    "Dzień 1" to { navController.navigate(DayRoute(dayId = 1)) },
                    "Galeria" to { navController.navigate(GalleryRoute(tripId = route.tripId)) },
                ),
            )
        }
        composable<DayRoute> { entry ->
            val route = entry.toRoute<DayRoute>()
            SkeletonScreen(
                title = stringResource(R.string.day_number, route.dayId.toInt()),
                onBack = goBack,
                links = listOf("Edycja dnia" to { navController.navigate(DayEditRoute(route.dayId)) }),
            )
        }
        composable<DayEditRoute> {
            SkeletonScreen(title = stringResource(R.string.day_edit_title), onBack = goBack)
        }
        composable<GalleryRoute> {
            SkeletonScreen(
                title = stringResource(R.string.gallery_title, "…"),
                onBack = goBack,
                centeredTitle = false,
            )
        }
    }
}
