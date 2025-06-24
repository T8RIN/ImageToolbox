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

package com.t8rin.imagetoolbox.core.resources.icons

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Rounded.PaletteSwatch: ImageVector by lazy {
    ImageVector.Builder(
        name = "Palette Swatch", defaultWidth = 24.0.dp, defaultHeight =
            24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f
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
            moveTo(2.53f, 19.65f)
            lineTo(3.87f, 20.21f)
            verticalLineTo(11.18f)
            lineTo(1.44f, 17.04f)
            curveTo(1.03f, 18.06f, 1.5f, 19.23f, 2.53f, 19.65f)
            moveTo(22.03f, 15.95f)
            lineTo(17.07f, 4.0f)
            curveTo(16.76f, 3.23f, 16.03f, 2.77f, 15.26f, 2.75f)
            curveTo(15.0f, 2.75f, 14.73f, 2.79f, 14.47f, 2.9f)
            lineTo(7.1f, 5.95f)
            curveTo(6.35f, 6.26f, 5.89f, 7.0f, 5.87f, 7.75f)
            curveTo(5.86f, 8.0f, 5.91f, 8.29f, 6.0f, 8.55f)
            lineTo(11.0f, 20.5f)
            curveTo(11.29f, 21.28f, 12.03f, 21.74f, 12.81f, 21.75f)
            curveTo(13.07f, 21.75f, 13.33f, 21.7f, 13.58f, 21.6f)
            lineTo(20.94f, 18.55f)
            curveTo(21.96f, 18.13f, 22.45f, 16.96f, 22.03f, 15.95f)
            moveTo(7.88f, 8.75f)
            arcTo(
                1.0f, 1.0f, 0.0f,
                isMoreThanHalf = false,
                isPositiveArc = true,
                x1 = 6.88f,
                y1 = 7.75f
            )
            arcTo(
                1.0f, 1.0f, 0.0f,
                isMoreThanHalf = false,
                isPositiveArc = true,
                x1 = 7.88f,
                y1 = 6.75f
            )
            curveTo(8.43f, 6.75f, 8.88f, 7.2f, 8.88f, 7.75f)
            curveTo(8.88f, 8.3f, 8.43f, 8.75f, 7.88f, 8.75f)
            moveTo(5.88f, 19.75f)
            arcTo(
                2.0f, 2.0f, 0.0f,
                isMoreThanHalf = false,
                isPositiveArc = false,
                x1 = 7.88f,
                y1 = 21.75f
            )
            horizontalLineTo(9.33f)
            lineTo(5.88f, 13.41f)
            verticalLineTo(19.75f)
            close()
        }
    }.build()
}

val Icons.Outlined.PaletteSwatch: ImageVector by lazy {
    ImageVector.Builder(
        name = "Palette Swatch", defaultWidth = 24.0.dp,
        defaultHeight = 24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f
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
            moveTo(2.5f, 19.6f)
            lineTo(3.8f, 20.2f)
            verticalLineTo(11.2f)
            lineTo(1.4f, 17.0f)
            curveTo(1.0f, 18.1f, 1.5f, 19.2f, 2.5f, 19.6f)
            moveTo(15.2f, 4.8f)
            lineTo(20.2f, 16.8f)
            lineTo(12.9f, 19.8f)
            lineTo(7.9f, 7.9f)
            verticalLineTo(7.8f)
            lineTo(15.2f, 4.8f)
            moveTo(15.3f, 2.8f)
            curveTo(15.0f, 2.8f, 14.8f, 2.8f, 14.5f, 2.9f)
            lineTo(7.1f, 6.0f)
            curveTo(6.4f, 6.3f, 5.9f, 7.0f, 5.9f, 7.8f)
            curveTo(5.9f, 8.0f, 5.9f, 8.3f, 6.0f, 8.6f)
            lineTo(11.0f, 20.5f)
            curveTo(11.3f, 21.3f, 12.0f, 21.7f, 12.8f, 21.7f)
            curveTo(13.1f, 21.7f, 13.3f, 21.7f, 13.6f, 21.6f)
            lineTo(21.0f, 18.5f)
            curveTo(22.0f, 18.1f, 22.5f, 16.9f, 22.1f, 15.9f)
            lineTo(17.1f, 4.0f)
            curveTo(16.8f, 3.2f, 16.0f, 2.8f, 15.3f, 2.8f)
            moveTo(10.5f, 9.9f)
            curveTo(9.9f, 9.9f, 9.5f, 9.5f, 9.5f, 8.9f)
            reflectiveCurveTo(9.9f, 7.9f, 10.5f, 7.9f)
            curveTo(11.1f, 7.9f, 11.5f, 8.4f, 11.5f, 8.9f)
            reflectiveCurveTo(11.1f, 9.9f, 10.5f, 9.9f)
            moveTo(5.9f, 19.8f)
            curveTo(5.9f, 20.9f, 6.8f, 21.8f, 7.9f, 21.8f)
            horizontalLineTo(9.3f)
            lineTo(5.9f, 13.5f)
            verticalLineTo(19.8f)
            close()
        }
    }.build()
}

