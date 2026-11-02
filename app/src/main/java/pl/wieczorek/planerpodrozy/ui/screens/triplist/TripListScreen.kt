package pl.wieczorek.planerpodrozy.ui.screens.triplist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Luggage
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pl.wieczorek.planerpodrozy.R
import pl.wieczorek.planerpodrozy.domain.model.Trip
import pl.wieczorek.planerpodrozy.domain.model.TripSummary
import pl.wieczorek.planerpodrozy.ui.components.DeleteTripDialog
import pl.wieczorek.planerpodrozy.ui.components.TripCard
import pl.wieczorek.planerpodrozy.ui.format.DateFormatter
import pl.wieczorek.planerpodrozy.ui.planerViewModel
import pl.wieczorek.planerpodrozy.ui.theme.Dimens
import pl.wieczorek.planerpodrozy.ui.theme.PlanerPodrozyTheme
import java.time.Instant
import java.time.LocalDate

/** Ekran 01 podłączony do ViewModelu; nawigację przekazuje wyżej. */
@Composable
fun TripListRoute(
    onAddTrip: () -> Unit,
    onOpenTrip: (tripId: Long) -> Unit,
) {
    val viewModel = planerViewModel { TripListViewModel(it.tripRepository) }
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    TripListScreen(
        state = state,
        onAddTrip = onAddTrip,
        onOpenTrip = onOpenTrip,
        onSortChange = viewModel::onSortChange,
        onRequestDelete = viewModel::onRequestDelete,
        onConfirmDelete = viewModel::onConfirmDelete,
        onDismissDelete = viewModel::onDismissDelete,
    )
}

/** Ekran 01 bez stanu: rysuje to, co dostanie w [state]. */
@Composable
fun TripListScreen(
    state: TripListUiState,
    onAddTrip: () -> Unit,
    onOpenTrip: (tripId: Long) -> Unit,
    onSortChange: (SortOrder) -> Unit,
    onRequestDelete: (TripSummary) -> Unit,
    onConfirmDelete: () -> Unit,
    onDismissDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = { TripListHeader() },
        // „+” w prawym dolnym rogu, w zasięgu kciuka praworęcznej osoby.
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTrip,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.trip_list_add))
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier,
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceS, Alignment.End),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.ScreenPadding),
            ) {
                SortButton(
                    icon = Icons.Filled.KeyboardArrowUp,
                    contentDescription = stringResource(R.string.trip_list_sort_ascending),
                    active = state.sortOrder == SortOrder.ASCENDING,
                    onClick = { onSortChange(SortOrder.ASCENDING) },
                )
                SortButton(
                    icon = Icons.Filled.KeyboardArrowDown,
                    contentDescription = stringResource(R.string.trip_list_sort_descending),
                    active = state.sortOrder == SortOrder.DESCENDING,
                    onClick = { onSortChange(SortOrder.DESCENDING) },
                )
            }
            when {
                state.isLoading -> Unit
                state.trips.isEmpty() -> EmptyTripList()
                else -> TripList(trips = state.trips, onOpenTrip = onOpenTrip, onRequestDelete = onRequestDelete)
            }
        }
    }

    state.tripToDelete?.let { trip ->
        DeleteTripDialog(tripName = trip.trip.name, onConfirm = onConfirmDelete, onDismiss = onDismissDelete)
    }
}

@Composable
private fun TripList(
    trips: List<TripSummary>,
    onOpenTrip: (Long) -> Unit,
    onRequestDelete: (TripSummary) -> Unit,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(Dimens.SpaceM),
        // Dolny odstęp, żeby ostatniej karty nie zasłaniał przycisk „+”.
        contentPadding = PaddingValues(
            start = Dimens.ScreenPadding,
            end = Dimens.ScreenPadding,
            bottom = Dimens.FabClearance,
        ),
        modifier = Modifier.fillMaxSize(),
    ) {
        items(trips, key = { it.trip.id }) { summary ->
            SwipeToDeleteBox(onDelete = { onRequestDelete(summary) }) {
                TripCard(
                    name = summary.trip.name,
                    dateRange = DateFormatter.range(summary.trip.startDate, summary.trip.endDate),
                    days = summary.trip.dayCount,
                    coverPath = summary.coverPath,
                    onClick = { onOpenTrip(summary.trip.id) },
                )
            }
        }
    }
}

