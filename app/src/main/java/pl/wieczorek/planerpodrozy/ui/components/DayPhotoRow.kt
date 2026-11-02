package pl.wieczorek.planerpodrozy.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import pl.wieczorek.planerpodrozy.domain.model.PhotoCategory
import pl.wieczorek.planerpodrozy.ui.theme.Dimens
import pl.wieczorek.planerpodrozy.ui.theme.PlanerPodrozyTheme

/**
 * Trzy kadry dnia w stałej kolejności kategorii (Krajobraz, Jedzenie, Selfie):
 * zdjęcie albo pusty kadr „Dodaj zdjęcie”. Ten sam rząd w widoku i w edycji dnia;
 * w edycji podaj [onChangePhoto] i [onDeletePhoto], żeby na zdjęciach pojawiły się przyciski.
 */
@Composable
fun DayPhotoRow(
    photoPaths: Map<PhotoCategory, String>,
    onAddPhoto: (PhotoCategory) -> Unit,
    modifier: Modifier = Modifier,
    onChangePhoto: ((PhotoCategory) -> Unit)? = null,
    onDeletePhoto: ((PhotoCategory) -> Unit)? = null,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceM),
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.PhotoRowHeight),
    ) {
        PhotoCategory.entries.forEach { category ->
            val label = stringResource(category.labelRes)
            val slotModifier = Modifier
                .weight(1f)
                .fillMaxHeight()
            val path = photoPaths[category]
            if (path != null) {
                PhotoCard(
                    imagePath = path,
                    label = label,
                    onChange = onChangePhoto?.let { { it(category) } },
                    onDelete = onDeletePhoto?.let { { it(category) } },
                    modifier = slotModifier,
                )
            } else {
                EmptyPhotoSlot(label = label, onClick = { onAddPhoto(category) }, modifier = slotModifier)
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun DayPhotoRowPreview() {
    PlanerPodrozyTheme {
        DayPhotoRow(
            photoPaths = mapOf(PhotoCategory.LANDSCAPE to "", PhotoCategory.FOOD to ""),
            onAddPhoto = {},
            onChangePhoto = {},
            onDeletePhoto = {},
            modifier = Modifier.padding(Dimens.SpaceL),
        )
    }
}
