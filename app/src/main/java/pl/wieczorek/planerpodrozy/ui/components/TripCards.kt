package pl.wieczorek.planerpodrozy.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pl.wieczorek.planerpodrozy.R
import pl.wieczorek.planerpodrozy.domain.model.PhotoCategory
import pl.wieczorek.planerpodrozy.ui.theme.Dimens
import pl.wieczorek.planerpodrozy.ui.theme.PlanerPodrozyTheme
import pl.wieczorek.planerpodrozy.ui.theme.caption
import pl.wieczorek.planerpodrozy.ui.theme.captionSmall

/** Karta podróży na liście: okładka, nazwa, termin i pigułka z liczbą dni. */
@Composable
fun TripCard(
    name: String,
    dateRange: String,
    days: Int,
    coverPath: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = planerCardBorder(),
        modifier = modifier.fillMaxWidth(),
    ) {
        TripCoverImage(
            path = coverPath,
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.TripCardImageHeight),
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceS),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(Dimens.SpaceM),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(Dimens.SpaceXs),
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = dateRange,
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            DurationPill(days = days)
        }
    }
}

/** Kafelek dnia w poziomej liście „DNI PODRÓŻY”: okładka dnia, numer, data i licznik n/3. */
@Composable
fun DayTile(
    dayNumber: Int,
    date: String,
    coverPath: String?,
    photoCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = planerCardBorder(),
        modifier = modifier.width(Dimens.DayTileWidth),
    ) {
        PhotoImage(
            path = coverPath,
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.DayTileImageHeight),
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.padding(Dimens.SpaceS),
        ) {
            Text(
                text = stringResource(R.string.day_number, dayNumber),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = date,
                style = MaterialTheme.typography.captionSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceXs),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Image,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(Dimens.SpaceM),
                )
                Text(
                    text = stringResource(R.string.day_photo_count, photoCount, PhotoCategory.PhotosPerDay),
                    style = MaterialTheme.typography.captionSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun TripCardPreview() {
    PlanerPodrozyTheme {
        TripCard(
            name = "Włochy 2026",
            dateRange = "12–18 lipca 2026",
            days = 7,
            coverPath = null,
            onClick = {},
            modifier = Modifier.padding(Dimens.SpaceL),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DayTilePreview() {
    PlanerPodrozyTheme {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceS),
            modifier = Modifier.padding(Dimens.SpaceL),
        ) {
            DayTile(dayNumber = 1, date = "12 lipca", coverPath = null, photoCount = 2, onClick = {})
            DayTile(dayNumber = 2, date = "13 lipca", coverPath = null, photoCount = 0, onClick = {})
        }
    }
}
