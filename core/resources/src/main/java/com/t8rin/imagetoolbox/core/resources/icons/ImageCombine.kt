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

val Icons.Rounded.ImageCombine: ImageVector by lazy {
    ImageVector.Builder(
        name = "Image Combine", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
        viewportWidth = 960.0f, viewportHeight = 960.0f
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
            moveTo(160.0f, 360.0f)
            quadToRelative(-17.0f, 0.0f, -28.5f, -11.5f)
            reflectiveQuadTo(120.0f, 320.0f)
            verticalLineToRelative(-120.0f)
            quadToRelative(0.0f, -33.0f, 23.5f, -56.5f)
            reflectiveQuadTo(200.0f, 120.0f)
            horizontalLineToRelative(120.0f)
            quadToRelative(17.0f, 0.0f, 28.5f, 11.5f)
            reflectiveQuadTo(360.0f, 160.0f)
            quadToRelative(0.0f, 17.0f, -11.5f, 28.5f)
            reflectiveQuadTo(320.0f, 200.0f)
            lineTo(200.0f, 200.0f)
            verticalLineToRelative(120.0f)
            quadToRelative(0.0f, 17.0f, -11.5f, 28.5f)
            reflectiveQuadTo(160.0f, 360.0f)
            close()
            moveTo(800.0f, 360.0f)
            quadToRelative(-17.0f, 0.0f, -28.5f, -11.5f)
            reflectiveQuadTo(760.0f, 320.0f)
            verticalLineToRelative(-120.0f)
            lineTo(640.0f, 200.0f)
            quadToRelative(-17.0f, 0.0f, -28.5f, -11.5f)
            reflectiveQuadTo(600.0f, 160.0f)
            quadToRelative(0.0f, -17.0f, 11.5f, -28.5f)
            reflectiveQuadTo(640.0f, 120.0f)
            horizontalLineToRelative(120.0f)
            quadToRelative(33.0f, 0.0f, 56.5f, 23.5f)
            reflectiveQuadTo(840.0f, 200.0f)
            verticalLineToRelative(120.0f)
            quadToRelative(0.0f, 17.0f, -11.5f, 28.5f)
            reflectiveQuadTo(800.0f, 360.0f)
            close()
            moveTo(645.0f, 605.0f)
            lineToRelative(-97.0f, -97.0f)
            quadToRelative(-12.0f, -12.0f, -12.0f, -28.0f)
            reflectiveQuadToRelative(12.0f, -28.0f)
            lineToRelative(97.0f, -97.0f)
            quadToRelative(12.0f, -12.0f, 28.5f, -12.0f)
            reflectiveQuadToRelative(28.5f, 12.0f)
            quadToRelative(12.0f, 12.0f, 12.0f, 28.5f)
            reflectiveQuadTo(702.0f, 412.0f)
            lineToRelative(-29.0f, 28.0f)
            horizontalLineToRelative(167.0f)
            quadToRelative(17.0f, 0.0f, 28.5f, 11.5f)
            reflectiveQuadTo(880.0f, 480.0f)
            quadToRelative(0.0f, 17.0f, -11.5f, 28.5f)
            reflectiveQuadTo(840.0f, 520.0f)
            lineTo(673.0f, 520.0f)
            lineToRelative(29.0f, 28.0f)
            quadToRelative(12.0f, 12.0f, 12.0f, 28.5f)
            reflectiveQuadTo(702.0f, 605.0f)
            quadToRelative(-12.0f, 12.0f, -28.5f, 12.0f)
            reflectiveQuadTo(645.0f, 605.0f)
            close()
            moveTo(259.0f, 605.0f)
            quadToRelative(-12.0f, -12.0f, -12.5f, -28.5f)
            reflectiveQuadTo(258.0f, 548.0f)
            lineToRelative(29.0f, -28.0f)
            lineTo(120.0f, 520.0f)
            quadToRelative(-17.0f, 0.0f, -28.5f, -11.5f)
            reflectiveQuadTo(80.0f, 480.0f)
            quadToRelative(0.0f, -17.0f, 11.5f, -28.5f)
            reflectiveQuadTo(120.0f, 440.0f)
            horizontalLineToRelative(167.0f)
            lineToRelative(-29.0f, -28.0f)
            quadToRelative(-12.0f, -12.0f, -11.5f, -28.5f)
            reflectiveQuadTo(259.0f, 355.0f)
            quadToRelative(12.0f, -12.0f, 28.0f, -12.0f)
            reflectiveQuadToRelative(28.0f, 12.0f)
            lineToRelative(97.0f, 97.0f)
            quadToRelative(12.0f, 12.0f, 12.0f, 28.0f)
            reflectiveQuadToRelative(-12.0f, 28.0f)
            lineToRelative(-97.0f, 97.0f)
            quadToRelative(-12.0f, 12.0f, -28.0f, 12.0f)
            reflectiveQuadToRelative(-28.0f, -12.0f)
            close()
            moveTo(200.0f, 840.0f)
            quadToRelative(-33.0f, 0.0f, -56.5f, -23.5f)
            reflectiveQuadTo(120.0f, 760.0f)
            verticalLineToRelative(-120.0f)
            quadToRelative(0.0f, -17.0f, 11.5f, -28.5f)
            reflectiveQuadTo(160.0f, 600.0f)
            quadToRelative(17.0f, 0.0f, 28.5f, 11.5f)
            reflectiveQuadTo(200.0f, 640.0f)
            verticalLineToRelative(120.0f)
            horizontalLineToRelative(120.0f)
            quadToRelative(17.0f, 0.0f, 28.5f, 11.5f)
            reflectiveQuadTo(360.0f, 800.0f)
            quadToRelative(0.0f, 17.0f, -11.5f, 28.5f)
            reflectiveQuadTo(320.0f, 840.0f)
            lineTo(200.0f, 840.0f)
            close()
            moveTo(640.0f, 840.0f)
            quadToRelative(-17.0f, 0.0f, -28.5f, -11.5f)
            reflectiveQuadTo(600.0f, 800.0f)
            quadToRelative(0.0f, -17.0f, 11.5f, -28.5f)
            reflectiveQuadTo(640.0f, 760.0f)
            horizontalLineToRelative(120.0f)
            verticalLineToRelative(-120.0f)
            quadToRelative(0.0f, -17.0f, 11.5f, -28.5f)
            reflectiveQuadTo(800.0f, 600.0f)
            quadToRelative(17.0f, 0.0f, 28.5f, 11.5f)
            reflectiveQuadTo(840.0f, 640.0f)
            verticalLineToRelative(120.0f)
            quadToRelative(0.0f, 33.0f, -23.5f, 56.5f)
            reflectiveQuadTo(760.0f, 840.0f)
            lineTo(640.0f, 840.0f)
            close()
        }
    }.build()
}

