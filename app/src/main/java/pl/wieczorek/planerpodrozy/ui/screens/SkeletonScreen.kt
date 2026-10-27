package pl.wieczorek.planerpodrozy.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import pl.wieczorek.planerpodrozy.ui.components.PlanerTopAppBar
import pl.wieczorek.planerpodrozy.ui.theme.Dimens
import pl.wieczorek.planerpodrozy.ui.theme.PlanerPodrozyTheme

// TODO etapy 2–7: TYMCZASOWY plik. Szkielet ekranu z paskiem i przyciskami przejść,
// żeby w etapie 1 dało się sprawdzić nawigację. Każdy ekran po kolei zastąpi
// prawdziwa implementacja, a na końcu ten plik zostanie usunięty.
// Teksty są tu wpisane na sztywno celowo, bo nie trafią do gotowej aplikacji.

@Composable
fun SkeletonScreen(
    title: String,
    onBack: () -> Unit,
    links: List<Pair<String, () -> Unit>> = emptyList(),
    centeredTitle: Boolean = true,
) {
    Scaffold(
        topBar = { PlanerTopAppBar(title = title, onBack = onBack, centered = centeredTitle) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(Dimens.SpaceM, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
        ) {
            Text(
                text = "Szkielet ekranu",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            links.forEach { (label, onClick) -> SkeletonLink(label, onClick) }
        }
    }
}

@Composable
fun SkeletonLink(label: String, onClick: () -> Unit) {
    OutlinedButton(onClick = onClick) { Text("→ $label") }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
private fun SkeletonScreenPreview() {
    PlanerPodrozyTheme {
        SkeletonScreen(title = "Dzień 1", onBack = {}, links = listOf("Edycja dnia" to {}))
    }
}
