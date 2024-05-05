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

val Icons.Outlined.Triangle: ImageVector by lazy {
    ImageVector.Builder(
        name = "OutlinedTriangle", defaultWidth = 24.0.dp, defaultHeight
        = 24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f
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
            moveTo(22.125f, 19.5f)
            lineToRelative(-9.25f, -16.0f)
            curveToRelative(-0.1f, -0.1667f, -0.2292f, -0.2917f, -0.3875f, -0.375f)
            reflectiveCurveTo(12.1667f, 3.0f, 12.0f, 3.0f)
            reflectiveCurveToRelative(-0.3292f, 0.0417f, -0.4875f, 0.125f)
            reflectiveCurveTo(11.225f, 3.3333f, 11.125f, 3.5f)
            lineToRelative(-9.25f, 16.0f)
            curveToRelative(-0.1f, 0.1667f, -0.1458f, 0.3375f, -0.1375f, 0.5125f)
            curveTo(1.7458f, 20.1875f, 1.7917f, 20.35f, 1.875f, 20.5f)
            reflectiveCurveToRelative(0.2f, 0.2708f, 0.35f, 0.3625f)
            curveTo(2.375f, 20.9542f, 2.5417f, 21.0f, 2.725f, 21.0f)
            horizontalLineToRelative(18.55f)
            curveToRelative(0.1833f, 0.0f, 0.35f, -0.0458f, 0.5f, -0.1375f)
            curveTo(21.925f, 20.7708f, 22.0417f, 20.65f, 22.125f, 20.5f)
            reflectiveCurveToRelative(0.1292f, -0.3125f, 0.1375f, -0.4875f)
            curveTo(22.2708f, 19.8375f, 22.225f, 19.6667f, 22.125f, 19.5f)
            close()
            moveTo(4.45f, 19.0f)
            lineTo(12.0f, 6.0f)
            lineToRelative(7.55f, 13.0f)
            horizontalLineTo(4.45f)
            close()
        }
    }
        .build()
}

val Icons.Rounded.Triangle: ImageVector by lazy {
    ImageVector.Builder(
        name = "Triangle", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
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
            moveTo(2.725f, 21.0f)
            curveToRelative(-0.1833f, 0.0f, -0.35f, -0.0458f, -0.5f, -0.1375f)
            curveTo(2.075f, 20.7708f, 1.9583f, 20.65f, 1.875f, 20.5f)
            reflectiveCurveToRelative(-0.1292f, -0.3125f, -0.1375f, -0.4875f)
            curveTo(1.7292f, 19.8375f, 1.775f, 19.6667f, 1.875f, 19.5f)
            lineToRelative(9.25f, -16.0f)
            curveToRelative(0.1f, -0.1667f, 0.2292f, -0.2917f, 0.3875f, -0.375f)
            curveToRelative(0.1583f, -0.0833f, 0.3208f, -0.125f, 0.4875f, -0.125f)
            curveToRelative(0.1667f, 0.0f, 0.3292f, 0.0417f, 0.4875f, 0.125f)
            curveToRelative(0.1583f, 0.0833f, 0.2875f, 0.2083f, 0.3875f, 0.375f)
            lineToRelative(9.25f, 16.0f)
            curveToRelative(0.1f, 0.1667f, 0.1458f, 0.3375f, 0.1375f, 0.5125f)
            curveTo(22.2542f, 20.1875f, 22.2083f, 20.35f, 22.125f, 20.5f)
            curveToRelative(-0.0833f, 0.15f, -0.2f, 0.2708f, -0.35f, 0.3625f)
            curveTo(21.625f, 20.9542f, 21.4583f, 21.0f, 21.275f, 21.0f)
            horizontalLineTo(2.725f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFF000000)),
            stroke = null,
            strokeLineWidth = 0.0f,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Miter,
            strokeLineMiter = 4.0f,
            pathFillType = PathFillType.NonZero
        ) {
            moveTo(4.45f, 19.0f)
            lineToRelative(15.1f, 0.0f)
            lineToRelative(-7.55f, -13.0f)
            close()
        }
    }
        .build()
}