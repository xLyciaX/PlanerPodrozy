package pl.wieczorek.planerpodrozy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch
import pl.wieczorek.planerpodrozy.R
import pl.wieczorek.planerpodrozy.ui.theme.Dimens
import pl.wieczorek.planerpodrozy.ui.theme.PlanerPodrozyTheme
import pl.wieczorek.planerpodrozy.ui.theme.caption

/** Jedna opcja w arkuszu, np. „Zrób zdjęcie” z podpisem „Uchwyć tę chwilę aparatem”. */
data class SheetOption(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val onClick: () -> Unit,
)

/**
 * Modalny arkusz z listą opcji i przyciskiem „Anuluj”. Ten sam komponent służy do
 * wyboru źródła zdjęcia (ekran 06) i do „+” przy filmie i notatce głosowej.
 * Po wybraniu opcji arkusz najpierw się chowa, potem wywołuje jej akcję.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionsBottomSheet(
    title: String,
    options: List<SheetOption>,
    onDismiss: () -> Unit,
    subtitle: String? = null,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    fun hideThen(action: () -> Unit) {
        scope.launch { sheetState.hide() }.invokeOnCompletion {
            onDismiss()
            action()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        shape = RoundedCornerShape(topStart = Dimens.SheetCornerRadius, topEnd = Dimens.SheetCornerRadius),
        dragHandle = { BottomSheetDefaults.DragHandle(color = MaterialTheme.colorScheme.primary) },
    ) {
        OptionsSheetContent(
            title = title,
            subtitle = subtitle,
            options = options.map { it.copy(onClick = { hideThen(it.onClick) }) },
            onCancel = { hideThen {} },
        )
    }
}

/** Zawartość arkusza wydzielona osobno, żeby dało się ją obejrzeć w @Preview. */
@Composable
private fun OptionsSheetContent(
    title: String,
    subtitle: String?,
    options: List<SheetOption>,
    onCancel: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Dimens.SpaceS),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = Dimens.SpaceL, end = Dimens.SpaceL, bottom = Dimens.SpaceXl),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        options.forEach { option ->
            ListItem(
                headlineContent = { Text(option.title, style = MaterialTheme.typography.titleSmall) },
                supportingContent = { Text(option.description, style = MaterialTheme.typography.caption) },
                leadingContent = {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(Dimens.SourceIconBoxSize)
                            .clip(MaterialTheme.shapes.medium)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                    ) {
                        Icon(option.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = ListItemDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    supportingColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
                modifier = Modifier
                    .clip(MaterialTheme.shapes.large)
                    .clickable(onClick = option.onClick),
            )
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        Button(
            onClick = onCancel,
            shape = MaterialTheme.shapes.large,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.SourceIconBoxSize),
        ) {
            Icon(Icons.Filled.Close, contentDescription = null, modifier = Modifier.size(Dimens.IconS))
            Text(
                text = stringResource(R.string.action_cancel),
                modifier = Modifier.padding(start = Dimens.SpaceS),
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun OptionsSheetContentPreview() {
    PlanerPodrozyTheme {
        Box(Modifier.background(MaterialTheme.colorScheme.primaryContainer)) {
            OptionsSheetContent(
                title = "Dodaj zdjęcie",
                subtitle = "Selfie · Dzień 1 · 12.07.2026",
                options = listOf(
                    SheetOption(Icons.Outlined.PhotoCamera, "Zrób zdjęcie", "Uchwyć tę chwilę aparatem") {},
                    SheetOption(Icons.Outlined.Photo, "Wybierz z galerii", "Dodaj wspomnienie z telefonu") {},
                ),
                onCancel = {},
            )
        }
    }
}
