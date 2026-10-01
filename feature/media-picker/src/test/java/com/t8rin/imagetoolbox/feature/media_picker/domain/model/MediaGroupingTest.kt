/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package com.t8rin.imagetoolbox.feature.media_picker.domain.model

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.ZoneId

class MediaGroupingTest {

    private val utc = ZoneId.of("UTC")

    @Test
    fun monthGroupsKeepYearsSeparateAndSortChronologically() {
        val media = listOf(
            media(1, "2025-01-01T00:00:00Z"),
            media(2, "2026-01-01T00:00:00Z"),
            media(3, "2025-12-31T00:00:00Z")
        )
        val headers = media.groupMedia(
            MediaDisplaySettings(dateGroup = MediaDateGroup.Month), utc
        ) { _, _ -> "Same label" }.filterIsInstance<MediaItem.Header>()

        assertEquals(listOf(2L, 3L, 1L), headers.map { it.data.single().id })
        assertEquals(3, headers.map { it.key }.distinct().size)
    }

    @Test
    fun yearGroupingAndFileSortingHaveIndependentOrders() = runBlocking {
        val media = listOf(
            media(1, "2025-01-01T00:00:00Z", "z.jpg"),
            media(2, "2026-01-01T00:00:00Z", "b.jpg"),
            media(3, "2025-12-31T00:00:00Z", "a.jpg")
        )
        val sorted = MediaOrder.Label(OrderType.Ascending).sortMedia(media)
        val headers = sorted.groupMedia(
            MediaDisplaySettings(dateGroup = MediaDateGroup.Year), utc
        ) { _, _ -> "Year" }.filterIsInstance<MediaItem.Header>()

        assertEquals(listOf(listOf(2L), listOf(3L, 1L)), headers.map { it.data.map(Media::id) })
    }

    @Test
    fun allModePreservesFileOrderWithoutHeaders() {
        val media = listOf(media(2), media(1))
        val items = media.groupMedia(
            MediaDisplaySettings(dateGroup = MediaDateGroup.None), utc
        ) { _, _ -> error("All mode should not format dates") }

        assertTrue(items.all { it is MediaItem.MediaViewItem })
        assertEquals(media, items.filterIsInstance<MediaItem.MediaViewItem>().map { it.media })
    }

    @Test
    fun dayGroupingUsesLocalCalendarBoundaries() {
        val media = listOf(
            media(1, "2026-01-01T20:59:00Z"),
            media(2, "2026-01-01T21:01:00Z")
        )
        val items = media.groupMedia(
            MediaDisplaySettings(), ZoneId.of("Europe/Moscow")
        ) { _, _ -> "Day" }

        assertEquals(2, items.filterIsInstance<MediaItem.Header>().size)
    }

    @Test
    fun dateTakenUsesMillisecondsAndFallsBackForMissingDates() = runBlocking {
        val first = media(1, "2026-01-01T00:00:00Z").copy(
            takenTimestamp = Instant.parse("2025-12-01T00:00:00Z").toEpochMilli()
        )
        val second = media(2, "2025-12-31T00:00:00Z").copy(takenTimestamp = 0)
        val third = media(3, "2025-12-30T00:00:00Z")
        val media =
            MediaOrder.DateTaken(OrderType.Descending).sortMedia(listOf(first, second, third))
        val headers = media.groupMedia(
            MediaDisplaySettings(
                grouping = MediaGrouping.DateTaken,
                dateGroup = MediaDateGroup.Month
            ), utc
        ) { _, _ -> "Month" }.filterIsInstance<MediaItem.Header>()

        assertEquals(listOf(2L, 3L, 1L), media.map(Media::id))
        assertEquals(1, headers.size)
        assertEquals(media, headers.single().data)
    }

    @Test
    fun sizeSortingUsesLoadedSizesWithoutQueryingUris() = runBlocking {
        val media = listOf(
            media(1).copy(size = 100),
            media(2).copy(size = 300),
            media(3).copy(size = 0)
        )

        assertEquals(
            listOf(3L, 1L, 2L),
            MediaOrder.Size(OrderType.Ascending).sortMedia(media).map(Media::id)
        )
        assertEquals(
            listOf(2L, 1L, 3L),
            MediaOrder.Size(OrderType.Descending).sortMedia(media).map(Media::id)
        )
    }

    @Test
    fun textSortingIgnoresCaseAndPreservesOrderForEqualKeys() = runBlocking {
        val media = listOf(
            media(1, label = "b.jpg").copy(path = "/Pictures/Z/one.JPG"),
            media(2, label = "a.JPG").copy(path = "/Pictures/a/two.jpg"),
            media(3, label = "A.jpg").copy(path = "/pictures/A/TWO.jpg")
        )
        listOf(
            MediaOrder.Label(OrderType.Ascending),
            MediaOrder.Path(OrderType.Ascending)
        ).forEach { order ->
            assertEquals(listOf(2L, 3L, 1L), order.sortMedia(media).map(Media::id))
            assertEquals(
                listOf(1L, 2L, 3L),
                order.copy(OrderType.Descending).sortMedia(media).map(Media::id)
            )
        }
    }

    @Test
    fun emptyMediaDoesNotCreateHeaders() {
        assertTrue(emptyList<Media>().groupMedia(MediaDisplaySettings(), utc) { _, _ -> "" }
            .isEmpty())
    }

    private fun media(
        id: Long,
        date: String = "2026-01-01T00:00:00Z",
        label: String = "$id.jpg"
    ) = Media(
        id = id,
        label = label,
        uri = "content://media/$id",
        path = "/Pictures/$label",
        relativePath = "Pictures/",
        albumID = 1,
        albumLabel = "Pictures",
        timestamp = Instant.parse(date).epochSecond,
        mimeType = "image/jpeg"
    )
}