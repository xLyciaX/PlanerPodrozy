package pl.wieczorek.planerpodrozy.ui.screens.tripdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pl.wieczorek.planerpodrozy.R
import pl.wieczorek.planerpodrozy.domain.model.DaySummary
import pl.wieczorek.planerpodrozy.domain.model.Trip
import pl.wieczorek.planerpodrozy.domain.model.TripDay
import pl.wieczorek.planerpodrozy.domain.model.TripDetail
import pl.wieczorek.planerpodrozy.ui.components.DayTile
import pl.wieczorek.planerpodrozy.ui.components.DurationPill
import pl.wieczorek.planerpodrozy.ui.components.EmptyMediaCard
import pl.wieczorek.planerpodrozy.ui.components.PlanerTopAppBar
import pl.wieczorek.planerpodrozy.ui.components.SectionLabel
import pl.wieczorek.planerpodrozy.ui.components.TripCoverImage
import pl.wieczorek.planerpodrozy.ui.format.DateFormatter
import pl.wieczorek.planerpodrozy.ui.planerViewModel
import pl.wieczorek.planerpodrozy.ui.theme.Dimens
import pl.wieczorek.planerpodrozy.ui.theme.PlanerPodrozyTheme
import java.time.Instant
import java.time.LocalDate

/** Ekran 04 podłączony do ViewModelu. */
@Composable
fun TripDetailRoute(
    tripId: Long,
    onBack: () -> Unit,
    onOpenGallery: () -> Unit,
    onOpenDay: (dayId: Long) -> Unit,
) {
    val viewModel = planerViewModel { TripDetailViewModel(tripId, it.tripRepository) }
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    TripDetailScreen(
        state = state,
        onBack = onBack,
        onOpenGallery = onOpenGallery,
        onOpenDay = onOpenDay,
    )
}

/** Ekran 04: przewijany widok podróży (multimedia dojdą w etapie 7). */
@Composable
fun TripDetailScreen(
    state: TripDetailUiState,
    onBack: () -> Unit,
    onOpenGallery: () -> Unit,
    onOpenDay: (dayId: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val detail = state.detail
    Scaffold(
        topBar = { PlanerTopAppBar(title = detail?.trip?.name.orEmpty(), onBack = onBack) },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier,
    ) { padding ->
        if (detail == null) return@Scaffold
        Column(
            verticalArrangement = Arrangement.spacedBy(Dimens.SpaceM),
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(bottom = Dimens.SpaceXl),
        ) {
            TripBanner(detail)
            Button(
                onClick = onOpenGallery,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = Dimens.ScreenPadding),
            ) {
                Text(stringResource(R.string.trip_detail_gallery))
            }
            SectionLabel(
                text = stringResource(R.string.trip_detail_days),
                modifier = Modifier.padding(horizontal = Dimens.ScreenPadding),
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceS),
                contentPadding = PaddingValues(horizontal = Dimens.ScreenPadding),
            ) {
                items(detail.days, key = { it.day.id }) { summary ->
                    DayTile(
                        dayNumber = summary.day.dayNumber,
                        date = DateFormatter.dayMonth(summary.day.date),
                        coverPath = summary.coverPath,
                        photoCount = summary.photoCount,
                        onClick = { onOpenDay(summary.day.id) },
                    )
                }
            }
            SectionLabel(
                text = stringResource(R.string.trip_detail_media),
                modifier = Modifier.padding(horizontal = Dimens.ScreenPadding),
            )
            // TODO etap 7: odtwarzacze i przycisk „+”, gdy film lub notatka istnieją.
            Column(
                verticalArrangement = Arrangement.spacedBy(Dimens.SpaceM),
                modifier = Modifier.padding(horizontal = Dimens.ScreenPadding),
            ) {
                EmptyMediaCard(
                    icon = Icons.Outlined.Videocam,
                    title = stringResource(R.string.media_video_title),
                    message = stringResource(R.string.media_video_empty),
                )
                EmptyMediaCard(
                    icon = Icons.Outlined.Mic,
                    title = stringResource(R.string.media_voice_title),
                    message = stringResource(R.string.media_voice_empty),
                )
            }
        }
    }
}

/** Baner: okładka podróży, a pod nią biały pasek z nazwą, terminem i liczbą dni. */
@Composable
private fun TripBanner(detail: TripDetail) {
    Column {
        TripCoverImage(
            path = detail.coverPath,
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.TripBannerHeight),
        )
        Surface(color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxWidth()) {
            Column(
                verticalArrangement = Arrangement.spacedBy(Dimens.SpaceXs),
                modifier = Modifier.padding(Dimens.ScreenPadding),
            ) {
                Text(
                    text = detail.trip.name,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarMonth,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(Dimens.IconS),
                    )
                    Text(
                        text = DateFormatter.range(detail.trip.startDate, detail.trip.endDate),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = Dimens.SpaceS),
                    )
                    DurationPill(
                        days = detail.trip.dayCount,
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}

private fun previewDetail(): TripDetail {
    val start = LocalDate.of(2026, 7, 12)
    val trip = Trip(1, "Włochy 2026", start, start.plusDays(6), null, null, Instant.EPOCH)
    return TripDetail(
        trip = trip,
        coverPath = null,
        days = (0 until 7).map { index ->
            DaySummary(
                day = TripDay(index.toLong(), 1, index + 1, start.plusDays(index.toLong()), "", emptyList()),
                coverPath = null,
                photoCount = if (index < 3) 2 else 0,
            )
        },
    )
}

@Preview(showBackground = true, widthDp = 360, heightDp = 1020)
@Composable
private fun TripDetailScreenPreview() {
    PlanerPodrozyTheme {
        TripDetailScreen(
            state = TripDetailUiState(detail = previewDetail(), isLoading = false),
            onBack = {},
            onOpenGallery = {},
            onOpenDay = {},
        )
    }
}
