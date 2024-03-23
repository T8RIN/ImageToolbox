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

val Icons.Rounded.QualityMedium: ImageVector by lazy {
    Builder(
        name = "Quality Medium", defaultWidth = 24.0.dp, defaultHeight =
        24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(21.0f, 6.0f)
            verticalLineTo(18.0f)
            arcTo(
                2.0f, 2.0f, 0.0f,
                isMoreThanHalf = false,
                isPositiveArc = true,
                x1 = 19.0f,
                y1 = 20.0f
            )
            horizontalLineTo(5.0f)
            arcTo(
                2.0f, 2.0f, 0.0f,
                isMoreThanHalf = false,
                isPositiveArc = true,
                x1 = 3.0f,
                y1 = 18.0f
            )
            verticalLineTo(6.0f)
            arcTo(
                2.0f, 2.0f, 0.0f,
                isMoreThanHalf = false,
                isPositiveArc = true,
                x1 = 5.0f,
                y1 = 4.0f
            )
            horizontalLineTo(19.0f)
            arcTo(
                2.0f, 2.0f, 0.0f,
                isMoreThanHalf = false,
                isPositiveArc = true,
                x1 = 21.0f,
                y1 = 6.0f
            )
            moveTo(12.0f, 10.0f)
            curveTo(12.0f, 9.5f, 11.5f, 9.0f, 11.0f, 9.0f)
            horizontalLineTo(6.5f)
            curveTo(6.0f, 9.0f, 5.5f, 9.5f, 5.5f, 10.0f)
            verticalLineTo(15.0f)
            horizontalLineTo(7.0f)
            verticalLineTo(10.5f)
            horizontalLineTo(8.0f)
            verticalLineTo(14.0f)
            horizontalLineTo(9.5f)
            verticalLineTo(10.5f)
            horizontalLineTo(10.5f)
            verticalLineTo(15.0f)
            horizontalLineTo(12.0f)
            verticalLineTo(10.0f)
            moveTo(14.5f, 9.0f)
            arcTo(
                1.0f, 1.0f, 0.0f,
                isMoreThanHalf = false,
                isPositiveArc = false,
                x1 = 13.5f,
                y1 = 10.0f
            )
            verticalLineTo(14.0f)
            arcTo(
                1.0f, 1.0f, 0.0f,
                isMoreThanHalf = false,
                isPositiveArc = false,
                x1 = 14.5f,
                y1 = 15.0f
            )
            horizontalLineTo(15.5f)
            verticalLineTo(16.5f)
            horizontalLineTo(16.75f)
            verticalLineTo(15.0f)
            horizontalLineTo(17.5f)
            arcTo(
                1.0f, 1.0f, 0.0f,
                isMoreThanHalf = false,
                isPositiveArc = false,
                x1 = 18.5f,
                y1 = 14.0f
            )
            verticalLineTo(10.0f)
            arcTo(
                1.0f, 1.0f, 0.0f,
                isMoreThanHalf = false,
                isPositiveArc = false,
                x1 = 17.5f,
                y1 = 9.0f
            )
            horizontalLineTo(14.5f)
            moveTo(15.0f, 10.5f)
            horizontalLineTo(17.0f)
            verticalLineTo(13.5f)
            horizontalLineTo(15.0f)
            verticalLineTo(10.5f)
            close()
        }
    }.build()
}