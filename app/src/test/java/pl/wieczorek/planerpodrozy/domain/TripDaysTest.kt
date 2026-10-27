package pl.wieczorek.planerpodrozy.domain

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class TripDaysTest {

    @Test
    fun generatesOneDatePerDayInclusive() {
        val dates = tripDates(LocalDate.of(2026, 7, 12), LocalDate.of(2026, 7, 18))
        assertEquals(7, dates.size)
        assertEquals(LocalDate.of(2026, 7, 12), dates.first())
        assertEquals(LocalDate.of(2026, 7, 18), dates.last())
    }

    @Test
    fun singleDayTrip() {
        val day = LocalDate.of(2026, 7, 12)
        assertEquals(listOf(day), tripDates(day, day))
    }

    @Test
    fun crossesMonthAndYear() {
        val dates = tripDates(LocalDate.of(2025, 12, 30), LocalDate.of(2026, 1, 2))
        assertEquals(4, dates.size)
        assertEquals(LocalDate.of(2026, 1, 1), dates[2])
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsEndBeforeStart() {
        tripDates(LocalDate.of(2026, 7, 18), LocalDate.of(2026, 7, 12))
    }
}
