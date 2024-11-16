/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.feature.media_picker.domain.model

import kotlinx.coroutines.coroutineScope

sealed class MediaOrder(private val orderType: OrderType) {
    class Label(orderType: OrderType) : MediaOrder(orderType)
    class Date(orderType: OrderType) : MediaOrder(orderType)
    class Expiry(orderType: OrderType = OrderType.Descending) : MediaOrder(orderType)

    fun copy(orderType: OrderType): MediaOrder {
        return when (this) {
            is Date -> Date(orderType)
            is Label -> Label(orderType)
            is Expiry -> Expiry(orderType)
        }
    }

    suspend fun sortMedia(media: List<Media>): List<Media> = coroutineScope {
        when (orderType) {
            OrderType.Ascending -> {
                when (this@MediaOrder) {
                    is Date -> media.sortedBy { it.timestamp }
                    is Label -> media.sortedBy { it.label.lowercase() }
                    is Expiry -> media.sortedBy { it.expiryTimestamp ?: it.timestamp }
                }
            }

            OrderType.Descending -> {
                when (this@MediaOrder) {
                    is Date -> media.sortedByDescending { it.timestamp }
                    is Label -> media.sortedByDescending { it.label.lowercase() }
                    is Expiry -> media.sortedByDescending { it.expiryTimestamp ?: it.timestamp }
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