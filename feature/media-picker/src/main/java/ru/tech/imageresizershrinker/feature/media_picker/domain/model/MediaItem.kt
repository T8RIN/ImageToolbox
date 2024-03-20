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

sealed class MediaItem {

    abstract val key: String

    data class Header(
        override val key: String,
        val text: String,
        val data: List<Media>
    ) : MediaItem()

    data class MediaViewItem(
        override val key: String,
        val media: Media
    ) : MediaItem()

}

val Any.isHeaderKey: Boolean
    get() = this is String && this.startsWith("header_")

val Any.isBigHeaderKey: Boolean
    get() = this is String && this.startsWith("header_big_")

val Any.isIgnoredKey: Boolean
    get() = this is String && this == "aboveGrid"