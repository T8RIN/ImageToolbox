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
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Outlined.BookmarkOff: ImageVector by lazy {
    Builder(
        name = "Bookmark Off", defaultWidth = 24.0.dp, defaultHeight =
        24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(3.28f, 4.0f)
            lineTo(2.0f, 5.27f)
            lineTo(5.0f, 8.27f)
            verticalLineTo(21.0f)
            lineTo(12.0f, 18.0f)
            lineTo(16.78f, 20.05f)
            lineTo(18.73f, 22.0f)
            lineTo(20.0f, 20.72f)
            lineTo(3.28f, 4.0f)
            moveTo(7.0f, 18.0f)
            verticalLineTo(10.27f)
            lineTo(13.0f, 16.25f)
            lineTo(12.0f, 15.82f)
            lineTo(7.0f, 18.0f)
            moveTo(7.0f, 5.16f)
            lineTo(5.5f, 3.67f)
            curveTo(5.88f, 3.26f, 6.41f, 3.0f, 7.0f, 3.0f)
            horizontalLineTo(17.0f)
            arcTo(
                2.0f, 2.0f, 0.0f,
                isMoreThanHalf = false,
                isPositiveArc = true,
                x1 = 19.0f,
                y1 = 5.0f
            )
            verticalLineTo(17.16f)
            lineTo(17.0f, 15.16f)
            verticalLineTo(5.0f)
            horizontalLineTo(7.0f)
            verticalLineTo(5.16f)
            close()
        }
    }.build()
}