val Icons.TwoTone.PaletteSwatch: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "TwoTone.PaletteSwatch",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(2.5f, 19.6f)
            lineToRelative(1.3f, 0.6f)
            verticalLineToRelative(-9f)
            lineToRelative(-2.4f, 5.8f)
            curveToRelative(-0.4f, 1.1f, 0.1f, 2.2f, 1.1f, 2.6f)
        }
        path(
            fill = SolidColor(Color(0xFF000000)),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(15.2f, 4.8f)
            lineToRelative(5f, 12f)
            lineToRelative(-7.3f, 3f)
            lineToRelative(-5f, -11.9f)
            lineToRelative(0f, -0.1f)
            lineToRelative(7.3f, -3f)
        }
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(10.5f, 9.9f)
            curveToRelative(-0.6f, 0f, -1f, -0.4f, -1f, -1f)
            reflectiveCurveToRelative(0.4f, -1f, 1f, -1f)
            reflectiveCurveToRelative(1f, 0.5f, 1f, 1f)
            reflectiveCurveToRelative(-0.4f, 1f, -1f, 1f)
        }
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(5.9f, 19.8f)
            curveToRelative(0f, 1.1f, 0.9f, 2f, 2f, 2f)
            horizontalLineToRelative(1.4f)
            lineToRelative(-3.4f, -8.3f)
            verticalLineToRelative(6.3f)
            close()
        }
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(22.1f, 15.9f)
            lineToRelative(-5f, -11.9f)
            curveToRelative(-0.3f, -0.8f, -1.1f, -1.2f, -1.8f, -1.2f)
            curveToRelative(-0.3f, 0f, -0.5f, 0f, -0.8f, 0.1f)
            lineToRelative(-7.4f, 3.1f)
            curveToRelative(-0.7f, 0.3f, -1.2f, 1f, -1.2f, 1.8f)
            curveToRelative(0f, 0.2f, 0f, 0.5f, 0.1f, 0.8f)
            lineToRelative(5f, 11.9f)
            curveToRelative(0.3f, 0.8f, 1f, 1.2f, 1.8f, 1.2f)
            curveToRelative(0.3f, 0f, 0.5f, 0f, 0.8f, -0.1f)
            lineToRelative(7.4f, -3.1f)
            curveToRelative(1f, -0.4f, 1.5f, -1.6f, 1.1f, -2.6f)
            close()
            moveTo(12.9f, 19.8f)
            lineTo(7.9f, 7.9f)
            verticalLineToRelative(-0.1f)
            lineToRelative(7.3f, -3f)
            lineToRelative(5f, 12f)
            lineToRelative(-7.3f, 3f)
            close()
        }
    }.build()
}