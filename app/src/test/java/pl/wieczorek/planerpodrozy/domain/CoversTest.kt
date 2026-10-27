package pl.wieczorek.planerpodrozy.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import pl.wieczorek.planerpodrozy.domain.model.Photo
import pl.wieczorek.planerpodrozy.domain.model.PhotoCategory
import java.time.Instant

class CoversTest {

    private fun photo(id: Long, dayId: Long, addedAtSeconds: Long) = Photo(
        id = id,
        dayId = dayId,
        category = PhotoCategory.LANDSCAPE,
        filePath = "photo_$id.jpg",
        addedAt = Instant.ofEpochSecond(addedAtSeconds),
    )

    @Test
    fun noPhotosMeansNoCover() {
        assertNull(coverPhoto(emptyList()))
    }

    @Test
    fun earliestAddedWinsRegardlessOfDay() {
        // Zdjęcie z dnia 3 dodane wcześniej niż zdjęcie z dnia 1.
        val fromDay1 = photo(id = 1, dayId = 1, addedAtSeconds = 200)
        val fromDay3 = photo(id = 2, dayId = 3, addedAtSeconds = 100)
        assertEquals(fromDay3, coverPhoto(listOf(fromDay1, fromDay3)))
    }

    @Test
    fun afterRemovingCoverNextEarliestTakesOver() {
        val first = photo(id = 1, dayId = 1, addedAtSeconds = 100)
        val second = photo(id = 2, dayId = 1, addedAtSeconds = 200)
        val third = photo(id = 3, dayId = 2, addedAtSeconds = 300)
        val all = listOf(third, first, second)
        assertEquals(first, coverPhoto(all))
        assertEquals(second, coverPhoto(all - first))
    }

    @Test
    fun sameTimestampFallsBackToId() {
        val a = photo(id = 5, dayId = 1, addedAtSeconds = 100)
        val b = photo(id = 2, dayId = 1, addedAtSeconds = 100)
        assertEquals(b, coverPhoto(listOf(a, b)))
    }
}
