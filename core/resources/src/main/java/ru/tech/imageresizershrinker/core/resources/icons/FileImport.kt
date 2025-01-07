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

val Icons.Rounded.FileImport: ImageVector by lazy {
    ImageVector.Builder(
        name = "Rounded.FileImport",
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
            moveTo(10.05f, 11.22f)
            lineTo(12.88f, 14.05f)
            lineTo(15f, 11.93f)
            verticalLineTo(19f)
            horizontalLineTo(7.93f)
            lineTo(10.05f, 16.88f)
            lineTo(7.22f, 14.05f)
        }
    }.build()
}

val Icons.Outlined.FileImport: ImageVector by lazy {
    ImageVector.Builder(
        name = "Outlined.FileImport",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(14f, 2f)
            horizontalLineTo(6f)
            curveTo(4.89f, 2f, 4f, 2.9f, 4f, 4f)
            verticalLineTo(20f)
            curveTo(4f, 21.11f, 4.89f, 22f, 6f, 22f)
            horizontalLineTo(18f)
            curveTo(19.11f, 22f, 20f, 21.11f, 20f, 20f)
            verticalLineTo(8f)
            lineTo(14f, 2f)
            moveTo(18f, 20f)
            horizontalLineTo(6f)
            verticalLineTo(4f)
            horizontalLineTo(13f)
            verticalLineTo(9f)
            horizontalLineTo(18f)
            verticalLineTo(20f)
            moveTo(15f, 11.93f)
            verticalLineTo(19f)
            horizontalLineTo(7.93f)
            lineTo(10.05f, 16.88f)
            lineTo(7.22f, 14.05f)
            lineTo(10.05f, 11.22f)
            lineTo(12.88f, 14.05f)
            lineTo(15f, 11.93f)
            close()
        }
    }.build()
}
