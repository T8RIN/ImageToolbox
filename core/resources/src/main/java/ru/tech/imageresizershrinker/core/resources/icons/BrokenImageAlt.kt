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

val Icons.Rounded.BrokenImageAlt: ImageVector by lazy {
    ImageVector.Builder(
        name = "BrokenImageAlt",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(19f, 3f)
            arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 21f, 5f)
            verticalLineTo(11f)
            horizontalLineTo(19f)
            verticalLineTo(13f)
            horizontalLineTo(19f)
            lineTo(17f, 13f)
            verticalLineTo(15f)
            horizontalLineTo(15f)
            verticalLineTo(17f)
            horizontalLineTo(13f)
            verticalLineTo(19f)
            horizontalLineTo(11f)
            verticalLineTo(21f)
            horizontalLineTo(5f)
            curveTo(3.89f, 21f, 3f, 20.1f, 3f, 19f)
            verticalLineTo(5f)
            arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 5f, 3f)
            horizontalLineTo(19f)
            moveTo(21f, 15f)
            verticalLineTo(19f)
            arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 19f, 21f)
            horizontalLineTo(19f)
            lineTo(15f, 21f)
            verticalLineTo(19f)
            horizontalLineTo(17f)
            verticalLineTo(17f)
            horizontalLineTo(19f)
            verticalLineTo(15f)
            horizontalLineTo(21f)
            moveTo(19f, 8.5f)
            arcTo(0.5f, 0.5f, 0f, isMoreThanHalf = false, isPositiveArc = false, 18.5f, 8f)
            horizontalLineTo(5.5f)
            arcTo(0.5f, 0.5f, 0f, isMoreThanHalf = false, isPositiveArc = false, 5f, 8.5f)
            verticalLineTo(15.5f)
            arcTo(0.5f, 0.5f, 0f, isMoreThanHalf = false, isPositiveArc = false, 5.5f, 16f)
            horizontalLineTo(11f)
            verticalLineTo(15f)
            horizontalLineTo(13f)
            verticalLineTo(13f)
            horizontalLineTo(15f)
            verticalLineTo(11f)
            horizontalLineTo(17f)
            verticalLineTo(9f)
            horizontalLineTo(19f)
            verticalLineTo(8.5f)
            close()
        }
    }.build()
}
