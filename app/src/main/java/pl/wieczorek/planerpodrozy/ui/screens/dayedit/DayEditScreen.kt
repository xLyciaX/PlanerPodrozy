package pl.wieczorek.planerpodrozy.ui.screens.dayedit

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pl.wieczorek.planerpodrozy.R
import pl.wieczorek.planerpodrozy.domain.model.PhotoCategory
import pl.wieczorek.planerpodrozy.domain.model.TripDay
import pl.wieczorek.planerpodrozy.ui.components.AddLocationDialog
import pl.wieczorek.planerpodrozy.ui.components.CancelSaveButtons
import pl.wieczorek.planerpodrozy.ui.components.DayPhotoRow
import pl.wieczorek.planerpodrozy.ui.components.PlanerTopAppBar
import pl.wieczorek.planerpodrozy.ui.components.dashedBorder
import pl.wieczorek.planerpodrozy.ui.components.planerTextFieldColors
import pl.wieczorek.planerpodrozy.ui.media.PhotoSourceSheet
import pl.wieczorek.planerpodrozy.ui.media.rememberPhotoSourceLaunchers
import pl.wieczorek.planerpodrozy.ui.planerViewModel
import pl.wieczorek.planerpodrozy.ui.theme.Dimens
import pl.wieczorek.planerpodrozy.ui.theme.PlanerPodrozyTheme
import java.time.LocalDate

/** Ekran 05b podłączony do ViewModelu. [onClose] wraca do widoku dnia. */
@Composable
fun DayEditRoute(dayId: Long, onClose: () -> Unit) {
    val viewModel = planerViewModel { DayEditViewModel(dayId, it.dayRepository, it.mediaStorage) }
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val launchers = rememberPhotoSourceLaunchers(
        onPhotoTaken = viewModel::onCameraResult,
        onPhotoPicked = viewModel::onGalleryResult,
    )

    // Systemowe „wstecz” działa jak „Anuluj”: odrzuca zmiany robocze.
    BackHandler(onBack = viewModel::onCancel)
    LaunchedEffect(state.isFinished) {
        if (state.isFinished) onClose()
    }

    DayEditScreen(
        state = state,
        onCancel = viewModel::onCancel,
        onSave = viewModel::onSave,
        onOpenAddLocation = viewModel::onOpenAddLocation,
        onDismissAddLocation = viewModel::onDismissAddLocation,
        onAddLocation = viewModel::onAddLocation,
        onRemoveLocation = viewModel::onRemoveLocation,
        onDescriptionChange = viewModel::onDescriptionChange,
        onChoosePhoto = viewModel::onChoosePhoto,
        onDeletePhoto = viewModel::onDeletePhoto,
        onDismissPhotoSource = viewModel::onDismissPhotoSource,
        onTakePhoto = { category -> launchers.takePhoto(viewModel.prepareCameraPhoto(category)) },
        onPickFromGallery = { category ->
            viewModel.onGalleryPickStarted(category)
            launchers.pickFromGallery()
        },
        onPhotoErrorShown = viewModel::onPhotoErrorShown,
    )
}

