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

val Icons.Outlined.Cool: ImageVector by lazy {
    Builder(
        name = "Cool", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
        viewportWidth = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(19.0f, 10.0f)
            curveTo(19.0f, 11.38f, 16.88f, 12.5f, 15.5f, 12.5f)
            curveTo(14.12f, 12.5f, 12.75f, 11.38f, 12.75f, 10.0f)
            horizontalLineTo(11.25f)
            curveTo(11.25f, 11.38f, 9.88f, 12.5f, 8.5f, 12.5f)
            curveTo(7.12f, 12.5f, 5.0f, 11.38f, 5.0f, 10.0f)
            horizontalLineTo(4.25f)
            curveTo(4.09f, 10.64f, 4.0f, 11.31f, 4.0f, 12.0f)
            arcTo(
                8.0f, 8.0f, 0.0f,
                isMoreThanHalf = false,
                isPositiveArc = false,
                x1 = 12.0f,
                y1 = 20.0f
            )
            arcTo(
                8.0f, 8.0f, 0.0f,
                isMoreThanHalf = false,
                isPositiveArc = false,
                x1 = 20.0f,
                y1 = 12.0f
            )
            curveTo(20.0f, 11.31f, 19.91f, 10.64f, 19.75f, 10.0f)
            horizontalLineTo(19.0f)
            moveTo(12.0f, 4.0f)
            curveTo(9.04f, 4.0f, 6.45f, 5.61f, 5.07f, 8.0f)
            horizontalLineTo(18.93f)
            curveTo(17.55f, 5.61f, 14.96f, 4.0f, 12.0f, 4.0f)
            moveTo(22.0f, 12.0f)
            arcTo(
                10.0f, 10.0f, 0.0f,
                isMoreThanHalf = false,
                isPositiveArc = true,
                x1 = 12.0f,
                y1 = 22.0f
            )
            arcTo(
                10.0f, 10.0f, 0.0f,
                isMoreThanHalf = false,
                isPositiveArc = true,
                x1 = 2.0f,
                y1 = 12.0f
            )
            arcTo(
                10.0f, 10.0f, 0.0f,
                isMoreThanHalf = false,
                isPositiveArc = true,
                x1 = 12.0f,
                y1 = 2.0f
            )
            arcTo(
                10.0f, 10.0f, 0.0f,
                isMoreThanHalf = false,
                isPositiveArc = true,
                x1 = 22.0f,
                y1 = 12.0f
            )
            moveTo(12.0f, 17.23f)
            curveTo(10.25f, 17.23f, 8.71f, 16.5f, 7.81f, 15.42f)
            lineTo(9.23f, 14.0f)
            curveTo(9.68f, 14.72f, 10.75f, 15.23f, 12.0f, 15.23f)
            curveTo(13.25f, 15.23f, 14.32f, 14.72f, 14.77f, 14.0f)
            lineTo(16.19f, 15.42f)
            curveTo(15.29f, 16.5f, 13.75f, 17.23f, 12.0f, 17.23f)
            close()
        }
    }.build()
}