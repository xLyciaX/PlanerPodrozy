package pl.wieczorek.planerpodrozy.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import pl.wieczorek.planerpodrozy.R

val DmSans = FontFamily(
    Font(R.font.dm_sans_regular, FontWeight.Normal),
    Font(R.font.dm_sans_medium, FontWeight.Medium),
)

private val Regular = TextStyle(fontFamily = DmSans, fontWeight = FontWeight.Normal)
private val Medium = TextStyle(fontFamily = DmSans, fontWeight = FontWeight.Medium)

// Rozmiary z makiety. Obok każdego stylu: gdzie jest używany.
val Typography = Typography(
    // „Moje podróże”, tytuły pasków aplikacji
    titleLarge = Medium.copy(fontSize = 22.sp, lineHeight = 28.sp, letterSpacing = (-0.01).em),
    // „Dokąd tym razem?”, nazwa podróży na banerze, tytuł bottom sheetu
    headlineSmall = Medium.copy(fontSize = 20.sp, lineHeight = 26.sp, letterSpacing = (-0.01).em),
    // „Trzy kadry dnia”
    titleMedium = Medium.copy(fontSize = 16.sp, lineHeight = 22.sp, letterSpacing = (-0.01).em),
    // „Opis dnia”, „Film z podróży”, nazwa miesiąca, opcje w bottom sheecie
    titleSmall = Medium.copy(fontSize = 14.sp, lineHeight = 20.sp),
    // tekst wpisywany w pola formularza
    bodyLarge = Regular.copy(fontSize = 14.sp, lineHeight = 24.sp),
    // opis dnia
    bodyMedium = Regular.copy(fontSize = 14.sp, lineHeight = 21.sp),
    // lokalizacje, termin podróży na banerze
    bodySmall = Regular.copy(fontSize = 13.sp, lineHeight = 18.sp),
    // przyciski, nazwy na kartach, podpisy kadrów
    labelLarge = Medium.copy(fontSize = 13.sp, lineHeight = 20.sp),
    // etykiety sekcji („DNI PODRÓŻY”), chip „Dodaj lokalizację”
    labelMedium = Medium.copy(fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.4.sp),
    // pigułka „7 dni”, etykiety pól, „Dzień 1” na kafelku
    labelSmall = Medium.copy(fontSize = 11.sp, lineHeight = 16.sp),
)

// Dwa drobne style z makiety, dla których M3 nie ma osobnego slotu.
// Zdefiniowane raz tutaj, używane jako MaterialTheme.typography.caption itd.

/** 11 sp Regular: daty na karcie podróży, czasy odtwarzaczy, podpisy w bottom sheecie. */
val Typography.caption: TextStyle
    get() = labelSmall.copy(fontWeight = FontWeight.Normal)

/** 10 sp Regular: data i licznik zdjęć na kafelku dnia. */
val Typography.captionSmall: TextStyle
    get() = caption.copy(fontSize = 10.sp, lineHeight = 14.sp)
