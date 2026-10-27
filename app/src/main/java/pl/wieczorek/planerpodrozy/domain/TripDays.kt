package pl.wieczorek.planerpodrozy.domain

import java.time.LocalDate

/** Kolejne daty podróży od [from] do [to] włącznie, po jednej na każdy dzień. */
fun tripDates(from: LocalDate, to: LocalDate): List<LocalDate> {
    require(!to.isBefore(from)) { "Koniec podróży ($to) nie może być przed początkiem ($from)" }
    return generateSequence(from) { it.plusDays(1) }
        .takeWhile { !it.isAfter(to) }
        .toList()
}
