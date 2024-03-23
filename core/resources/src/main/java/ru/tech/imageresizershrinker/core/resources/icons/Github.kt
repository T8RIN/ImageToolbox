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
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Rounded.Github: ImageVector by lazy {
    ImageVector.Builder(
        name = "Github", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
        viewportWidth = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)),
            stroke = null,
            strokeLineWidth = 0.0f,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Miter,
            strokeLineMiter = 4.0f,
            pathFillType = PathFillType.NonZero
        ) {
            moveTo(12.0f, 2.0f)
            arcTo(
                10.0f, 10.0f, 0.0f,
                isMoreThanHalf = false,
                isPositiveArc = false,
                x1 = 2.0f,
                y1 = 12.0f
            )
            curveTo(2.0f, 16.42f, 4.87f, 20.17f, 8.84f, 21.5f)
            curveTo(9.34f, 21.58f, 9.5f, 21.27f, 9.5f, 21.0f)
            curveTo(9.5f, 20.77f, 9.5f, 20.14f, 9.5f, 19.31f)
            curveTo(6.73f, 19.91f, 6.14f, 17.97f, 6.14f, 17.97f)
            curveTo(5.68f, 16.81f, 5.03f, 16.5f, 5.03f, 16.5f)
            curveTo(4.12f, 15.88f, 5.1f, 15.9f, 5.1f, 15.9f)
            curveTo(6.1f, 15.97f, 6.63f, 16.93f, 6.63f, 16.93f)
            curveTo(7.5f, 18.45f, 8.97f, 18.0f, 9.54f, 17.76f)
            curveTo(9.63f, 17.11f, 9.89f, 16.67f, 10.17f, 16.42f)
            curveTo(7.95f, 16.17f, 5.62f, 15.31f, 5.62f, 11.5f)
            curveTo(5.62f, 10.39f, 6.0f, 9.5f, 6.65f, 8.79f)
            curveTo(6.55f, 8.54f, 6.2f, 7.5f, 6.75f, 6.15f)
            curveTo(6.75f, 6.15f, 7.59f, 5.88f, 9.5f, 7.17f)
            curveTo(10.29f, 6.95f, 11.15f, 6.84f, 12.0f, 6.84f)
            curveTo(12.85f, 6.84f, 13.71f, 6.95f, 14.5f, 7.17f)
            curveTo(16.41f, 5.88f, 17.25f, 6.15f, 17.25f, 6.15f)
            curveTo(17.8f, 7.5f, 17.45f, 8.54f, 17.35f, 8.79f)
            curveTo(18.0f, 9.5f, 18.38f, 10.39f, 18.38f, 11.5f)
            curveTo(18.38f, 15.32f, 16.04f, 16.16f, 13.81f, 16.41f)
            curveTo(14.17f, 16.72f, 14.5f, 17.33f, 14.5f, 18.26f)
            curveTo(14.5f, 19.6f, 14.5f, 20.68f, 14.5f, 21.0f)
            curveTo(14.5f, 21.27f, 14.66f, 21.59f, 15.17f, 21.5f)
            curveTo(19.14f, 20.16f, 22.0f, 16.42f, 22.0f, 12.0f)
            arcTo(
                10.0f, 10.0f, 0.0f,
                isMoreThanHalf = false,
                isPositiveArc = false,
                x1 = 12.0f,
                y1 = 2.0f
            )
            close()
        }
    }.build()
}