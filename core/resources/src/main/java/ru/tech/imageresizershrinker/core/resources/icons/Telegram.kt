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

val Icons.Rounded.Telegram: ImageVector by lazy {
    ImageVector.Builder(
        name = "Telegram", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
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
            moveTo(11.944f, 0.0f)
            arcTo(
                12.0f, 12.0f, 0.0f,
                isMoreThanHalf = false,
                isPositiveArc = false,
                x1 = 0.0f,
                y1 = 12.0f
            )
            arcToRelative(
                12.0f, 12.0f, 0.0f,
                isMoreThanHalf = false,
                isPositiveArc = false,
                dx1 = 12.0f,
                dy1 = 12.0f
            )
            arcToRelative(
                12.0f, 12.0f, 0.0f,
                isMoreThanHalf = false,
                isPositiveArc = false,
                dx1 = 12.0f,
                dy1 = -12.0f
            )
            arcTo(
                12.0f, 12.0f, 0.0f,
                isMoreThanHalf = false,
                isPositiveArc = false,
                x1 = 12.0f,
                y1 = 0.0f
            )
            arcToRelative(
                12.0f, 12.0f, 0.0f,
                isMoreThanHalf = false,
                isPositiveArc = false,
                dx1 = -0.056f,
                dy1 = 0.0f
            )
            close()
            moveTo(16.906f, 7.224f)
            curveToRelative(0.1f, -0.002f, 0.321f, 0.023f, 0.465f, 0.14f)
            arcToRelative(
                0.506f, 0.506f, 0.0f,
                isMoreThanHalf = false,
                isPositiveArc = true,
                dx1 = 0.171f,
                dy1 = 0.325f
            )
            curveToRelative(0.016f, 0.093f, 0.036f, 0.306f, 0.02f, 0.472f)
            curveToRelative(-0.18f, 1.898f, -0.962f, 6.502f, -1.36f, 8.627f)
            curveToRelative(-0.168f, 0.9f, -0.499f, 1.201f, -0.82f, 1.23f)
            curveToRelative(-0.696f, 0.065f, -1.225f, -0.46f, -1.9f, -0.902f)
            curveToRelative(-1.056f, -0.693f, -1.653f, -1.124f, -2.678f, -1.8f)
            curveToRelative(-1.185f, -0.78f, -0.417f, -1.21f, 0.258f, -1.91f)
            curveToRelative(0.177f, -0.184f, 3.247f, -2.977f, 3.307f, -3.23f)
            curveToRelative(0.007f, -0.032f, 0.014f, -0.15f, -0.056f, -0.212f)
            reflectiveCurveToRelative(-0.174f, -0.041f, -0.249f, -0.024f)
            curveToRelative(-0.106f, 0.024f, -1.793f, 1.14f, -5.061f, 3.345f)
            curveToRelative(-0.48f, 0.33f, -0.913f, 0.49f, -1.302f, 0.48f)
            curveToRelative(-0.428f, -0.008f, -1.252f, -0.241f, -1.865f, -0.44f)
            curveToRelative(-0.752f, -0.245f, -1.349f, -0.374f, -1.297f, -0.789f)
            curveToRelative(0.027f, -0.216f, 0.325f, -0.437f, 0.893f, -0.663f)
            curveToRelative(3.498f, -1.524f, 5.83f, -2.529f, 6.998f, -3.014f)
            curveToRelative(3.332f, -1.386f, 4.025f, -1.627f, 4.476f, -1.635f)
            close()
        }
    }.build()
}