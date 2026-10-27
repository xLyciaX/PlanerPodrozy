package pl.wieczorek.planerpodrozy.ui.theme

import androidx.compose.ui.graphics.Color

// Paleta wyciągnięta z makiety w Figmie. Komponenty nie używają tych stałych
// bezpośrednio, tylko przez MaterialTheme.colorScheme (patrz Theme.kt).

val Primary = Color(0xFF6750A4)
val OnPrimary = Color(0xFFFFFFFF)
val PrimaryContainer = Color(0xFFEADDFF)

// Kolor nagłówków z makiety. Bazowa paleta M3 nie ma takiego odcienia,
// dlatego trafia do slotu onPrimaryContainer (tekst na liliowym tle).
val OnPrimaryContainer = Color(0xFF4F4073)

// Przerywana ramka pustego kadru „Dodaj zdjęcie”.
val Secondary = Color(0xFF9E8CC7)
val SecondaryContainer = Color(0xFFE8DEF8)
val OnSecondaryContainer = Color(0xFF4A4459)

val Background = Color(0xFFFDFBFF)
val Surface = Color(0xFFFFFFFF)
val OnSurface = Color(0xFF1D1B20)
val OnSurfaceVariant = Color(0xFF49454F)
val Outline = Color(0xFF79747E)
val OutlineVariant = Color(0xFFECE6F0)

val SurfaceContainerLow = Color(0xFFF6F1FC)
val SurfaceContainer = Color(0xFFF1ECFB)
val SurfaceContainerHigh = Color(0xFFECE6F0)
val SurfaceContainerHighest = Color(0xFFEDEAF4)
