package pl.wieczorek.planerpodrozy.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import pl.wieczorek.planerpodrozy.R
import pl.wieczorek.planerpodrozy.ui.theme.PlanerPodrozyTheme

/**
 * Pasek aplikacji ze strzałką wstecz, używany na wszystkich ekranach poza listą podróży.
 * [centered] = false daje tytuł wyrównany do lewej (galeria).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanerTopAppBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    centered: Boolean = true,
) {
    val colors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.background,
        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
    )
    val titleContent: @Composable () -> Unit = {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
    val navigationIcon: @Composable () -> Unit = {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.action_back),
            )
        }
    }
    if (centered) {
        CenterAlignedTopAppBar(
            title = titleContent,
            navigationIcon = navigationIcon,
            colors = colors,
            modifier = modifier,
        )
    } else {
        TopAppBar(
            title = titleContent,
            navigationIcon = navigationIcon,
            colors = colors,
            modifier = modifier,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PlanerTopAppBarPreview() {
    PlanerPodrozyTheme {
        PlanerTopAppBar(title = "Nowa podróż", onBack = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun PlanerTopAppBarStartPreview() {
    PlanerPodrozyTheme {
        PlanerTopAppBar(title = "Galeria – Włochy 2026", onBack = {}, centered = false)
    }
}
