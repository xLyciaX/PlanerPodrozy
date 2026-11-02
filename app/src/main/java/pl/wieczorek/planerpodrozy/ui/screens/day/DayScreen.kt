package pl.wieczorek.planerpodrozy.ui.screens.day

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pl.wieczorek.planerpodrozy.R
import pl.wieczorek.planerpodrozy.domain.model.DayDetail
import pl.wieczorek.planerpodrozy.domain.model.PhotoCategory
import pl.wieczorek.planerpodrozy.domain.model.TripDay
import pl.wieczorek.planerpodrozy.ui.components.DayPhotoRow
import pl.wieczorek.planerpodrozy.ui.components.LocationRow
import pl.wieczorek.planerpodrozy.ui.components.PlanerTopAppBar
import pl.wieczorek.planerpodrozy.ui.components.planerCardBorder
import pl.wieczorek.planerpodrozy.ui.media.PhotoSourceSheet
import pl.wieczorek.planerpodrozy.ui.media.rememberPhotoSourceLaunchers
import pl.wieczorek.planerpodrozy.ui.planerViewModel
import pl.wieczorek.planerpodrozy.ui.theme.Dimens
import pl.wieczorek.planerpodrozy.ui.theme.PlanerPodrozyTheme
import java.time.LocalDate

/** Ekran 05 podłączony do ViewModelu. */
@Composable
fun DayRoute(
    dayId: Long,
    onBack: () -> Unit,
    onEdit: () -> Unit,
) {
    val viewModel = planerViewModel { DayViewModel(dayId, it.dayRepository, it.mediaStorage) }
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val launchers = rememberPhotoSourceLaunchers(
        onPhotoTaken = viewModel::onCameraResult,
        onPhotoPicked = viewModel::onGalleryResult,
    )
    DayScreen(
        state = state,
        onBack = onBack,
        onEdit = onEdit,
        onAddPhoto = viewModel::onAddPhoto,
        onDismissPhotoSource = viewModel::onDismissPhotoSource,
        onTakePhoto = { category -> launchers.takePhoto(viewModel.prepareCameraPhoto(category)) },
        onPickFromGallery = { category ->
            viewModel.onGalleryPickStarted(category)
            launchers.pickFromGallery()
        },
        onPhotoErrorShown = viewModel::onPhotoErrorShown,
    )
}

/** Ekran 05: lokalizacje, trzy kadry dnia i opis. */
@Composable
fun DayScreen(
    state: DayUiState,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onAddPhoto: (PhotoCategory) -> Unit,
    onDismissPhotoSource: () -> Unit,
    onTakePhoto: (PhotoCategory) -> Unit,
    onPickFromGallery: (PhotoCategory) -> Unit,
    onPhotoErrorShown: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val detail = state.detail
    val title = detail?.let { stringResource(R.string.day_number, it.day.dayNumber) }.orEmpty()
    val snackbarHostState = remember { SnackbarHostState() }
    val photoErrorMessage = stringResource(R.string.photo_add_error)
    LaunchedEffect(state.showPhotoError) {
        if (state.showPhotoError) {
            snackbarHostState.showSnackbar(photoErrorMessage)
            onPhotoErrorShown()
        }
    }

    Scaffold(
        topBar = { PlanerTopAppBar(title = title, onBack = onBack) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier,
    ) { padding ->
        if (detail == null) return@Scaffold
        Column(
            verticalArrangement = Arrangement.spacedBy(Dimens.SpaceL),
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimens.ScreenPadding, vertical = Dimens.SpaceXl),
        ) {
            if (detail.day.locations.isNotEmpty()) {
                // FlowRow zawija lokalizacje do kolejnej linii, gdy nie mieszczą się w jednej.
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceL),
                    verticalArrangement = Arrangement.spacedBy(Dimens.SpaceS),
                ) {
                    detail.day.locations.forEach { LocationRow(text = it) }
                }
            }
            PhotosHeader(onEdit = onEdit)
            DayPhotoRow(
                photoPaths = detail.photos.mapValues { it.value.filePath },
                onAddPhoto = onAddPhoto,
            )
            DayDescription(description = detail.day.description)
        }
    }

    val category = state.photoSourceCategory
    if (detail != null && category != null) {
        PhotoSourceSheet(
            category = category,
            dayNumber = detail.day.dayNumber,
            date = detail.day.date,
            onTakePhoto = { onTakePhoto(category) },
            onPickFromGallery = { onPickFromGallery(category) },
            onDismiss = onDismissPhotoSource,
        )
    }
}

@Composable
private fun PhotosHeader(onEdit: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = stringResource(R.string.day_photos_heading),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.weight(1f),
        )
        // Tonal button z M3 domyślnie bierze secondaryContainer; makieta ma liliowy primaryContainer.
        FilledTonalButton(
            onClick = onEdit,
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary,
            ),
        ) {
            Icon(Icons.Outlined.Edit, contentDescription = null, modifier = Modifier.size(Dimens.IconS))
            Text(
                text = stringResource(R.string.day_edit),
                modifier = Modifier.padding(start = Dimens.SpaceS),
            )
        }
    }
}

@Composable
private fun DayDescription(description: String) {
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpaceM)) {
        Text(
            text = stringResource(R.string.day_description),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
        OutlinedCard(
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = planerCardBorder(),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = description.ifBlank { stringResource(R.string.day_description_empty) },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(Dimens.SpaceL),
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun DayScreenPreview() {
    PlanerPodrozyTheme {
        DayScreen(
            state = DayUiState(
                detail = DayDetail(
                    day = TripDay(
                        id = 1,
                        tripId = 1,
                        dayNumber = 1,
                        date = LocalDate.of(2026, 7, 12),
                        description = "Pierwszy dzień w Positano! Spacerowaliśmy wąskimi uliczkami.",
                        locations = listOf("Positano, Włochy", "Amalfi, Włochy"),
                    ),
                    photos = emptyMap(),
                ),
                isLoading = false,
            ),
            onBack = {},
            onEdit = {},
            onAddPhoto = {},
            onDismissPhotoSource = {},
            onTakePhoto = {},
            onPickFromGallery = {},
            onPhotoErrorShown = {},
        )
    }
}
