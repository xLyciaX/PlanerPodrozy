package pl.wieczorek.planerpodrozy.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes

val Shapes = Shapes(
    // chipy lokalizacji w edycji dnia
    small = RoundedCornerShape(Dimens.CornerSmall),
    // ikona źródła w bottom sheecie
    medium = RoundedCornerShape(Dimens.CornerMedium),
    // karta podróży, kafelek dnia, pola formularza, miniatury galerii
    large = RoundedCornerShape(Dimens.CornerLarge),
    // karty kadrów, opis dnia, odtwarzacze
    extraLarge = RoundedCornerShape(Dimens.CornerExtraLarge),
)
