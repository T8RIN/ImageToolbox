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

package ru.tech.imageresizershrinker.core.resources.icons

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Rounded.ClipboardFile: ImageVector by lazy {
    ImageVector.Builder(
        name = "ClipboardFile",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(19f, 3f)
            curveTo(20.1f, 3f, 21f, 3.9f, 21f, 5f)
            verticalLineTo(9.17f)
            lineTo(19.83f, 8f)
            horizontalLineTo(15f)
            curveTo(12.79f, 8f, 11f, 9.79f, 11f, 12f)
            verticalLineTo(21f)
            horizontalLineTo(5f)
            curveTo(3.9f, 21f, 3f, 20.1f, 3f, 19f)
            verticalLineTo(5f)
            curveTo(3f, 3.9f, 3.9f, 3f, 5f, 3f)
            horizontalLineTo(9.18f)
            curveTo(9.6f, 1.84f, 10.7f, 1f, 12f, 1f)
            curveTo(13.3f, 1f, 14.4f, 1.84f, 14.82f, 3f)
            horizontalLineTo(19f)
            moveTo(12f, 3f)
            curveTo(11.45f, 3f, 11f, 3.45f, 11f, 4f)
            curveTo(11f, 4.55f, 11.45f, 5f, 12f, 5f)
            curveTo(12.55f, 5f, 13f, 4.55f, 13f, 4f)
            curveTo(13f, 3.45f, 12.55f, 3f, 12f, 3f)
            moveTo(15f, 23f)
            curveTo(13.9f, 23f, 13f, 22.11f, 13f, 21f)
            verticalLineTo(12f)
            curveTo(13f, 10.9f, 13.9f, 10f, 15f, 10f)
            horizontalLineTo(19f)
            lineTo(23f, 14f)
            verticalLineTo(21f)
            curveTo(23f, 22.11f, 22.11f, 23f, 21f, 23f)
            horizontalLineTo(15f)
            moveTo(21f, 14.83f)
            lineTo(18.17f, 12f)
            horizontalLineTo(18f)
            verticalLineTo(15f)
            horizontalLineTo(21f)
            verticalLineTo(14.83f)
            close()
        }
    }.build()
}
