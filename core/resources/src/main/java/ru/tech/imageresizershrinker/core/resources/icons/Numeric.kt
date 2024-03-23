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

val Icons.Filled.Numeric: ImageVector by lazy {
    Builder(
        name = "Numeric", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
        viewportWidth = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(4.0f, 17.0f)
            verticalLineTo(9.0f)
            horizontalLineTo(2.0f)
            verticalLineTo(7.0f)
            horizontalLineTo(6.0f)
            verticalLineTo(17.0f)
            horizontalLineTo(4.0f)
            moveTo(22.0f, 15.0f)
            curveTo(22.0f, 16.11f, 21.1f, 17.0f, 20.0f, 17.0f)
            horizontalLineTo(16.0f)
            verticalLineTo(15.0f)
            horizontalLineTo(20.0f)
            verticalLineTo(13.0f)
            horizontalLineTo(18.0f)
            verticalLineTo(11.0f)
            horizontalLineTo(20.0f)
            verticalLineTo(9.0f)
            horizontalLineTo(16.0f)
            verticalLineTo(7.0f)
            horizontalLineTo(20.0f)
            arcTo(
                2.0f, 2.0f, 0.0f,
                isMoreThanHalf = false,
                isPositiveArc = true,
                x1 = 22.0f,
                y1 = 9.0f
            )
            verticalLineTo(10.5f)
            arcTo(
                1.5f, 1.5f, 0.0f,
                isMoreThanHalf = false,
                isPositiveArc = true,
                x1 = 20.5f,
                y1 = 12.0f
            )
            arcTo(
                1.5f, 1.5f, 0.0f,
                isMoreThanHalf = false,
                isPositiveArc = true,
                x1 = 22.0f,
                y1 = 13.5f
            )
            verticalLineTo(15.0f)
            moveTo(14.0f, 15.0f)
            verticalLineTo(17.0f)
            horizontalLineTo(8.0f)
            verticalLineTo(13.0f)
            curveTo(8.0f, 11.89f, 8.9f, 11.0f, 10.0f, 11.0f)
            horizontalLineTo(12.0f)
            verticalLineTo(9.0f)
            horizontalLineTo(8.0f)
            verticalLineTo(7.0f)
            horizontalLineTo(12.0f)
            arcTo(
                2.0f, 2.0f, 0.0f,
                isMoreThanHalf = false,
                isPositiveArc = true,
                x1 = 14.0f,
                y1 = 9.0f
            )
            verticalLineTo(11.0f)
            curveTo(14.0f, 12.11f, 13.1f, 13.0f, 12.0f, 13.0f)
            horizontalLineTo(10.0f)
            verticalLineTo(15.0f)
            horizontalLineTo(14.0f)
            close()
        }
    }.build()
}
