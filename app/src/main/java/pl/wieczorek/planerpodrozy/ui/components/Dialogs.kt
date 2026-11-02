package pl.wieczorek.planerpodrozy.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.DialogProperties
import pl.wieczorek.planerpodrozy.R
import pl.wieczorek.planerpodrozy.ui.format.DateFormatter
import pl.wieczorek.planerpodrozy.ui.theme.PlanerPodrozyTheme

/** Dialog z polem tekstowym do dodania lokalizacji (np. „Positano, Włochy”). */
@Composable
fun AddLocationDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var text by rememberSaveable { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.add_location_title)) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                singleLine = true,
                placeholder = { Text(stringResource(R.string.add_location_hint)) },
                shape = MaterialTheme.shapes.large,
                colors = planerTextFieldColors(),
                modifier = Modifier.fillMaxWidth(),
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(text.trim()) }, enabled = text.isNotBlank()) {
                Text(stringResource(R.string.action_add))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) }
        },
        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    )
}

/**
 * Dialog widoczny w trakcie nagrywania notatki głosowej: licznik czasu
 * i przyciski „Anuluj” / „Zatrzymaj i zapisz”. Nie zamyka się kliknięciem obok.
 */
@Composable
fun RecordVoiceDialog(
    elapsedMs: Long,
    onStopAndSave: () -> Unit,
    onCancel: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = {},
        icon = { Icon(Icons.Outlined.Mic, contentDescription = null) },
        title = { Text(stringResource(R.string.record_title)) },
        text = {
            Text(
                text = DateFormatter.duration(elapsedMs),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        confirmButton = {
            TextButton(onClick = onStopAndSave) { Text(stringResource(R.string.record_stop_save)) }
        },
        dismissButton = {
            TextButton(onClick = onCancel) { Text(stringResource(R.string.action_cancel)) }
        },
        iconContentColor = MaterialTheme.colorScheme.primary,
        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
    )
}

/** Potwierdzenie usunięcia podróży; „Usuń” w kolorze błędu, bo operacji nie da się cofnąć. */
@Composable
fun DeleteTripDialog(
    tripName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Outlined.Delete, contentDescription = null) },
        title = { Text(stringResource(R.string.trip_delete_title)) },
        text = { Text(stringResource(R.string.trip_delete_message, tripName)) },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
            ) {
                Text(stringResource(R.string.action_delete))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) }
        },
        iconContentColor = MaterialTheme.colorScheme.error,
        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    )
}

@Preview
@Composable
private fun AddLocationDialogPreview() {
    PlanerPodrozyTheme { AddLocationDialog(onConfirm = {}, onDismiss = {}) }
}

@Preview
@Composable
private fun RecordVoiceDialogPreview() {
    PlanerPodrozyTheme { RecordVoiceDialog(elapsedMs = 12_000, onStopAndSave = {}, onCancel = {}) }
}

@Preview
@Composable
private fun DeleteTripDialogPreview() {
    PlanerPodrozyTheme { DeleteTripDialog(tripName = "Włochy 2026", onConfirm = {}, onDismiss = {}) }
}
