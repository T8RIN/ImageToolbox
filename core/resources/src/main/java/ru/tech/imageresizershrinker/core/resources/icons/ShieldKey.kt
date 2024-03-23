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

val Icons.Rounded.ShieldKey: ImageVector by lazy {
    Builder(
        name = "Shield Key",
        defaultWidth = 24.0.dp,
        defaultHeight = 24.0.dp,
        viewportWidth = 24.0f,
        viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)),
            stroke = null,
            strokeLineWidth = 0.0f,
            strokeLineCap = Butt,
            strokeLineJoin = Miter,
            strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(12.0f, 8.0f)
            arcTo(
                1.0f, 1.0f, 0.0f,
                isMoreThanHalf = false,
                isPositiveArc = true,
                x1 = 13.0f,
                y1 = 9.0f
            )
            arcTo(
                1.0f, 1.0f, 0.0f,
                isMoreThanHalf = false,
                isPositiveArc = true,
                x1 = 12.0f,
                y1 = 10.0f
            )
            arcTo(
                1.0f, 1.0f, 0.0f,
                isMoreThanHalf = false,
                isPositiveArc = true,
                x1 = 11.0f,
                y1 = 9.0f
            )
            arcTo(
                1.0f, 1.0f, 0.0f,
                isMoreThanHalf = false,
                isPositiveArc = true,
                x1 = 12.0f,
                y1 = 8.0f
            )
            moveTo(21.0f, 11.0f)
            curveTo(21.0f, 16.55f, 17.16f, 21.74f, 12.0f, 23.0f)
            curveTo(6.84f, 21.74f, 3.0f, 16.55f, 3.0f, 11.0f)
            verticalLineTo(5.0f)
            lineTo(12.0f, 1.0f)
            lineTo(21.0f, 5.0f)
            verticalLineTo(11.0f)
            moveTo(12.0f, 6.0f)
            arcTo(
                3.0f, 3.0f, 0.0f,
                isMoreThanHalf = false,
                isPositiveArc = false,
                x1 = 9.0f,
                y1 = 9.0f
            )
            curveTo(9.0f, 10.31f, 9.83f, 11.42f, 11.0f, 11.83f)
            verticalLineTo(18.0f)
            horizontalLineTo(13.0f)
            verticalLineTo(16.0f)
            horizontalLineTo(15.0f)
            verticalLineTo(14.0f)
            horizontalLineTo(13.0f)
            verticalLineTo(11.83f)
            curveTo(14.17f, 11.42f, 15.0f, 10.31f, 15.0f, 9.0f)
            arcTo(
                3.0f, 3.0f, 0.0f,
                isMoreThanHalf = false,
                isPositiveArc = false,
                x1 = 12.0f,
                y1 = 6.0f
            )
            close()
        }
    }.build()
}
