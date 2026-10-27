package pl.wieczorek.planerpodrozy.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pl.wieczorek.planerpodrozy.R
import pl.wieczorek.planerpodrozy.ui.theme.Dimens
import pl.wieczorek.planerpodrozy.ui.theme.PlanerPodrozyTheme

/** Ramka i tło kart z makiety: biała karta z jasną obwódką. */
@Composable
fun planerCardBorder(): BorderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)

/**
 * Karta kadru: zdjęcie i podpis kategorii. W edycji dnia podaj [onChange] i [onDelete],
 * żeby w rogu pojawiły się przyciski „zmień” i „usuń”.
 */
@Composable
fun PhotoCard(
    imagePath: String?,
    label: String,
    modifier: Modifier = Modifier,
    onChange: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
) {
    OutlinedCard(
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = planerCardBorder(),
        modifier = modifier,
    ) {
        Box {
            Column {
                PhotoImage(
                    path = imagePath,
                    contentDescription = label,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(Dimens.SpaceM),
                )
            }
            if (onChange != null || onDelete != null) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Dimens.PhotoActionGap),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(Dimens.PhotoActionGap),
                ) {
                    onChange?.let {
                        PhotoActionButton(Icons.Outlined.Edit, stringResource(R.string.cd_change_photo), it)
                    }
                    onDelete?.let {
                        PhotoActionButton(Icons.Outlined.Delete, stringResource(R.string.cd_delete_photo), it)
                    }
                }
            }
        }
    }
}

@Composable
private fun PhotoActionButton(icon: ImageVector, contentDescription: String, onClick: () -> Unit) {
    FilledIconButton(
        onClick = onClick,
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
            contentColor = MaterialTheme.colorScheme.primary,
        ),
        modifier = Modifier.size(Dimens.PhotoActionSize),
    ) {
        Icon(icon, contentDescription = contentDescription, modifier = Modifier.size(Dimens.IconXs))
    }
}

/** Pusty kadr z przerywaną ramką: „Selfie / Dodaj zdjęcie”. Kliknięcie otwiera wybór źródła. */
@Composable
fun EmptyPhotoSlot(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        contentColor = MaterialTheme.colorScheme.primary,
        modifier = modifier.dashedBorder(
            color = MaterialTheme.colorScheme.secondary,
            cornerRadius = Dimens.CornerExtraLarge,
        ),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(Dimens.SpaceM, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimens.SpaceS),
        ) {
            Icon(
                imageVector = Icons.Outlined.PhotoCamera,
                contentDescription = null,
                modifier = Modifier.size(Dimens.IconL),
            )
            Text(
                text = label + "\n" + stringResource(R.string.day_add_photo),
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun PhotoCardsPreview() {
    PlanerPodrozyTheme {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceM),
            modifier = Modifier
                .padding(Dimens.SpaceL)
                .height(Dimens.PhotoRowHeight),
        ) {
            PhotoCard(imagePath = null, label = "Krajobraz", modifier = Modifier.weight(1f))
            PhotoCard(
                imagePath = null,
                label = "Jedzenie",
                onChange = {},
                onDelete = {},
                modifier = Modifier.weight(1f),
            )
            EmptyPhotoSlot(label = "Selfie", onClick = {}, modifier = Modifier.weight(1f))
        }
    }
}
