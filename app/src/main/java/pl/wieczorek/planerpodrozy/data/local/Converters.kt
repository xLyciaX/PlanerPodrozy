package pl.wieczorek.planerpodrozy.data.local

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json
import java.time.Instant
import java.time.LocalDate

/** Zamiana typów, których SQLite nie zna, na liczby i tekst (i z powrotem). */
class Converters {
    @TypeConverter
    fun localDateToEpochDay(date: LocalDate): Long = date.toEpochDay()

    @TypeConverter
    fun epochDayToLocalDate(epochDay: Long): LocalDate = LocalDate.ofEpochDay(epochDay)

    @TypeConverter
    fun instantToMillis(instant: Instant): Long = instant.toEpochMilli()

    @TypeConverter
    fun millisToInstant(millis: Long): Instant = Instant.ofEpochMilli(millis)

    // Lista lokalizacji zapisana jako tekst JSON, np. ["Positano, Włochy","Amalfi, Włochy"].
    @TypeConverter
    fun stringListToJson(list: List<String>): String = Json.encodeToString(list)

    @TypeConverter
    fun jsonToStringList(json: String): List<String> = Json.decodeFromString(json)
}
