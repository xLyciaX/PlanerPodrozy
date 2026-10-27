package pl.wieczorek.planerpodrozy.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import pl.wieczorek.planerpodrozy.R
import pl.wieczorek.planerpodrozy.ui.theme.Dimens
import pl.wieczorek.planerpodrozy.ui.theme.PlanerPodrozyTheme

/** Kolory pól tekstowych z makiety: białe tło, jasna ramka, fioletowa po zaznaczeniu. */
@Composable
fun planerTextFieldColors(): TextFieldColors = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = MaterialTheme.colorScheme.surface,
    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
    disabledContainerColor = MaterialTheme.colorScheme.surface,
    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
)

/**
 * Pole daty tylko do odczytu, z ikoną kalendarza. Kliknięcie w dowolne miejsce
 * pola wywołuje [onClick] (otwiera wybór dat).
 */
@Composable
fun DateField(
    label: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            label = { Text(label) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    contentDescription = stringResource(R.string.cd_pick_dates),
                    tint = MaterialTheme.colorScheme.primary,
                )
            },
            shape = MaterialTheme.shapes.large,
            colors = planerTextFieldColors(),
            modifier = Modifier.fillMaxWidth(),
        )
        // Przezroczysta warstwa przechwytuje kliknięcie, bo pole readOnly samo go nie zgłasza.
        Box(
            Modifier
                .matchParentSize()
                .clickable(onClick = onClick),
        )
    }
}

/** Para przycisków „Anuluj” / „Zapisz” na całą szerokość, po równo. */
@Composable
fun CancelSaveButtons(
    onCancel: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
    saveEnabled: Boolean = true,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceM),
    ) {
        OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f)) {
            Text(stringResource(R.string.action_cancel))
        }
        Button(onClick = onSave, enabled = saveEnabled, modifier = Modifier.weight(1f)) {
            Text(stringResource(R.string.action_save))
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun FormFieldsPreview() {
    PlanerPodrozyTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(Dimens.SpaceM),
            modifier = Modifier.padding(Dimens.SpaceL),
        ) {
            OutlinedTextField(
                value = "Włochy 2026",
                onValueChange = {},
                label = { Text("Cel podróży") },
                shape = MaterialTheme.shapes.large,
                colors = planerTextFieldColors(),
                modifier = Modifier.fillMaxWidth(),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceS)) {
                DateField("Od", "12.07.2026", onClick = {}, modifier = Modifier.weight(1f))
                DateField("Do", "", onClick = {}, modifier = Modifier.weight(1f))
            }
            CancelSaveButtons(onCancel = {}, onSave = {})
            CancelSaveButtons(onCancel = {}, onSave = {}, saveEnabled = false)
        }
    }
}
