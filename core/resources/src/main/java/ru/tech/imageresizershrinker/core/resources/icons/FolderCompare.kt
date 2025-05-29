/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.core.resources.icons

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Rounded.FolderCompare: ImageVector by lazy {
    ImageVector.Builder(
        name = "FolderCompare",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(13f, 19f)
            curveTo(13f, 19.34f, 13.04f, 19.67f, 13.09f, 20f)
            horizontalLineTo(4f)
            curveTo(2.9f, 20f, 2f, 19.11f, 2f, 18f)
            verticalLineTo(6f)
            curveTo(2f, 4.89f, 2.89f, 4f, 4f, 4f)
            horizontalLineTo(10f)
            lineTo(12f, 6f)
            horizontalLineTo(20f)
            curveTo(21.1f, 6f, 22f, 6.89f, 22f, 8f)
            verticalLineTo(13.81f)
            curveTo(21.12f, 13.3f, 20.1f, 13f, 19f, 13f)
            curveTo(15.69f, 13f, 13f, 15.69f, 13f, 19f)
            moveTo(23f, 17f)
            lineTo(20f, 14.5f)
            verticalLineTo(16f)
            horizontalLineTo(16f)
            verticalLineTo(18f)
            horizontalLineTo(20f)
            verticalLineTo(19.5f)
            lineTo(23f, 17f)
            moveTo(18f, 18.5f)
            lineTo(15f, 21f)
            lineTo(18f, 23.5f)
            verticalLineTo(22f)
            horizontalLineTo(22f)
            verticalLineTo(20f)
            horizontalLineTo(18f)
            verticalLineTo(18.5f)
            close()
        }
    }.build()
}
