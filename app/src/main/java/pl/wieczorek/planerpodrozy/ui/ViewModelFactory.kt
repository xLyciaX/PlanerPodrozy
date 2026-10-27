package pl.wieczorek.planerpodrozy.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.compose.viewModel
import pl.wieczorek.planerpodrozy.AppContainer
import pl.wieczorek.planerpodrozy.PlanerApp

/**
 * Tworzy (albo zwraca istniejący) ViewModel ekranu, podając mu zależności z [AppContainer].
 * Przykład: `val viewModel = planerViewModel { TripListViewModel(it.tripRepository) }`.
 */
@Composable
inline fun <reified VM : ViewModel> planerViewModel(crossinline create: (AppContainer) -> VM): VM =
    viewModel {
        val app = this[APPLICATION_KEY] as PlanerApp
        create(app.container)
    }
