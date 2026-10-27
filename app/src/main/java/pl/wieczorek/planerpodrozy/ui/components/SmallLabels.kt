package pl.wieczorek.planerpodrozy.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.tooling.preview.Preview
import pl.wieczorek.planerpodrozy.R
import pl.wieczorek.planerpodrozy.ui.theme.Dimens
import pl.wieczorek.planerpodrozy.ui.theme.PlanerPodrozyTheme

/**
 * Pigułka z liczbą dni („7 dni”). Na liście jest szara, w widoku podróży liliowa,
 * dlatego kolory są parametrami.
 */
@Composable
fun DurationPill(
    days: Int,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    Surface(
        shape = CircleShape,
        color = containerColor,
        contentColor = contentColor,
        modifier = modifier,
    ) {
        Text(
            text = pluralStringResource(R.plurals.trip_duration_days, days, days),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = Dimens.SpaceM, vertical = Dimens.SpaceXs),
        )
    }
}

/** Etykieta sekcji („DNI PODRÓŻY”) z opcjonalnym elementem po prawej (np. przycisk „+”). */
@Composable
fun SectionLabel(
    text: String,
    modifier: Modifier = Modifier,
    trailing: @Composable RowScope.() -> Unit = {},
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        trailing()
    }
}

/** Lokalizacja z ikoną pinezki („Positano, Włochy”). */
@Composable
fun LocationRow(text: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceS),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Outlined.LocationOn,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(Dimens.IconS),
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SmallLabelsPreview() {
    PlanerPodrozyTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(Dimens.SpaceM),
            modifier = Modifier.padding(Dimens.SpaceL),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceS)) {
                DurationPill(days = 1)
                DurationPill(days = 3)
                DurationPill(
                    days = 7,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.primary,
                )
            }
            SectionLabel(text = "DNI PODRÓŻY")
            SectionLabel(text = "FILM I NOTATKA GŁOSOWA") {
                IconButton(onClick = {}) { Icon(Icons.Filled.Add, contentDescription = null) }
            }
            LocationRow(text = "Positano, Włochy")
        }
    }
}
