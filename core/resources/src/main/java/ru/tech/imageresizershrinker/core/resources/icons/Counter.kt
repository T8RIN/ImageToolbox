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

val Icons.Outlined.Counter: ImageVector by lazy {
    Builder(
        name = "Counter",
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
            moveTo(4.0f, 4.0f)
            horizontalLineTo(20.0f)
            arcTo(
                2.0f,
                2.0f,
                0.0f,
                isMoreThanHalf = false,
                isPositiveArc = true,
                x1 = 22.0f,
                y1 = 6.0f
            )
            verticalLineTo(18.0f)
            arcTo(
                2.0f,
                2.0f,
                0.0f,
                isMoreThanHalf = false,
                isPositiveArc = true,
                x1 = 20.0f,
                y1 = 20.0f
            )
            horizontalLineTo(4.0f)
            arcTo(
                2.0f,
                2.0f,
                0.0f,
                isMoreThanHalf = false,
                isPositiveArc = true,
                x1 = 2.0f,
                y1 = 18.0f
            )
            verticalLineTo(6.0f)
            arcTo(
                horizontalEllipseRadius = 2.0f,
                verticalEllipseRadius = 2.0f,
                theta = 0.0f,
                isMoreThanHalf = false,
                isPositiveArc = true,
                x1 = 4.0f,
                y1 = 4.0f
            )
            moveTo(4.0f, 6.0f)
            verticalLineTo(18.0f)
            horizontalLineTo(11.0f)
            verticalLineTo(6.0f)
            horizontalLineTo(4.0f)
            moveTo(20.0f, 18.0f)
            verticalLineTo(6.0f)
            horizontalLineTo(18.76f)
            curveTo(19.0f, 6.54f, 18.95f, 7.07f, 18.95f, 7.13f)
            curveTo(18.88f, 7.8f, 18.41f, 8.5f, 18.24f, 8.75f)
            lineTo(15.91f, 11.3f)
            lineTo(19.23f, 11.28f)
            lineTo(19.24f, 12.5f)
            lineTo(14.04f, 12.47f)
            lineTo(14.0f, 11.47f)
            curveTo(14.0f, 11.47f, 17.05f, 8.24f, 17.2f, 7.95f)
            curveTo(17.34f, 7.67f, 17.91f, 6.0f, 16.5f, 6.0f)
            curveTo(15.27f, 6.05f, 15.41f, 7.3f, 15.41f, 7.3f)
            lineTo(13.87f, 7.31f)
            curveTo(13.87f, 7.31f, 13.88f, 6.65f, 14.25f, 6.0f)
            horizontalLineTo(13.0f)
            verticalLineTo(18.0f)
            horizontalLineTo(15.58f)
            lineTo(15.57f, 17.14f)
            lineTo(16.54f, 17.13f)
            curveTo(16.54f, 17.13f, 17.45f, 16.97f, 17.46f, 16.08f)
            curveTo(17.5f, 15.08f, 16.65f, 15.08f, 16.5f, 15.08f)
            curveTo(16.37f, 15.08f, 15.43f, 15.13f, 15.43f, 15.95f)
            horizontalLineTo(13.91f)
            curveTo(13.91f, 15.95f, 13.95f, 13.89f, 16.5f, 13.89f)
            curveTo(19.1f, 13.89f, 18.96f, 15.91f, 18.96f, 15.91f)
            curveTo(18.96f, 15.91f, 19.0f, 17.16f, 17.85f, 17.63f)
            lineTo(18.37f, 18.0f)
            horizontalLineTo(20.0f)
            moveTo(8.92f, 16.0f)
            horizontalLineTo(7.42f)
            verticalLineTo(10.2f)
            lineTo(5.62f, 10.76f)
            verticalLineTo(9.53f)
            lineTo(8.76f, 8.41f)
            horizontalLineTo(8.92f)
            verticalLineTo(16.0f)
            close()
        }
    }.build()
}
