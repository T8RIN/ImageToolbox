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

val Icons.Rounded.FileExport: ImageVector by lazy {
    ImageVector.Builder(
        name = "Rounded.FileExport",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(6f, 2f)
            curveTo(4.89f, 2f, 4f, 2.9f, 4f, 4f)
            verticalLineTo(20f)
            arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, 6f, 22f)
            horizontalLineTo(18f)
            arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, 20f, 20f)
            verticalLineTo(8f)
            lineTo(14f, 2f)
            moveTo(13f, 3.5f)
            lineTo(18.5f, 9f)
            horizontalLineTo(13f)
            moveTo(8.93f, 12.22f)
            horizontalLineTo(16f)
            verticalLineTo(19.29f)
            lineTo(13.88f, 17.17f)
            lineTo(11.05f, 20f)
            lineTo(8.22f, 17.17f)
            lineTo(11.05f, 14.35f)
        }
    }.build()
}

val Icons.Outlined.FileExport: ImageVector by lazy {
    ImageVector.Builder(
        name = "Outlined.FileExport",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(14f, 2f)
            horizontalLineTo(6f)
            curveTo(4.9f, 2f, 4f, 2.9f, 4f, 4f)
            verticalLineTo(20f)
            curveTo(4f, 21.1f, 4.9f, 22f, 6f, 22f)
            horizontalLineTo(18f)
            curveTo(19.1f, 22f, 20f, 21.1f, 20f, 20f)
            verticalLineTo(8f)
            lineTo(14f, 2f)
            moveTo(18f, 20f)
            horizontalLineTo(6f)
            verticalLineTo(4f)
            horizontalLineTo(13f)
            verticalLineTo(9f)
            horizontalLineTo(18f)
            verticalLineTo(20f)
            moveTo(16f, 11f)
            verticalLineTo(18.1f)
            lineTo(13.9f, 16f)
            lineTo(11.1f, 18.8f)
            lineTo(8.3f, 16f)
            lineTo(11.1f, 13.2f)
            lineTo(8.9f, 11f)
            horizontalLineTo(16f)
            close()
        }
    }.build()
}