/**
 * Przesunięcie karty od prawej do lewej odsłania czerwone tło z koszem i wywołuje [onDelete].
 * Karta wraca na miejsce, bo o usunięciu decyduje dopiero dialog potwierdzenia.
 */
@Composable
private fun SwipeToDeleteBox(onDelete: () -> Unit, content: @Composable () -> Unit) {
    val state = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) onDelete()
            // false: nie zostawiamy karty „odsuniętej”, wraca na miejsce.
            false
        },
    )
    SwipeToDismissBox(
        state = state,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                contentAlignment = Alignment.CenterEnd,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.large)
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(horizontal = Dimens.SpaceXl),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = stringResource(R.string.cd_delete_trip),
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                )
            }
        },
    ) {
        content()
    }
}

/** Zaokrąglony liliowy nagłówek „Moje podróże” (w makiecie to nie jest standardowy pasek). */
@Composable
private fun TripListHeader() {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        shape = RoundedCornerShape(
            bottomStart = Dimens.SheetCornerRadius,
            bottomEnd = Dimens.SheetCornerRadius,
        ),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(Dimens.ListHeaderHeight),
        ) {
            Text(
                text = stringResource(R.string.trip_list_title),
                style = MaterialTheme.typography.titleLarge,
            )
        }
    }
}

/** Aktywny kierunek sortowania ma liliowe tło, nieaktywny jest bez tła. */
@Composable
private fun SortButton(
    icon: ImageVector,
    contentDescription: String,
    active: Boolean,
    onClick: () -> Unit,
) {
    if (active) {
        FilledTonalIconButton(
            onClick = onClick,
            colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
        ) {
            Icon(icon, contentDescription = contentDescription)
        }
    } else {
        IconButton(onClick = onClick) {
            Icon(icon, contentDescription = contentDescription, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun EmptyTripList() {
    Column(
        verticalArrangement = Arrangement.spacedBy(Dimens.SpaceS, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.SpaceXl),
    ) {
        Icon(
            imageVector = Icons.Outlined.Luggage,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(Dimens.SourceIconBoxSize),
        )
        Text(
            text = stringResource(R.string.trip_list_empty_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(R.string.trip_list_empty_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

private fun previewTrip(id: Long, name: String, start: LocalDate, days: Long) = TripSummary(
    trip = Trip(
        id = id,
        name = name,
        startDate = start,
        endDate = start.plusDays(days - 1),
        videoPath = null,
        voiceNotePath = null,
        createdAt = Instant.EPOCH,
    ),
    coverPath = null,
)

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun TripListScreenPreview() {
    PlanerPodrozyTheme {
        TripListScreen(
            state = TripListUiState(
                trips = listOf(
                    previewTrip(1, "Włochy 2026", LocalDate.of(2026, 7, 12), 7),
                    previewTrip(2, "Weekend w Tatrach", LocalDate.of(2026, 6, 19), 3),
                ),
                isLoading = false,
            ),
            onAddTrip = {},
            onOpenTrip = {},
            onSortChange = {},
            onRequestDelete = {},
            onConfirmDelete = {},
            onDismissDelete = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun TripListScreenEmptyPreview() {
    PlanerPodrozyTheme {
        TripListScreen(
            state = TripListUiState(isLoading = false),
            onAddTrip = {},
            onOpenTrip = {},
            onSortChange = {},
            onRequestDelete = {},
            onConfirmDelete = {},
            onDismissDelete = {},
        )
    }
}
