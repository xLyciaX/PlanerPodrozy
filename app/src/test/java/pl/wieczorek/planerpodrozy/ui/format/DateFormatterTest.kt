package pl.wieczorek.planerpodrozy.ui.format

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class DateFormatterTest {

    @Test
    fun range_sameMonth() {
        assertEquals(
            "12–18 lipca 2026",
            DateFormatter.range(LocalDate.of(2026, 7, 12), LocalDate.of(2026, 7, 18)),
        )
    }

    @Test
    fun range_differentMonths() {
        assertEquals(
            "30 czerwca – 2 lipca 2026",
            DateFormatter.range(LocalDate.of(2026, 6, 30), LocalDate.of(2026, 7, 2)),
        )
    }

    @Test
    fun range_differentYears() {
        assertEquals(
            "30 grudnia 2025 – 2 stycznia 2026",
            DateFormatter.range(LocalDate.of(2025, 12, 30), LocalDate.of(2026, 1, 2)),
        )
    }

    @Test
    fun range_singleDay() {
        assertEquals(
            "12 lipca 2026",
            DateFormatter.range(LocalDate.of(2026, 7, 12), LocalDate.of(2026, 7, 12)),
        )
    }

    @Test
    fun numericAndDayMonth() {
        val date = LocalDate.of(2026, 7, 2)
        assertEquals("02.07.2026", DateFormatter.numeric(date))
        assertEquals("2 lipca", DateFormatter.dayMonth(date))
    }

    @Test
    fun duration() {
        assertEquals("0:00", DateFormatter.duration(0))
        assertEquals("0:42", DateFormatter.duration(42_400))
        assertEquals("2:18", DateFormatter.duration(138_000))
        assertEquals("1:02:05", DateFormatter.duration(3_725_000))
    }
}
