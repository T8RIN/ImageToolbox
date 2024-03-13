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
package ru.tech.imageresizershrinker.media_picker.domain.model

import android.net.Uri
import android.os.Parcelable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Immutable
@Parcelize
data class Album(
    val id: Long = 0,
    val label: String,
    val uri: Uri,
    val pathToThumbnail: String,
    val relativePath: String,
    val timestamp: Long,
    var count: Long = 0,
    val selected: Boolean = false,
    val isPinned: Boolean = false,
) : Parcelable {

    @IgnoredOnParcel
    @Stable
    val volume: String =
        pathToThumbnail.substringBeforeLast("/").removeSuffix(relativePath.removeSuffix("/"))

    @IgnoredOnParcel
    @Stable
    val isOnSdcard: Boolean =
        volume.toLowerCase(Locale.current).matches(".*[0-9a-f]{4}-[0-9a-f]{4}".toRegex())

    companion object {

        val NewAlbum = Album(
            id = -200,
            label = "New Album",
            uri = Uri.EMPTY,
            pathToThumbnail = "",
            relativePath = "",
            timestamp = 0,
            count = 0,
        )
    }
}