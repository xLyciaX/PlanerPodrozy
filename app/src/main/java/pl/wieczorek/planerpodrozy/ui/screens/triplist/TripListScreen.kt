package pl.wieczorek.planerpodrozy.ui.screens.triplist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Luggage
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import pl.wieczorek.planerpodrozy.R
import pl.wieczorek.planerpodrozy.ui.theme.Dimens
import pl.wieczorek.planerpodrozy.ui.screens.SkeletonLink
import pl.wieczorek.planerpodrozy.ui.theme.PlanerPodrozyTheme

enum class SortOrder { ASCENDING, DESCENDING }

/**
 * Ekran 01. W etapie 1 pokazuje nagłówek, przyciski i pusty stan.
 * Lista kart podróży dojdzie w etapie 2, razem z bazą danych.
 */
@Composable
fun TripListScreen(
    sortOrder: SortOrder,
    onAddTrip: () -> Unit,
    onSortChange: (SortOrder) -> Unit,
    modifier: Modifier = Modifier,
    // TODO etap 2: usunąć, przejście do podróży będzie z karty na liście.
    onOpenSampleTrip: (() -> Unit)? = null,
) {
    Scaffold(
        topBar = { TripListHeader() },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier,
    ) { padding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(Dimens.SpaceM),
            modifier = Modifier
                .padding(padding)
                .padding(Dimens.ScreenPadding)
                .fillMaxSize(),
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                FilledIconButton(onClick = onAddTrip) {
                    Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.trip_list_add))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceS)) {
                    SortButton(
                        icon = Icons.Filled.KeyboardArrowUp,
                        contentDescription = stringResource(R.string.trip_list_sort_ascending),
                        active = sortOrder == SortOrder.ASCENDING,
                        onClick = { onSortChange(SortOrder.ASCENDING) },
                    )
                    SortButton(
                        icon = Icons.Filled.KeyboardArrowDown,
                        contentDescription = stringResource(R.string.trip_list_sort_descending),
                        active = sortOrder == SortOrder.DESCENDING,
                        onClick = { onSortChange(SortOrder.DESCENDING) },
                    )
                }
            }
            EmptyTripList(onOpenSampleTrip)
        }
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
private fun EmptyTripList(onOpenSampleTrip: (() -> Unit)?) {
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
        onOpenSampleTrip?.let { SkeletonLink("Widok podróży", it) }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun TripListScreenPreview() {
    PlanerPodrozyTheme {
        TripListScreen(sortOrder = SortOrder.DESCENDING, onAddTrip = {}, onSortChange = {})
    }
}
