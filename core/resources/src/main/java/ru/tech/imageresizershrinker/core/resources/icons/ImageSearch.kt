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

val Icons.Rounded.ImageSearch: ImageVector by lazy {
    Builder(
        name = "ImageSearch", defaultWidth = 24.0.dp, defaultHeight =
        24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(15.5f, 2.0f)
            curveTo(18.0f, 2.0f, 20.0f, 4.0f, 20.0f, 6.5f)
            curveTo(20.0f, 7.38f, 19.75f, 8.21f, 19.31f, 8.9f)
            lineTo(22.39f, 12.0f)
            lineTo(21.0f, 13.39f)
            lineTo(17.88f, 10.32f)
            curveTo(17.19f, 10.75f, 16.37f, 11.0f, 15.5f, 11.0f)
            curveTo(13.0f, 11.0f, 11.0f, 9.0f, 11.0f, 6.5f)
            curveTo(11.0f, 4.0f, 13.0f, 2.0f, 15.5f, 2.0f)
            moveTo(15.5f, 4.0f)
            arcTo(
                2.5f, 2.5f, 0.0f,
                isMoreThanHalf = false,
                isPositiveArc = false,
                x1 = 13.0f,
                y1 = 6.5f
            )
            arcTo(
                2.5f, 2.5f, 0.0f,
                isMoreThanHalf = false,
                isPositiveArc = false,
                x1 = 15.5f,
                y1 = 9.0f
            )
            arcTo(
                2.5f, 2.5f, 0.0f,
                isMoreThanHalf = false,
                isPositiveArc = false,
                x1 = 18.0f,
                y1 = 6.5f
            )
            arcTo(
                2.5f, 2.5f, 0.0f,
                isMoreThanHalf = false,
                isPositiveArc = false,
                x1 = 15.5f,
                y1 = 4.0f
            )
            moveTo(7.5f, 14.5f)
            lineTo(4.0f, 19.0f)
            horizontalLineTo(18.0f)
            lineTo(13.5f, 13.0f)
            lineTo(10.0f, 17.5f)
            lineTo(7.5f, 14.5f)
            moveTo(20.0f, 20.0f)
            arcTo(
                2.0f, 2.0f, 0.0f,
                isMoreThanHalf = false,
                isPositiveArc = true,
                x1 = 18.0f,
                y1 = 22.0f
            )
            horizontalLineTo(4.0f)
            arcTo(
                2.0f, 2.0f, 0.0f,
                isMoreThanHalf = false,
                isPositiveArc = true,
                x1 = 2.0f,
                y1 = 20.0f
            )
            verticalLineTo(6.0f)
            arcTo(
                2.0f, 2.0f, 0.0f,
                isMoreThanHalf = false,
                isPositiveArc = true,
                x1 = 4.0f,
                y1 = 4.0f
            )
            horizontalLineTo(9.5f)
            curveTo(9.18f, 4.77f, 9.0f, 5.61f, 9.0f, 6.5f)
            arcTo(
                6.5f, 6.5f, 0.0f,
                isMoreThanHalf = false,
                isPositiveArc = false,
                x1 = 15.5f,
                y1 = 13.0f
            )
            curveTo(16.18f, 13.0f, 16.84f, 12.89f, 17.46f, 12.7f)
            lineTo(20.0f, 15.24f)
            verticalLineTo(20.0f)
            close()
        }
    }.build()
}