package pl.wieczorek.planerpodrozy.ui.components

import androidx.annotation.StringRes
import pl.wieczorek.planerpodrozy.R
import pl.wieczorek.planerpodrozy.domain.model.PhotoCategory

/** Podpis kategorii na karcie kadru („Krajobraz”, „Jedzenie”, „Selfie”). */
@get:StringRes
val PhotoCategory.labelRes: Int
    get() = when (this) {
        PhotoCategory.LANDSCAPE -> R.string.category_landscape
        PhotoCategory.FOOD -> R.string.category_food
        PhotoCategory.SELFIE -> R.string.category_selfie
    }

/** Nazwa filtra w galerii (liczba mnoga: „Krajobrazy”). */
@get:StringRes
val PhotoCategory.filterLabelRes: Int
    get() = when (this) {
        PhotoCategory.LANDSCAPE -> R.string.gallery_filter_landscape
        PhotoCategory.FOOD -> R.string.gallery_filter_food
        PhotoCategory.SELFIE -> R.string.gallery_filter_selfie
    }
