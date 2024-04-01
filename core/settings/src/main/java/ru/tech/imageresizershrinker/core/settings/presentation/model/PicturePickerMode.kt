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

package ru.tech.imageresizershrinker.core.settings.presentation.model

import android.os.Build
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Wallpaper
import androidx.compose.material.icons.outlined.WebStories
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.ui.graphics.vector.ImageVector
import ru.tech.imageresizershrinker.core.resources.R

sealed class PicturePickerMode(
    val ordinal: Int,
    val icon: ImageVector,
    val title: Int,
    val subtitle: Int
) {
    data object Embedded : PicturePickerMode(
        ordinal = 0,
        icon = Icons.Outlined.Wallpaper,
        title = R.string.embedded_picker,
        subtitle = R.string.embedded_picker_sub
    )

    data object PhotoPicker : PicturePickerMode(
        ordinal = 1,
        icon = Icons.Outlined.WebStories,
        title = R.string.photo_picker,
        subtitle = R.string.photo_picker_sub
    )

    data object Gallery : PicturePickerMode(
        ordinal = 2,
        icon = Icons.Outlined.Image,
        title = R.string.gallery_picker,
        subtitle = R.string.gallery_picker_sub
    )

    data object GetContent : PicturePickerMode(
        ordinal = 3,
        icon = Icons.Rounded.FolderOpen,
        title = R.string.file_explorer_picker,
        subtitle = R.string.file_explorer_picker_sub
    )

    data object CameraCapture : PicturePickerMode(
        ordinal = 4,
        icon = Icons.Outlined.CameraAlt,
        title = R.string.camera,
        subtitle = R.string.camera_sub
    )

    companion object {

        val SafeEmbedded by lazy {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Embedded
            } else PhotoPicker
        }

        fun fromInt(
            ordinal: Int
        ) = when (ordinal) {
            0 -> SafeEmbedded
            1 -> PhotoPicker
            2 -> Gallery
            3 -> GetContent
            4 -> CameraCapture
            else -> SafeEmbedded
        }

        val entries by lazy {
            listOf(
                SafeEmbedded, PhotoPicker, Gallery, GetContent, CameraCapture
            ).distinct()
        }
    }
}