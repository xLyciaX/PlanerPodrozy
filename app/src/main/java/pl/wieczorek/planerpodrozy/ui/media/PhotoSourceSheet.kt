package pl.wieczorek.planerpodrozy.ui.media

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import pl.wieczorek.planerpodrozy.R
import pl.wieczorek.planerpodrozy.domain.model.PhotoCategory
import pl.wieczorek.planerpodrozy.ui.components.OptionsBottomSheet
import pl.wieczorek.planerpodrozy.ui.components.SheetOption
import pl.wieczorek.planerpodrozy.ui.components.labelRes
import pl.wieczorek.planerpodrozy.ui.format.DateFormatter
import pl.wieczorek.planerpodrozy.ui.theme.PlanerPodrozyTheme
import java.time.LocalDate

/** Ekran 06: „Dodaj zdjęcie” z kontekstem („Selfie · Dzień 1 · 12.07.2026”), aparat albo galeria. */
@Composable
fun PhotoSourceSheet(
    category: PhotoCategory,
    dayNumber: Int,
    date: LocalDate,
    onTakePhoto: () -> Unit,
    onPickFromGallery: () -> Unit,
    onDismiss: () -> Unit,
) {
    OptionsBottomSheet(
        title = stringResource(R.string.photo_source_title),
        subtitle = stringResource(
            R.string.photo_source_context,
            stringResource(category.labelRes),
            dayNumber,
            DateFormatter.numeric(date),
        ),
        options = listOf(
            SheetOption(
                icon = Icons.Outlined.PhotoCamera,
                title = stringResource(R.string.photo_source_camera),
                description = stringResource(R.string.photo_source_camera_hint),
                onClick = onTakePhoto,
            ),
            SheetOption(
                icon = Icons.Outlined.Photo,
                title = stringResource(R.string.photo_source_gallery),
                description = stringResource(R.string.photo_source_gallery_hint),
                onClick = onPickFromGallery,
            ),
        ),
        onDismiss = onDismiss,
    )
}

// Arkusz modalny pokazuje się w podglądzie tylko w trybie interaktywnym (Interactive Mode).
@Preview
@Composable
private fun PhotoSourceSheetPreview() {
    PlanerPodrozyTheme {
        PhotoSourceSheet(
            category = PhotoCategory.SELFIE,
            dayNumber = 1,
            date = LocalDate.of(2026, 7, 12),
            onTakePhoto = {},
            onPickFromGallery = {},
            onDismiss = {},
        )
    }
}
