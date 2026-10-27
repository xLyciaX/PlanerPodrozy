package pl.wieczorek.planerpodrozy.ui.screens.newtrip

import android.content.res.Configuration
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePickerColors
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import pl.wieczorek.planerpodrozy.R
import pl.wieczorek.planerpodrozy.ui.format.DateFormatter
import pl.wieczorek.planerpodrozy.ui.theme.Dimens
import pl.wieczorek.planerpodrozy.ui.theme.PlanerPodrozyTheme
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

/**
 * Ekran 03: wybór zakresu dat w oknie modalnym (gotowy DateRangePicker z M3).
 * Polska lokalizacja jest wymuszona, więc tydzień zaczyna się od poniedziałku,
 * a nazwy miesięcy są po polsku niezależnie od języka telefonu.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDateRangeDialog(
    initialStart: LocalDate?,
    initialEnd: LocalDate?,
    onConfirm: (start: LocalDate, end: LocalDate) -> Unit,
    onDismiss: () -> Unit,
) {
    val state = remember {
        DateRangePickerState(
            locale = DateFormatter.PolishLocale,
            initialSelectedStartDateMillis = initialStart?.toUtcMillis(),
            initialSelectedEndDateMillis = initialEnd?.toUtcMillis(),
            initialDisplayMode = DisplayMode.Picker,
        )
    }
    val start = state.selectedStartDateMillis?.toLocalDate()
    val end = state.selectedEndDateMillis?.toLocalDate()
    val colors = tripDatePickerColors()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = { if (start != null && end != null) onConfirm(start, end) },
                enabled = start != null && end != null,
            ) {
                Text(stringResource(R.string.action_ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) }
        },
        shape = MaterialTheme.shapes.extraLarge,
        colors = colors,
    ) {
        // Kalendarz M3 bierze język i pierwszy dzień tygodnia z konfiguracji ekranu.
        // Okno dialogowe ma własną konfigurację, więc podmieniamy ją na polską
        // dopiero tutaj, wewnątrz okna.
        val polishConfiguration = Configuration(LocalConfiguration.current).apply {
            setLocale(DateFormatter.PolishLocale)
        }
        CompositionLocalProvider(LocalConfiguration provides polishConfiguration) {
            DateRangePickerContent(state = state, colors = colors, start = start, end = end)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ColumnScope.DateRangePickerContent(
    state: DateRangePickerState,
    colors: DatePickerColors,
    start: LocalDate?,
    end: LocalDate?,
) {
    DateRangePicker(
        state = state,
        colors = colors,
        showModeToggle = false,
        title = {
            Text(
                text = stringResource(R.string.new_trip_pick_dates_title),
                modifier = Modifier.padding(start = Dimens.SpaceXl, end = Dimens.SpaceM, top = Dimens.SpaceL),
            )
        },
        headline = {
            Text(
                text = stringResource(
                    R.string.new_trip_date_range,
                    start?.let(DateFormatter::numeric) ?: stringResource(R.string.new_trip_from),
                    end?.let(DateFormatter::numeric) ?: stringResource(R.string.new_trip_to),
                ),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(start = Dimens.SpaceXl, end = Dimens.SpaceM, bottom = Dimens.SpaceM),
            )
        },
        // weight(1f): kalendarz zajmuje miejsce między nagłówkiem a przyciskami okna.
        modifier = Modifier.weight(1f),
    )
}

/** Kolory kalendarza z makiety: liliowe tło, zaznaczone końce w kolorze primary, zakres jasnoliliowy. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun tripDatePickerColors(): DatePickerColors = DatePickerDefaults.colors(
    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
    titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
    headlineContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    weekdayContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
    subheadContentColor = MaterialTheme.colorScheme.onSurface,
    selectedDayContainerColor = MaterialTheme.colorScheme.primary,
    selectedDayContentColor = MaterialTheme.colorScheme.onPrimary,
    dayInSelectionRangeContainerColor = MaterialTheme.colorScheme.primaryContainer,
    dayInSelectionRangeContentColor = MaterialTheme.colorScheme.primary,
    todayDateBorderColor = MaterialTheme.colorScheme.primary,
    todayContentColor = MaterialTheme.colorScheme.primary,
)

// DatePicker z M3 operuje na milisekundach w strefie UTC (północ danego dnia).
private fun LocalDate.toUtcMillis(): Long = atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

private fun Long.toLocalDate(): LocalDate = Instant.ofEpochMilli(this).atZone(ZoneOffset.UTC).toLocalDate()

@Preview
@Composable
private fun TripDateRangeDialogPreview() {
    PlanerPodrozyTheme {
        TripDateRangeDialog(
            initialStart = LocalDate.of(2026, 7, 12),
            initialEnd = LocalDate.of(2026, 7, 18),
            onConfirm = { _, _ -> },
            onDismiss = {},
        )
    }
}
