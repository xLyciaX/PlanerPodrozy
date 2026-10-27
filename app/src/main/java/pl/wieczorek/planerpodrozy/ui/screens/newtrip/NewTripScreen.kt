package pl.wieczorek.planerpodrozy.ui.screens.newtrip

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pl.wieczorek.planerpodrozy.R
import pl.wieczorek.planerpodrozy.ui.components.CancelSaveButtons
import pl.wieczorek.planerpodrozy.ui.components.DateField
import pl.wieczorek.planerpodrozy.ui.components.PlanerTopAppBar
import pl.wieczorek.planerpodrozy.ui.components.planerTextFieldColors
import pl.wieczorek.planerpodrozy.ui.format.DateFormatter
import pl.wieczorek.planerpodrozy.ui.planerViewModel
import pl.wieczorek.planerpodrozy.ui.theme.Dimens
import pl.wieczorek.planerpodrozy.ui.theme.PlanerPodrozyTheme
import java.time.LocalDate

/** Ekran 02 podłączony do ViewModelu. [onClose] wraca do listy (po zapisie albo anulowaniu). */
@Composable
fun NewTripRoute(onClose: () -> Unit) {
    val viewModel = planerViewModel { NewTripViewModel(it.tripRepository) }
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // Efekt uruchamia się, gdy zmieni się isSaved: po udanym zapisie wracamy do listy.
    LaunchedEffect(state.isSaved) {
        if (state.isSaved) onClose()
    }

    NewTripScreen(
        state = state,
        onNameChange = viewModel::onNameChange,
        onOpenDatePicker = viewModel::onOpenDatePicker,
        onDismissDatePicker = viewModel::onDismissDatePicker,
        onDatesSelected = viewModel::onDatesSelected,
        onSave = viewModel::onSave,
        onCancel = onClose,
    )
}

@Composable
fun NewTripScreen(
    state: NewTripUiState,
    onNameChange: (String) -> Unit,
    onOpenDatePicker: () -> Unit,
    onDismissDatePicker: () -> Unit,
    onDatesSelected: (LocalDate, LocalDate) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = { PlanerTopAppBar(title = stringResource(R.string.new_trip_title), onBack = onCancel) },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier,
    ) { padding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(Dimens.SpaceM),
            modifier = Modifier
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(Dimens.ScreenPadding),
        ) {
            Text(
                text = stringResource(R.string.new_trip_heading),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(vertical = Dimens.SpaceM),
            )
            OutlinedTextField(
                value = state.name,
                onValueChange = onNameChange,
                label = { Text(stringResource(R.string.new_trip_destination)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done,
                ),
                shape = MaterialTheme.shapes.large,
                colors = planerTextFieldColors(),
                modifier = Modifier.fillMaxWidth(),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceS)) {
                DateField(
                    label = stringResource(R.string.new_trip_from),
                    value = state.startDate?.let(DateFormatter::numeric).orEmpty(),
                    onClick = onOpenDatePicker,
                    modifier = Modifier.weight(1f),
                )
                DateField(
                    label = stringResource(R.string.new_trip_to),
                    value = state.endDate?.let(DateFormatter::numeric).orEmpty(),
                    onClick = onOpenDatePicker,
                    modifier = Modifier.weight(1f),
                )
            }
            CancelSaveButtons(
                onCancel = onCancel,
                onSave = onSave,
                saveEnabled = state.canSave,
                modifier = Modifier.padding(top = Dimens.SpaceM),
            )
        }
    }

    if (state.isDatePickerOpen) {
        TripDateRangeDialog(
            initialStart = state.startDate,
            initialEnd = state.endDate,
            onConfirm = onDatesSelected,
            onDismiss = onDismissDatePicker,
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
private fun NewTripScreenPreview() {
    PlanerPodrozyTheme {
        NewTripScreen(
            state = NewTripUiState(
                name = "Włochy 2026",
                startDate = LocalDate.of(2026, 7, 12),
                endDate = LocalDate.of(2026, 7, 18),
            ),
            onNameChange = {},
            onOpenDatePicker = {},
            onDismissDatePicker = {},
            onDatesSelected = { _, _ -> },
            onSave = {},
            onCancel = {},
        )
    }
}