val Icons.TwoTone.ImageCombine: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "TwoTone.ImageCombine",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(5.075f, 3f)
            lineTo(18.925f, 3f)
            arcTo(2.075f, 2.075f, 0f, isMoreThanHalf = false, isPositiveArc = true, 21f, 5.075f)
            lineTo(21f, 18.925f)
            arcTo(2.075f, 2.075f, 0f, isMoreThanHalf = false, isPositiveArc = true, 18.925f, 21f)
            lineTo(5.075f, 21f)
            arcTo(2.075f, 2.075f, 0f, isMoreThanHalf = false, isPositiveArc = true, 3f, 18.925f)
            lineTo(3f, 5.075f)
            arcTo(2.075f, 2.075f, 0f, isMoreThanHalf = false, isPositiveArc = true, 5.075f, 3f)
            close()
        }
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(4f, 9f)
            curveToRelative(-0.283f, 0f, -0.521f, -0.096f, -0.712f, -0.287f)
            curveToRelative(-0.192f, -0.192f, -0.287f, -0.429f, -0.287f, -0.712f)
            verticalLineToRelative(-3f)
            curveToRelative(0f, -0.55f, 0.196f, -1.021f, 0.587f, -1.413f)
            curveToRelative(0.392f, -0.392f, 0.863f, -0.587f, 1.413f, -0.587f)
            horizontalLineToRelative(3f)
            curveToRelative(0.283f, 0f, 0.521f, 0.096f, 0.712f, 0.287f)
            curveToRelative(0.192f, 0.192f, 0.287f, 0.429f, 0.287f, 0.712f)
            reflectiveCurveToRelative(-0.096f, 0.521f, -0.287f, 0.712f)
            reflectiveCurveToRelative(-0.429f, 0.287f, -0.712f, 0.287f)
            horizontalLineToRelative(-3f)
            verticalLineToRelative(3f)
            curveToRelative(0f, 0.283f, -0.096f, 0.521f, -0.287f, 0.712f)
            reflectiveCurveToRelative(-0.429f, 0.287f, -0.712f, 0.287f)
            close()
            moveTo(20f, 9f)
            curveToRelative(-0.283f, 0f, -0.521f, -0.096f, -0.712f, -0.287f)
            reflectiveCurveToRelative(-0.287f, -0.429f, -0.287f, -0.712f)
            verticalLineToRelative(-3f)
            horizontalLineToRelative(-3f)
            curveToRelative(-0.283f, 0f, -0.521f, -0.096f, -0.712f, -0.287f)
            reflectiveCurveToRelative(-0.287f, -0.429f, -0.287f, -0.712f)
            reflectiveCurveToRelative(0.096f, -0.521f, 0.287f, -0.712f)
            curveToRelative(0.192f, -0.192f, 0.429f, -0.287f, 0.712f, -0.287f)
            horizontalLineToRelative(3f)
            curveToRelative(0.55f, 0f, 1.021f, 0.196f, 1.413f, 0.587f)
            curveToRelative(0.392f, 0.392f, 0.587f, 0.863f, 0.587f, 1.413f)
            verticalLineToRelative(3f)
            curveToRelative(0f, 0.283f, -0.096f, 0.521f, -0.287f, 0.712f)
            reflectiveCurveToRelative(-0.429f, 0.287f, -0.712f, 0.287f)
            close()
            moveTo(16.125f, 15.125f)
            lineToRelative(-2.425f, -2.425f)
            curveToRelative(-0.2f, -0.2f, -0.3f, -0.433f, -0.3f, -0.7f)
            reflectiveCurveToRelative(0.1f, -0.5f, 0.3f, -0.7f)
            lineToRelative(2.425f, -2.425f)
            curveToRelative(0.2f, -0.2f, 0.438f, -0.3f, 0.712f, -0.3f)
            reflectiveCurveToRelative(0.512f, 0.1f, 0.712f, 0.3f)
            reflectiveCurveToRelative(0.3f, 0.438f, 0.3f, 0.712f)
            reflectiveCurveToRelative(-0.1f, 0.512f, -0.3f, 0.712f)
            lineToRelative(-0.725f, 0.7f)
            horizontalLineToRelative(4.175f)
            curveToRelative(0.283f, 0f, 0.521f, 0.096f, 0.712f, 0.287f)
            reflectiveCurveToRelative(0.287f, 0.429f, 0.287f, 0.712f)
            reflectiveCurveToRelative(-0.096f, 0.521f, -0.287f, 0.712f)
            curveToRelative(-0.192f, 0.192f, -0.429f, 0.287f, -0.712f, 0.287f)
            horizontalLineToRelative(-4.175f)
            lineToRelative(0.725f, 0.7f)
            curveToRelative(0.2f, 0.2f, 0.3f, 0.438f, 0.3f, 0.712f)
            reflectiveCurveToRelative(-0.1f, 0.512f, -0.3f, 0.712f)
            reflectiveCurveToRelative(-0.438f, 0.3f, -0.712f, 0.3f)
            reflectiveCurveToRelative(-0.512f, -0.1f, -0.712f, -0.3f)
            close()
            moveTo(6.475f, 15.125f)
            curveToRelative(-0.2f, -0.2f, -0.304f, -0.438f, -0.313f, -0.712f)
            reflectiveCurveToRelative(0.087f, -0.512f, 0.287f, -0.712f)
            lineToRelative(0.725f, -0.7f)
            horizontalLineTo(3f)
            curveToRelative(-0.283f, 0f, -0.521f, -0.096f, -0.712f, -0.287f)
            curveToRelative(-0.192f, -0.192f, -0.287f, -0.429f, -0.287f, -0.712f)
            reflectiveCurveToRelative(0.096f, -0.521f, 0.287f, -0.712f)
            reflectiveCurveToRelative(0.429f, -0.287f, 0.712f, -0.287f)
            horizontalLineToRelative(4.175f)
            lineToRelative(-0.725f, -0.7f)
            curveToRelative(-0.2f, -0.2f, -0.296f, -0.438f, -0.287f, -0.712f)
            reflectiveCurveToRelative(0.112f, -0.512f, 0.313f, -0.712f)
            reflectiveCurveToRelative(0.433f, -0.3f, 0.7f, -0.3f)
            reflectiveCurveToRelative(0.5f, 0.1f, 0.7f, 0.3f)
            lineToRelative(2.425f, 2.425f)
            curveToRelative(0.2f, 0.2f, 0.3f, 0.433f, 0.3f, 0.7f)
            reflectiveCurveToRelative(-0.1f, 0.5f, -0.3f, 0.7f)
            lineToRelative(-2.425f, 2.425f)
            curveToRelative(-0.2f, 0.2f, -0.433f, 0.3f, -0.7f, 0.3f)
            reflectiveCurveToRelative(-0.5f, -0.1f, -0.7f, -0.3f)
            close()
            moveTo(5f, 21f)
            curveToRelative(-0.55f, 0f, -1.021f, -0.196f, -1.413f, -0.587f)
            curveToRelative(-0.392f, -0.392f, -0.587f, -0.863f, -0.587f, -1.413f)
            verticalLineToRelative(-3f)
            curveToRelative(0f, -0.283f, 0.096f, -0.521f, 0.287f, -0.712f)
            curveToRelative(0.192f, -0.192f, 0.429f, -0.287f, 0.712f, -0.287f)
            reflectiveCurveToRelative(0.521f, 0.096f, 0.712f, 0.287f)
            reflectiveCurveToRelative(0.287f, 0.429f, 0.287f, 0.712f)
            verticalLineToRelative(3f)
            horizontalLineToRelative(3f)
            curveToRelative(0.283f, 0f, 0.521f, 0.096f, 0.712f, 0.287f)
            reflectiveCurveToRelative(0.287f, 0.429f, 0.287f, 0.712f)
            reflectiveCurveToRelative(-0.096f, 0.521f, -0.287f, 0.712f)
            reflectiveCurveToRelative(-0.429f, 0.287f, -0.712f, 0.287f)
            horizontalLineToRelative(-3f)
            close()
            moveTo(16f, 21f)
            curveToRelative(-0.283f, 0f, -0.521f, -0.096f, -0.712f, -0.287f)
            reflectiveCurveToRelative(-0.287f, -0.429f, -0.287f, -0.712f)
            reflectiveCurveToRelative(0.096f, -0.521f, 0.287f, -0.712f)
            reflectiveCurveToRelative(0.429f, -0.287f, 0.712f, -0.287f)
            horizontalLineToRelative(3f)
            verticalLineToRelative(-3f)
            curveToRelative(0f, -0.283f, 0.096f, -0.521f, 0.287f, -0.712f)
            reflectiveCurveToRelative(0.429f, -0.287f, 0.712f, -0.287f)
            reflectiveCurveToRelative(0.521f, 0.096f, 0.712f, 0.287f)
            reflectiveCurveToRelative(0.287f, 0.429f, 0.287f, 0.712f)
            verticalLineToRelative(3f)
            curveToRelative(0f, 0.55f, -0.196f, 1.021f, -0.587f, 1.413f)
            reflectiveCurveToRelative(-0.863f, 0.587f, -1.413f, 0.587f)
            horizontalLineToRelative(-3f)
            close()
        }
    }.build()
}