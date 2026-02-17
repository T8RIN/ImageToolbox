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

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Outlined.Stylus: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Stylus",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveToRelative(307f, 640f)
            lineToRelative(-87f, -360f)
            lineToRelative(260f, -240f)
            lineToRelative(260f, 240f)
            lineToRelative(-87f, 360f)
            lineTo(307f, 640f)
            close()
            moveTo(370f, 560f)
            horizontalLineToRelative(220f)
            lineToRelative(61f, -253f)
            lineToRelative(-131f, -121f)
            verticalLineToRelative(106f)
            quadToRelative(14f, 10f, 22f, 25f)
            reflectiveQuadToRelative(8f, 33f)
            quadToRelative(0f, 29f, -20.5f, 49.5f)
            reflectiveQuadTo(480f, 420f)
            quadToRelative(-29f, 0f, -49.5f, -20.5f)
            reflectiveQuadTo(410f, 350f)
            quadToRelative(0f, -18f, 8f, -33f)
            reflectiveQuadToRelative(22f, -25f)
            verticalLineToRelative(-106f)
            lineTo(309f, 307f)
            lineToRelative(61f, 253f)
            close()
            moveTo(160f, 840f)
            lineToRelative(22f, -65f)
            quadToRelative(8f, -25f, 29f, -40f)
            reflectiveQuadToRelative(47f, -15f)
            horizontalLineToRelative(444f)
            quadToRelative(26f, 0f, 47f, 15f)
            reflectiveQuadToRelative(29f, 40f)
            lineToRelative(22f, 65f)
            lineTo(160f, 840f)
            close()
        }
    }.build()
}

val Icons.TwoTone.Stylus: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Stylus TwoTone",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(12f, 1f)
            lineToRelative(-6.5f, 6f)
            lineToRelative(2.175f, 9f)
            horizontalLineToRelative(8.65f)
            lineToRelative(2.175f, -9f)
            lineTo(12f, 1f)
            close()
            moveTo(14.75f, 14f)
            horizontalLineToRelative(-5.5f)
            lineToRelative(-1.525f, -6.325f)
            lineToRelative(3.275f, -3.025f)
            verticalLineToRelative(2.65f)
            curveToRelative(-0.233f, 0.167f, -0.417f, 0.375f, -0.55f, 0.625f)
            curveToRelative(-0.133f, 0.25f, -0.2f, 0.525f, -0.2f, 0.825f)
            curveToRelative(0f, 0.483f, 0.171f, 0.896f, 0.513f, 1.237f)
            reflectiveCurveToRelative(0.754f, 0.513f, 1.237f, 0.513f)
            reflectiveCurveToRelative(0.896f, -0.171f, 1.237f, -0.513f)
            reflectiveCurveToRelative(0.513f, -0.754f, 0.513f, -1.237f)
            curveToRelative(0f, -0.3f, -0.067f, -0.575f, -0.2f, -0.825f)
            curveToRelative(-0.133f, -0.25f, -0.317f, -0.458f, -0.55f, -0.625f)
            verticalLineToRelative(-2.65f)
            lineToRelative(3.275f, 3.025f)
            lineToRelative(-1.525f, 6.325f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(4f, 21f)
            lineToRelative(0.55f, -1.625f)
            curveToRelative(0.133f, -0.417f, 0.375f, -0.75f, 0.725f, -1f)
            reflectiveCurveToRelative(0.742f, -0.375f, 1.175f, -0.375f)
            horizontalLineToRelative(11.1f)
            curveToRelative(0.433f, 0f, 0.825f, 0.125f, 1.175f, 0.375f)
            reflectiveCurveToRelative(0.592f, 0.583f, 0.725f, 1f)
            lineToRelative(0.55f, 1.625f)
            horizontalLineTo(4f)
            close()
        }
        path(
            fill = SolidColor(Color.Black),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(9.25f, 14f)
            horizontalLineToRelative(5.5f)
            lineToRelative(1.525f, -6.325f)
            lineToRelative(-3.275f, -3.025f)
            verticalLineToRelative(2.65f)
            curveToRelative(0.233f, 0.167f, 0.417f, 0.375f, 0.55f, 0.625f)
            reflectiveCurveToRelative(0.2f, 0.525f, 0.2f, 0.825f)
            curveToRelative(0f, 0.483f, -0.171f, 0.896f, -0.512f, 1.237f)
            reflectiveCurveToRelative(-0.754f, 0.512f, -1.237f, 0.512f)
            reflectiveCurveToRelative(-0.896f, -0.171f, -1.237f, -0.512f)
            reflectiveCurveToRelative(-0.512f, -0.754f, -0.512f, -1.237f)
            curveToRelative(0f, -0.3f, 0.067f, -0.575f, 0.2f, -0.825f)
            reflectiveCurveToRelative(0.317f, -0.458f, 0.55f, -0.625f)
            verticalLineToRelative(-2.65f)
            lineToRelative(-3.275f, 3.025f)
            lineToRelative(1.525f, 6.325f)
            close()
        }
    }.build()
}