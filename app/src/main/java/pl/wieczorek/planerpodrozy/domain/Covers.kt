package pl.wieczorek.planerpodrozy.domain

import pl.wieczorek.planerpodrozy.domain.model.Photo

/**
 * Reguła okładek (jedyne miejsce w aplikacji): okładką podróży albo dnia jest
 * najwcześniej dodane zdjęcie, wg znacznika czasu dodania, a nie numeru dnia.
 * Przy równym czasie decyduje kolejność zapisu w bazie (mniejsze id).
 * Po usunięciu okładki ta sama funkcja wskaże kolejne zdjęcie, a gdy zdjęć brak, zwróci null.
 */
fun coverPhoto(photos: Collection<Photo>): Photo? =
    photos.minWithOrNull(compareBy<Photo>({ it.addedAt }, { it.id }))
