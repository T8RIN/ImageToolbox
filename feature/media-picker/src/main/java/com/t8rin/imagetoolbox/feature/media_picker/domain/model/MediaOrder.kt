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

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.ensureActive

sealed class MediaOrder(val orderType: OrderType) {
    class Label(orderType: OrderType) : MediaOrder(orderType)
    class Date(orderType: OrderType) : MediaOrder(orderType)
    class Path(orderType: OrderType) : MediaOrder(orderType)
    class Size(orderType: OrderType) : MediaOrder(orderType)
    class DateTaken(orderType: OrderType) : MediaOrder(orderType)
    class Random(orderType: OrderType = OrderType.Descending) : MediaOrder(orderType)
    class Expiry(orderType: OrderType = OrderType.Descending) : MediaOrder(orderType)

    fun copy(orderType: OrderType): MediaOrder {
        return when (this) {
            is Date -> Date(orderType)
            is Label -> Label(orderType)
            is Expiry -> Expiry(orderType)
            is Path -> Path(orderType)
            is Size -> Size(orderType)
            is DateTaken -> DateTaken(orderType)
            is Random -> Random(orderType)
        }
    }

    suspend fun sortMedia(media: List<Media>): List<Media> = coroutineScope {
        fun sortByText(selector: (Media) -> String): List<Media> {
            val withKeys = media.map {
                ensureActive()
                it to selector(it).lowercase()
            }
            val comparator = compareBy<Pair<Media, String>> {
                ensureActive()
                it.second
            }
            return withKeys.sortedWith(
                if (orderType == OrderType.Ascending) comparator else comparator.reversed()
            ).map { it.first }
        }

        when (orderType) {
            OrderType.Ascending -> {
                when (this@MediaOrder) {
                    is Date -> media.sortedBy { it.timestamp }
                    is Label -> sortByText(Media::label)
                    is Expiry -> media.sortedBy { it.expiryTimestamp ?: it.timestamp }
                    is Path -> sortByText(Media::path)
                    is Size -> media.sortedBy {
                        ensureActive()
                        it.fileSize
                    }

                    is DateTaken -> media.sortedBy { it.dateTakenSeconds }
                    is Random -> media.shuffled()
                }
            }

            OrderType.Descending -> {
                when (this@MediaOrder) {
                    is Date -> media.sortedByDescending { it.timestamp }
                    is Label -> sortByText(Media::label)
                    is Expiry -> media.sortedByDescending { it.expiryTimestamp ?: it.timestamp }
                    is Path -> sortByText(Media::path)
                    is Size -> media.sortedByDescending {
                        ensureActive()
                        it.fileSize
                    }

                    is DateTaken -> media.sortedByDescending { it.dateTakenSeconds }
                    is Random -> media.shuffled()
                }
            }
        }
    }

    fun sortAlbums(albums: List<Album>): List<Album> {
        return when (orderType) {
            OrderType.Ascending -> {
                when (this) {
                    is Date -> albums.sortedBy { it.timestamp }
                    is Label -> albums.sortedBy { it.label.lowercase() }
                    else -> albums
                }
            }

            OrderType.Descending -> {
                when (this) {
                    is Date -> albums.sortedByDescending { it.timestamp }
                    is Label -> albums.sortedByDescending { it.label.lowercase() }
                    else -> albums
                }
            }
        }
    }
}