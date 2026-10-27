package pl.wieczorek.planerpodrozy.ui.format

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Jedyne miejsce formatowania dat i czasów w aplikacji (formaty z makiety). */
object DateFormatter {

    val PolishLocale: Locale = Locale.forLanguageTag("pl-PL")

    // „MMMM” w formacie daty daje po polsku dopełniacz: „12 lipca”.
    private val dayMonth = DateTimeFormatter.ofPattern("d MMMM", PolishLocale)
    private val dayMonthYear = DateTimeFormatter.ofPattern("d MMMM yyyy", PolishLocale)
    private val numeric = DateTimeFormatter.ofPattern("dd.MM.yyyy", PolishLocale)

    /**
     * Zakres podróży, np.:
     * - „12–18 lipca 2026” (ten sam miesiąc),
     * - „30 czerwca – 2 lipca 2026” (ten sam rok),
     * - „30 grudnia 2025 – 2 stycznia 2026”,
     * - „12 lipca 2026” (podróż jednodniowa).
     */
    fun range(from: LocalDate, to: LocalDate): String = when {
        from == to -> from.format(dayMonthYear)
        from.year == to.year && from.month == to.month ->
            "${from.dayOfMonth}–${to.format(dayMonthYear)}"
        from.year == to.year -> "${from.format(dayMonth)} – ${to.format(dayMonthYear)}"
        else -> "${from.format(dayMonthYear)} – ${to.format(dayMonthYear)}"
    }

    /** „12.07.2026” */
    fun numeric(date: LocalDate): String = date.format(numeric)

    /** „12 lipca” (kafelek dnia) */
    fun dayMonth(date: LocalDate): String = date.format(dayMonth)

    /** Czas odtwarzania, np. „0:42”, „2:18”, „1:02:05”. */
    fun duration(millis: Long): String {
        val totalSeconds = (millis.coerceAtLeast(0) / 1000)
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return if (hours > 0) {
            "%d:%02d:%02d".format(hours, minutes, seconds)
        } else {
            "%d:%02d".format(minutes, seconds)
        }
    }
}