/** Ekran 05b: chipy lokalizacji, kadry ze „zmień”/„usuń”, opis z licznikiem, Anuluj/Zapisz. */
@Composable
fun DayEditScreen(
    state: DayEditUiState,
    onCancel: () -> Unit,
    onSave: () -> Unit,
    onOpenAddLocation: () -> Unit,
    onDismissAddLocation: () -> Unit,
    onAddLocation: (String) -> Unit,
    onRemoveLocation: (index: Int) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onChoosePhoto: (PhotoCategory) -> Unit,
    onDeletePhoto: (PhotoCategory) -> Unit,
    onDismissPhotoSource: () -> Unit,
    onTakePhoto: (PhotoCategory) -> Unit,
    onPickFromGallery: (PhotoCategory) -> Unit,
    onPhotoErrorShown: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val photoErrorMessage = stringResource(R.string.photo_add_error)
    LaunchedEffect(state.showPhotoError) {
        if (state.showPhotoError) {
            snackbarHostState.showSnackbar(photoErrorMessage)
            onPhotoErrorShown()
        }
    }

    Scaffold(
        topBar = { PlanerTopAppBar(title = stringResource(R.string.day_edit_title), onBack = onCancel) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier,
    ) { padding ->
        if (state.isLoading) return@Scaffold
        Column(
            verticalArrangement = Arrangement.spacedBy(Dimens.SpaceL),
            modifier = Modifier
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimens.ScreenPadding, vertical = Dimens.SpaceXl),
        ) {
            LocationChips(
                locations = state.locations,
                onRemove = onRemoveLocation,
                onAdd = onOpenAddLocation,
            )
            Text(
                text = stringResource(R.string.day_edit_photos_heading),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            DayPhotoRow(
                photoPaths = state.photos.mapValues { it.value.filePath },
                onAddPhoto = onChoosePhoto,
                onChangePhoto = onChoosePhoto,
                onDeletePhoto = onDeletePhoto,
            )
            DescriptionField(description = state.description, onChange = onDescriptionChange)
            CancelSaveButtons(onCancel = onCancel, onSave = onSave, saveEnabled = !state.isSaving)
        }
    }

    if (state.isAddLocationOpen) {
        AddLocationDialog(onConfirm = onAddLocation, onDismiss = onDismissAddLocation)
    }
    val category = state.photoSourceCategory
    val date = state.date
    if (category != null && date != null) {
        PhotoSourceSheet(
            category = category,
            dayNumber = state.dayNumber,
            date = date,
            onTakePhoto = { onTakePhoto(category) },
            onPickFromGallery = { onPickFromGallery(category) },
            onDismiss = onDismissPhotoSource,
        )
    }
}

/** Chipy lokalizacji z „×” (InputChip) i chip „+ Dodaj lokalizację” z przerywaną ramką (AssistChip). */
@Composable
private fun LocationChips(
    locations: List<String>,
    onRemove: (index: Int) -> Unit,
    onAdd: () -> Unit,
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceS),
        verticalArrangement = Arrangement.spacedBy(Dimens.SpaceS),
    ) {
        locations.forEachIndexed { index, location ->
            InputChip(
                selected = false,
                onClick = {},
                label = { Text(location, style = MaterialTheme.typography.bodySmall) },
                leadingIcon = {
                    Icon(Icons.Outlined.LocationOn, contentDescription = null, modifier = Modifier.size(Dimens.IconS))
                },
                trailingIcon = {
                    IconButton(onClick = { onRemove(index) }, modifier = Modifier.size(Dimens.IconS)) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = stringResource(R.string.cd_remove_location),
                            modifier = Modifier.size(Dimens.IconS),
                        )
                    }
                },
                colors = InputChipDefaults.inputChipColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    leadingIconColor = MaterialTheme.colorScheme.primary,
                    trailingIconColor = MaterialTheme.colorScheme.primary,
                ),
                border = InputChipDefaults.inputChipBorder(enabled = true, selected = false, borderColor = Color.Transparent),
            )
        }
        AssistChip(
            onClick = onAdd,
            label = { Text(stringResource(R.string.day_edit_add_location), style = MaterialTheme.typography.labelMedium) },
            leadingIcon = {
                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(Dimens.IconS))
            },
            colors = AssistChipDefaults.assistChipColors(
                labelColor = MaterialTheme.colorScheme.primary,
                leadingIconContentColor = MaterialTheme.colorScheme.primary,
            ),
            // M3 nie ma przerywanej ramki: zwykłą chowamy i rysujemy własną.
            border = AssistChipDefaults.assistChipBorder(enabled = true, borderColor = Color.Transparent),
            modifier = Modifier.dashedBorder(MaterialTheme.colorScheme.primary, Dimens.CornerSmall),
        )
    }
}

@Composable
private fun DescriptionField(description: String, onChange: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpaceM)) {
        Text(
            text = stringResource(R.string.day_description),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
        OutlinedTextField(
            value = description,
            onValueChange = onChange,
            minLines = 4,
            textStyle = MaterialTheme.typography.bodyMedium,
            supportingText = {
                Text(
                    text = stringResource(
                        R.string.day_edit_description_counter,
                        description.length,
                        TripDay.MaxDescriptionLength,
                    ),
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            shape = MaterialTheme.shapes.extraLarge,
            colors = planerTextFieldColors(),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun DayEditScreenPreview() {
    PlanerPodrozyTheme {
        DayEditScreen(
            state = DayEditUiState(
                isLoading = false,
                dayNumber = 1,
                date = LocalDate.of(2026, 7, 12),
                locations = listOf("Positano, Włochy", "Amalfi, Włochy"),
                description = "Pierwszy dzień w Positano!",
            ),
            onCancel = {},
            onSave = {},
            onOpenAddLocation = {},
            onDismissAddLocation = {},
            onAddLocation = {},
            onRemoveLocation = {},
            onDescriptionChange = {},
            onChoosePhoto = {},
            onDeletePhoto = {},
            onDismissPhotoSource = {},
            onTakePhoto = {},
            onPickFromGallery = {},
            onPhotoErrorShown = {},
        )
    }
}
