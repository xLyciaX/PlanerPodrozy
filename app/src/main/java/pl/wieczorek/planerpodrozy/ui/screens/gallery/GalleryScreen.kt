package pl.wieczorek.planerpodrozy.ui.screens.gallery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pl.wieczorek.planerpodrozy.R
import pl.wieczorek.planerpodrozy.domain.model.Photo
import pl.wieczorek.planerpodrozy.domain.model.PhotoCategory
import pl.wieczorek.planerpodrozy.ui.components.PhotoImage
import pl.wieczorek.planerpodrozy.ui.components.PlanerTopAppBar
import pl.wieczorek.planerpodrozy.ui.components.filterLabelRes
import pl.wieczorek.planerpodrozy.ui.components.labelRes
import pl.wieczorek.planerpodrozy.ui.planerViewModel
import pl.wieczorek.planerpodrozy.ui.theme.Dimens
import pl.wieczorek.planerpodrozy.ui.theme.PlanerPodrozyTheme
import java.time.Instant

/** Ekran 07 podłączony do ViewModelu. */
@Composable
fun GalleryRoute(tripId: Long, onBack: () -> Unit) {
    val viewModel = planerViewModel { GalleryViewModel(tripId, it.tripRepository) }
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    GalleryScreen(state = state, onBack = onBack, onSelectCategory = viewModel::onSelectCategory)
}

/** Ekran 07: filtry kategorii i siatka 3 kolumn ze zdjęciami podróży. */
@Composable
fun GalleryScreen(
    state: GalleryUiState,
    onBack: () -> Unit,
    onSelectCategory: (PhotoCategory?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            PlanerTopAppBar(
                title = if (state.isLoading) "" else stringResource(R.string.gallery_title, state.tripName),
                onBack = onBack,
                centered = false,
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier,
    ) { padding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(Dimens.SpaceM),
            modifier = Modifier
                .padding(padding)
                .padding(top = Dimens.ScreenPadding)
                .fillMaxSize(),
        ) {
            CategoryFilters(selected = state.selectedCategory, onSelect = onSelectCategory)
            if (!state.isLoading && state.photos.isEmpty()) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = stringResource(R.string.gallery_empty),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                PhotoGrid(photos = state.photos)
            }
        }
    }
}

/** Poziomo przewijane filtry: „Wszystkie” + trzy kategorie; zawsze wybrany dokładnie jeden. */
@Composable
private fun CategoryFilters(selected: PhotoCategory?, onSelect: (PhotoCategory?) -> Unit) {
    // null na początku listy oznacza filtr „Wszystkie”.
    val filters: List<PhotoCategory?> = listOf(null) + PhotoCategory.entries
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceS),
        contentPadding = PaddingValues(horizontal = Dimens.ScreenPadding),
    ) {
        items(filters) { category ->
            val isSelected = category == selected
            FilterChip(
                selected = isSelected,
                onClick = { onSelect(category) },
                label = {
                    Text(
                        stringResource(category?.filterLabelRes ?: R.string.gallery_filter_all),
                        style = MaterialTheme.typography.labelLarge,
                    )
                },
                leadingIcon = if (isSelected) {
                    { Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(Dimens.IconS)) }
                } else {
                    null
                },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    labelColor = MaterialTheme.colorScheme.primary,
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary,
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = MaterialTheme.colorScheme.secondary,
                ),
            )
        }
    }
}

@Composable
private fun PhotoGrid(photos: List<Photo>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceS),
        verticalArrangement = Arrangement.spacedBy(Dimens.SpaceS),
        contentPadding = PaddingValues(
            start = Dimens.ScreenPadding,
            end = Dimens.ScreenPadding,
            bottom = Dimens.ScreenPadding,
        ),
        modifier = Modifier.fillMaxSize(),
    ) {
        items(photos, key = { it.id }) { photo ->
            PhotoImage(
                path = photo.filePath,
                contentDescription = stringResource(photo.category.labelRes),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.GalleryThumbnailHeight)
                    .clip(MaterialTheme.shapes.large),
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun GalleryScreenPreview() {
    PlanerPodrozyTheme {
        GalleryScreen(
            state = GalleryUiState(
                tripName = "Włochy 2026",
                photos = (1L..9L).map { id ->
                    Photo(id, 1, PhotoCategory.entries[(id % 3).toInt()], "", Instant.EPOCH)
                },
                isLoading = false,
            ),
            onBack = {},
            onSelectCategory = {},
        )
    }
}
