/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
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

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons

val Icons.Outlined.WallArt: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.WallArt",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(160f, 880f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(80f, 800f)
            verticalLineToRelative(-480f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(160f, 240f)
            horizontalLineToRelative(160f)
            lineToRelative(132f, -132f)
            quadToRelative(12f, -12f, 28f, -12f)
            reflectiveQuadToRelative(28f, 12f)
            lineToRelative(132f, 132f)
            horizontalLineToRelative(160f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(880f, 320f)
            verticalLineToRelative(480f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(800f, 880f)
            lineTo(160f, 880f)
            close()
            moveTo(160f, 800f)
            horizontalLineToRelative(640f)
            verticalLineToRelative(-480f)
            lineTo(160f, 320f)
            verticalLineToRelative(480f)
            close()
            moveTo(450f, 680f)
            lineTo(376f, 581f)
            quadToRelative(-6f, -8f, -16f, -8f)
            reflectiveQuadToRelative(-16f, 8f)
            lineToRelative(-80f, 107f)
            quadToRelative(-8f, 10f, -2f, 21f)
            reflectiveQuadToRelative(18f, 11f)
            horizontalLineToRelative(400f)
            quadToRelative(12f, 0f, 18f, -11f)
            reflectiveQuadToRelative(-2f, -21f)
            lineTo(586f, 541f)
            quadToRelative(-6f, -8f, -16f, -8f)
            reflectiveQuadToRelative(-16f, 8f)
            lineTo(450f, 680f)
            close()
            moveTo(742.5f, 502.5f)
            quadTo(760f, 485f, 760f, 460f)
            reflectiveQuadToRelative(-17.5f, -42.5f)
            quadTo(725f, 400f, 700f, 400f)
            reflectiveQuadToRelative(-42.5f, 17.5f)
            quadTo(640f, 435f, 640f, 460f)
            reflectiveQuadToRelative(17.5f, 42.5f)
            quadTo(675f, 520f, 700f, 520f)
            reflectiveQuadToRelative(42.5f, -17.5f)
            close()
            moveTo(404f, 240f)
            horizontalLineToRelative(152f)
            lineToRelative(-76f, -76f)
            lineToRelative(-76f, 76f)
            close()
            moveTo(160f, 800f)
            verticalLineToRelative(-480f)
            verticalLineToRelative(480f)
            close()
        }
    }.build()
}

val Icons.TwoTone.WallArt: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "TwoTone.WallArt",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(4f, 22f)
            curveToRelative(-0.55f, 0f, -1.021f, -0.196f, -1.413f, -0.587f)
            reflectiveCurveToRelative(-0.587f, -0.863f, -0.587f, -1.413f)
            verticalLineTo(8f)
            curveToRelative(0f, -0.55f, 0.196f, -1.021f, 0.587f, -1.413f)
            reflectiveCurveToRelative(0.863f, -0.587f, 1.413f, -0.587f)
            horizontalLineToRelative(4f)
            lineToRelative(3.3f, -3.3f)
            curveToRelative(0.2f, -0.2f, 0.433f, -0.3f, 0.7f, -0.3f)
            reflectiveCurveToRelative(0.5f, 0.1f, 0.7f, 0.3f)
            lineToRelative(3.3f, 3.3f)
            horizontalLineToRelative(4f)
            curveToRelative(0.55f, 0f, 1.021f, 0.196f, 1.413f, 0.587f)
            reflectiveCurveToRelative(0.587f, 0.863f, 0.587f, 1.413f)
            verticalLineToRelative(12f)
            curveToRelative(0f, 0.55f, -0.196f, 1.021f, -0.587f, 1.413f)
            reflectiveCurveToRelative(-0.863f, 0.587f, -1.413f, 0.587f)
            horizontalLineTo(4f)
            close()
            moveTo(4f, 20f)
            horizontalLineToRelative(16f)
            verticalLineTo(8f)
            horizontalLineTo(4f)
            verticalLineToRelative(12f)
            close()
            moveTo(11.25f, 17f)
            lineToRelative(-1.85f, -2.475f)
            curveToRelative(-0.1f, -0.133f, -0.233f, -0.2f, -0.4f, -0.2f)
            reflectiveCurveToRelative(-0.3f, 0.067f, -0.4f, 0.2f)
            lineToRelative(-2f, 2.675f)
            curveToRelative(-0.133f, 0.167f, -0.15f, 0.342f, -0.05f, 0.525f)
            curveToRelative(0.1f, 0.183f, 0.25f, 0.275f, 0.45f, 0.275f)
            horizontalLineToRelative(10f)
            curveToRelative(0.2f, 0f, 0.35f, -0.092f, 0.45f, -0.275f)
            curveToRelative(0.1f, -0.183f, 0.083f, -0.358f, -0.05f, -0.525f)
            lineToRelative(-2.75f, -3.675f)
            curveToRelative(-0.1f, -0.133f, -0.233f, -0.2f, -0.4f, -0.2f)
            reflectiveCurveToRelative(-0.3f, 0.067f, -0.4f, 0.2f)
            lineToRelative(-2.6f, 3.475f)
            close()
            moveTo(18.563f, 12.563f)
            curveToRelative(0.292f, -0.292f, 0.438f, -0.646f, 0.438f, -1.063f)
            curveToRelative(0f, -0.417f, -0.146f, -0.771f, -0.438f, -1.063f)
            reflectiveCurveToRelative(-0.646f, -0.438f, -1.063f, -0.438f)
            reflectiveCurveToRelative(-0.771f, 0.146f, -1.063f, 0.438f)
            reflectiveCurveToRelative(-0.438f, 0.646f, -0.438f, 1.063f)
            curveToRelative(0f, 0.417f, 0.146f, 0.771f, 0.438f, 1.063f)
            reflectiveCurveToRelative(0.646f, 0.438f, 1.063f, 0.438f)
            reflectiveCurveToRelative(0.771f, -0.146f, 1.063f, -0.438f)
            close()
            moveTo(10.1f, 6f)
            horizontalLineToRelative(3.8f)
            lineToRelative(-1.9f, -1.9f)
            lineToRelative(-1.9f, 1.9f)
            close()
            moveTo(4f, 20f)
            verticalLineTo(8f)
            verticalLineToRelative(12f)
            close()
        }
        path(
            fill = SolidColor(Color.Black),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(4f, 8f)
            horizontalLineToRelative(16f)
            verticalLineToRelative(12f)
            horizontalLineToRelative(-16f)
            close()
        }
    }.build()
}
