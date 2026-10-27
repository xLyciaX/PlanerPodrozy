package pl.wieczorek.planerpodrozy.ui.theme

import androidx.compose.ui.unit.dp

/** Wymiary powtarzające się w wielu miejscach makiety. */
object Dimens {
    // Odstępy
    val SpaceXs = 4.dp
    val SpaceS = 8.dp
    val SpaceM = 12.dp
    val SpaceL = 16.dp
    val SpaceXl = 24.dp

    // Zaokrąglenia (używane w Shape.kt i tam, gdzie potrzebny jest sam promień)
    val CornerSmall = 8.dp
    val CornerMedium = 12.dp
    val CornerLarge = 16.dp
    val CornerExtraLarge = 24.dp

    /** Margines ekranu od krawędzi. */
    val ScreenPadding = SpaceL

    /** Zaokrąglenie dolnych rogów nagłówka listy i górnych rogów bottom sheetu. */
    val SheetCornerRadius = 28.dp

    /** Dolny odstęp listy, żeby ostatniego elementu nie zasłaniał pływający przycisk. */
    val FabClearance = 88.dp

    // Nagłówek „Moje podróże”
    val ListHeaderHeight = 88.dp

    // Karta podróży i baner
    val TripCardImageHeight = 70.dp
    val TripBannerHeight = 132.dp

    // Kafelek dnia
    val DayTileWidth = 100.dp
    val DayTileImageHeight = 64.dp

    // Kadry dnia
    val PhotoRowHeight = 200.dp

    // Galeria
    val GalleryThumbnailHeight = 80.dp

    // Multimedia
    val MediaHeaderHeight = 48.dp
    val VideoPreviewHeight = 128.dp
    val MediaProgressRowHeight = 40.dp
    val PlayButtonSize = 56.dp
    val VoiceButtonSize = 48.dp
    val WaveformHeight = 36.dp
    val ProgressHeight = 4.dp

    // Przyciski „zmień” / „usuń” na zdjęciu w edycji dnia
    val PhotoActionSize = 30.dp
    val PhotoActionGap = 6.dp

    // Fala dźwiękowa
    val WaveBarWidth = 4.dp
    val WaveBarGap = 3.dp

    // Ikony
    val IconXs = 16.dp
    val IconS = 18.dp
    val IconM = 24.dp
    val IconL = 32.dp
    val SourceIconBoxSize = 48.dp
}
