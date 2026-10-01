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
)

fun List<Media>.groupMedia(
    settings: MediaDisplaySettings,
    zoneId: ZoneId = ZoneId.systemDefault(),
    dateLabel: (Long, MediaDateGroup) -> String
): List<MediaItem> {
    fun List<Media>.items() = map {
        MediaItem.MediaViewItem("media_${it.id}_${it.label}", it)
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