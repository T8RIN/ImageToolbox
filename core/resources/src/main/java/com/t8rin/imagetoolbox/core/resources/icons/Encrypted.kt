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

val Icons.Outlined.Encrypted: ImageVector by lazy {
    ImageVector.Builder(
        name = "Encrypted", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
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
            moveTo(420.0f, 600.0f)
            horizontalLineToRelative(120.0f)
            lineToRelative(-23.0f, -129.0f)
            quadToRelative(20.0f, -10.0f, 31.5f, -29.0f)
            reflectiveQuadToRelative(11.5f, -42.0f)
            quadToRelative(0.0f, -33.0f, -23.5f, -56.5f)
            reflectiveQuadTo(480.0f, 320.0f)
            quadToRelative(-33.0f, 0.0f, -56.5f, 23.5f)
            reflectiveQuadTo(400.0f, 400.0f)
            quadToRelative(0.0f, 23.0f, 11.5f, 42.0f)
            reflectiveQuadToRelative(31.5f, 29.0f)
            lineToRelative(-23.0f, 129.0f)
            close()
            moveTo(480.0f, 880.0f)
            quadToRelative(-139.0f, -35.0f, -229.5f, -159.5f)
            reflectiveQuadTo(160.0f, 444.0f)
            verticalLineToRelative(-244.0f)
            lineToRelative(320.0f, -120.0f)
            lineToRelative(320.0f, 120.0f)
            verticalLineToRelative(244.0f)
            quadToRelative(0.0f, 152.0f, -90.5f, 276.5f)
            reflectiveQuadTo(480.0f, 880.0f)
            close()
            moveTo(480.0f, 796.0f)
            quadToRelative(104.0f, -33.0f, 172.0f, -132.0f)
            reflectiveQuadToRelative(68.0f, -220.0f)
            verticalLineToRelative(-189.0f)
            lineToRelative(-240.0f, -90.0f)
            lineToRelative(-240.0f, 90.0f)
            verticalLineToRelative(189.0f)
            quadToRelative(0.0f, 121.0f, 68.0f, 220.0f)
            reflectiveQuadToRelative(172.0f, 132.0f)
            close()
            moveTo(480.0f, 480.0f)
            close()
        }
    }.build()
}

val Icons.TwoTone.Encrypted: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "TwoTone.Encrypted",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(10.5f, 15f)
            horizontalLineToRelative(3f)
            lineToRelative(-0.575f, -3.225f)
            curveToRelative(0.333f, -0.167f, 0.596f, -0.408f, 0.788f, -0.725f)
            reflectiveCurveToRelative(0.287f, -0.667f, 0.287f, -1.05f)
            curveToRelative(0f, -0.55f, -0.196f, -1.021f, -0.587f, -1.413f)
            reflectiveCurveToRelative(-0.863f, -0.587f, -1.413f, -0.587f)
            reflectiveCurveToRelative(-1.021f, 0.196f, -1.413f, 0.587f)
            reflectiveCurveToRelative(-0.587f, 0.863f, -0.587f, 1.413f)
            curveToRelative(0f, 0.383f, 0.096f, 0.733f, 0.287f, 1.05f)
            reflectiveCurveToRelative(0.454f, 0.558f, 0.788f, 0.725f)
            lineToRelative(-0.575f, 3.225f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFF000000)),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(12f, 19.9f)
            curveToRelative(1.733f, -0.55f, 3.167f, -1.65f, 4.3f, -3.3f)
            reflectiveCurveToRelative(1.7f, -3.483f, 1.7f, -5.5f)
            verticalLineToRelative(-4.725f)
            lineToRelative(-6f, -2.25f)
            lineToRelative(-6f, 2.25f)
            verticalLineToRelative(4.725f)
            curveToRelative(0f, 2.017f, 0.567f, 3.85f, 1.7f, 5.5f)
            reflectiveCurveToRelative(2.567f, 2.75f, 4.3f, 3.3f)
            close()
        }
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(12f, 2f)
            lineToRelative(-8f, 3f)
            verticalLineToRelative(6.1f)
            curveToRelative(0f, 2.533f, 0.754f, 4.838f, 2.263f, 6.913f)
            curveToRelative(1.508f, 2.075f, 3.421f, 3.404f, 5.737f, 3.987f)
            curveToRelative(2.317f, -0.583f, 4.229f, -1.913f, 5.737f, -3.987f)
            curveToRelative(1.508f, -2.075f, 2.263f, -4.379f, 2.263f, -6.913f)
            verticalLineToRelative(-6.1f)
            lineToRelative(-8f, -3f)
            close()
            moveTo(18f, 11.088f)
            curveToRelative(0f, 2.017f, -0.567f, 3.85f, -1.7f, 5.5f)
            curveToRelative(-1.133f, 1.65f, -2.567f, 2.75f, -4.3f, 3.3f)
            curveToRelative(-1.733f, -0.55f, -3.167f, -1.65f, -4.3f, -3.3f)
            curveToRelative(-1.133f, -1.65f, -1.7f, -3.483f, -1.7f, -5.5f)
            verticalLineToRelative(-4.725f)
            lineToRelative(6f, -2.25f)
            lineToRelative(6f, 2.25f)
            verticalLineToRelative(4.725f)
            close()
        }
    }.build()
}