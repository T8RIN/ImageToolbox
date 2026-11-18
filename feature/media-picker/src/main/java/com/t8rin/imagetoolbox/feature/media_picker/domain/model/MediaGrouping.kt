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

import java.time.Instant
import java.time.ZoneId
import java.util.Locale

enum class MediaGrouping {
    DateModified, DateTaken, MimeType, Extension
}

enum class MediaDateGroup {
    Year, Month, Day, None
}

data class MediaDisplaySettings(
    val grouping: MediaGrouping = MediaGrouping.DateModified,
    val dateGroup: MediaDateGroup = MediaDateGroup.Day,
    val groupOrder: OrderType = OrderType.Descending,
    val mediaOrder: MediaOrder = MediaOrder.Date(OrderType.Descending)
) {
    fun asString(): String = listOf(
        grouping.name,
        dateGroup.name,
        (groupOrder == OrderType.Ascending).toString(),
        when (mediaOrder) {
            is MediaOrder.Date -> "date"
            is MediaOrder.Label -> "label"
            is MediaOrder.Path -> "path"
            is MediaOrder.Size -> "size"
            is MediaOrder.DateTaken -> "date_taken"
            is MediaOrder.Random -> "random"
            is MediaOrder.Expiry -> "expiry"
        },
        (mediaOrder.orderType == OrderType.Ascending).toString()
    ).joinToString("/")

    companion object {
        fun fromString(string: String?): MediaDisplaySettings? {
            val data = string?.split("/")?.takeIf { it.size == 5 } ?: return null
            val grouping = MediaGrouping.entries.find { it.name == data[0] } ?: return null
            val dateGroup = MediaDateGroup.entries.find { it.name == data[1] } ?: return null
            val groupAscending = data[2].toBooleanStrictOrNull() ?: return null
            val mediaAscending = data[4].toBooleanStrictOrNull() ?: return null
            val order = if (mediaAscending) OrderType.Ascending else OrderType.Descending
            val mediaOrder = when (data[3]) {
                "date" -> MediaOrder.Date(order)
                "label" -> MediaOrder.Label(order)
                "path" -> MediaOrder.Path(order)
                "size" -> MediaOrder.Size(order)
                "date_taken" -> MediaOrder.DateTaken(order)
                "random" -> MediaOrder.Random(order)
                "expiry" -> MediaOrder.Expiry(order)
                else -> return null
            }
            return MediaDisplaySettings(
                grouping = grouping,
                dateGroup = dateGroup,
                groupOrder = if (groupAscending) OrderType.Ascending else OrderType.Descending,
                mediaOrder = mediaOrder
            )
        }
    }
}

fun List<Media>.groupMedia(
    settings: MediaDisplaySettings,
    zoneId: ZoneId = ZoneId.systemDefault(),
    dateLabel: (Long, MediaDateGroup) -> String
): List<MediaItem> {
    fun List<Media>.items() = map {
        MediaItem.MediaViewItem("media_${it.uri}", it)
    }

    if (settings.dateGroup == MediaDateGroup.None) return items()

    val groups = groupBy { media ->
        when (settings.grouping) {
            MediaGrouping.DateModified, MediaGrouping.DateTaken -> {
                val timestamp = if (settings.grouping == MediaGrouping.DateTaken) {
                    media.dateTakenSeconds
                } else media.timestamp
                val date = Instant.ofEpochSecond(timestamp).atZone(zoneId).toLocalDate()
                when (settings.dateGroup) {
                    MediaDateGroup.Year -> date.withDayOfYear(1)
                    MediaDateGroup.Month -> date.withDayOfMonth(1)
                    else -> date
                }.toString()
            }

            MediaGrouping.MimeType -> media.mimeType.lowercase(Locale.ROOT)
            MediaGrouping.Extension -> media.fileExtension.lowercase(Locale.ROOT)
        }
    }.toSortedMap()

    val entries = if (settings.groupOrder == OrderType.Descending) {
        groups.entries.reversed()
    } else groups.entries.toList()

    return entries.flatMap { (key, media) ->
        val first = media.first()
        val title = when (settings.grouping) {
            MediaGrouping.DateModified -> dateLabel(first.timestamp, settings.dateGroup)
            MediaGrouping.DateTaken -> dateLabel(first.dateTakenSeconds, settings.dateGroup)
            else -> key
        }
        listOf(MediaItem.Header("header_${settings.grouping}_$key", title, media)) + media.items()
    }
}

val Media.dateTakenSeconds: Long
    get() = takenTimestamp?.takeIf { it > 0 }?.div(1000) ?: timestamp