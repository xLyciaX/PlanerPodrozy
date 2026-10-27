package pl.wieczorek.planerpodrozy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Luggage
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.AsyncImage
import pl.wieczorek.planerpodrozy.R
import pl.wieczorek.planerpodrozy.ui.theme.Dimens
import pl.wieczorek.planerpodrozy.ui.theme.PlanerPodrozyTheme
import java.io.File

/**
 * Zdjęcie z pliku w pamięci aplikacji, przycięte do rozmiaru.
 * Gdy [path] to null, pokazuje [placeholder] (domyślnie jednolite liliowe tło,
 * jak pusty kafelek dnia w makiecie).
 */
@Composable
fun PhotoImage(
    path: String?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    placeholder: @Composable () -> Unit = {
        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primaryContainer),
        )
    },
) {
    Box(modifier) {
        if (path == null) {
            placeholder()
        } else {
            AsyncImage(
                model = File(path),
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

/** Okładka podróży: zdjęcie albo grafika pustej podróży (reguła 4). */
@Composable
fun TripCoverImage(path: String?, modifier: Modifier = Modifier) {
    PhotoImage(
        path = path,
        modifier = modifier,
        contentDescription = stringResource(R.string.cd_trip_cover),
        placeholder = { EmptyTripPlaceholder(Modifier.fillMaxSize()) },
    )
}

/** Domyślna grafika podróży bez zdjęć: walizka na jasnoliliowym tle. */
@Composable
fun EmptyTripPlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Outlined.Luggage,
            contentDescription = stringResource(R.string.cd_empty_trip),
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(Dimens.IconL),
        )
    }
}

@Preview(showBackground = true, widthDp = 328)
@Composable
private fun TripCoverImagePlaceholderPreview() {
    PlanerPodrozyTheme {
        TripCoverImage(
            path = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.TripCardImageHeight),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PhotoImagePlaceholderPreview() {
    PlanerPodrozyTheme {
        PhotoImage(path = null, modifier = Modifier.size(Dimens.DayTileWidth, Dimens.DayTileImageHeight))
    }
}
