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

val Icons.Rounded.Polygon: ImageVector by lazy {
    ImageVector.Builder(
        name = "Polygon", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
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
            moveTo(7.45f, 21.0f)
            curveTo(7.0167f, 21.0f, 6.625f, 20.875f, 6.275f, 20.625f)
            reflectiveCurveToRelative(-0.5917f, -0.5833f, -0.725f, -1.0f)
            lineToRelative(-3.075f, -9.2f)
            curveToRelative(-0.1333f, -0.4333f, -0.1333f, -0.8583f, 0.0f, -1.275f)
            curveToRelative(0.1333f, -0.4167f, 0.3833f, -0.75f, 0.75f, -1.0f)
            lineToRelative(7.625f, -5.35f)
            curveTo(11.2f, 2.5667f, 11.5833f, 2.45f, 12.0f, 2.45f)
            curveToRelative(0.4167f, 0.0f, 0.8f, 0.1167f, 1.15f, 0.35f)
            lineToRelative(7.625f, 5.35f)
            curveToRelative(0.3667f, 0.25f, 0.6167f, 0.5833f, 0.75f, 1.0f)
            curveToRelative(0.1333f, 0.4167f, 0.1333f, 0.8417f, 0.0f, 1.275f)
            lineTo(18.45f, 19.625f)
            curveToRelative(-0.1333f, 0.4167f, -0.375f, 0.75f, -0.725f, 1.0f)
            reflectiveCurveTo(16.9833f, 21.0f, 16.55f, 21.0f)
            horizontalLineTo(7.45f)
            close()
        }
    }.build()
}

val Icons.Outlined.Polygon: ImageVector by lazy {
    ImageVector.Builder(
        name = "OutlinedPolygon", defaultWidth = 24.0.dp, defaultHeight =
        24.0.dp, viewportWidth = 960.0f, viewportHeight = 960.0f
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
            moveTo(298.0f, 760.0f)
            horizontalLineToRelative(364.0f)
            lineToRelative(123.0f, -369.0f)
            lineToRelative(-305.0f, -213.0f)
            lineToRelative(-305.0f, 213.0f)
            lineToRelative(123.0f, 369.0f)
            close()
            moveTo(298.0f, 840.0f)
            quadToRelative(-26.0f, 0.0f, -47.0f, -15.0f)
            reflectiveQuadToRelative(-29.0f, -40.0f)
            lineTo(99.0f, 417.0f)
            quadToRelative(-8.0f, -26.0f, 0.0f, -51.0f)
            reflectiveQuadToRelative(30.0f, -40.0f)
            lineToRelative(305.0f, -214.0f)
            quadToRelative(21.0f, -14.0f, 46.0f, -14.0f)
            reflectiveQuadToRelative(46.0f, 14.0f)
            lineToRelative(305.0f, 214.0f)
            quadToRelative(22.0f, 15.0f, 30.0f, 40.0f)
            reflectiveQuadToRelative(0.0f, 51.0f)
            lineTo(738.0f, 785.0f)
            quadToRelative(-8.0f, 25.0f, -29.0f, 40.0f)
            reflectiveQuadToRelative(-47.0f, 15.0f)
            lineTo(298.0f, 840.0f)
            close()
            moveTo(480.0f, 469.0f)
            close()
        }
    }.build()
}