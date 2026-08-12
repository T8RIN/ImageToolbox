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

val Icons.Rounded.Labs: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.Labs",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(480f, 880f)
            quadToRelative(-83f, 0f, -141.5f, -58.5f)
            reflectiveQuadTo(280f, 680f)
            verticalLineToRelative(-360f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(200f, 240f)
            verticalLineToRelative(-80f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(280f, 80f)
            horizontalLineToRelative(400f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(760f, 160f)
            verticalLineToRelative(80f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(680f, 320f)
            verticalLineToRelative(360f)
            quadToRelative(0f, 83f, -58.5f, 141.5f)
            reflectiveQuadTo(480f, 880f)
            close()
            moveTo(565f, 765f)
            quadToRelative(35f, -35f, 35f, -85f)
            horizontalLineToRelative(-80f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(480f, 640f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(520f, 600f)
            horizontalLineToRelative(80f)
            verticalLineToRelative(-80f)
            horizontalLineToRelative(-80f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(480f, 480f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(520f, 440f)
            horizontalLineToRelative(80f)
            verticalLineToRelative(-120f)
            lineTo(360f, 320f)
            verticalLineToRelative(360f)
            quadToRelative(0f, 50f, 35f, 85f)
            reflectiveQuadToRelative(85f, 35f)
            quadToRelative(50f, 0f, 85f, -35f)
            close()
        }
    }.build()
}

val Icons.TwoTone.Labs: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "TwoTone.Labs",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(12f, 22f)
            curveToRelative(-1.383f, 0f, -2.563f, -0.488f, -3.538f, -1.462f)
            reflectiveCurveToRelative(-1.462f, -2.154f, -1.462f, -3.538f)
            verticalLineTo(8f)
            curveToRelative(-0.55f, 0f, -1.021f, -0.196f, -1.413f, -0.587f)
            reflectiveCurveToRelative(-0.587f, -0.863f, -0.587f, -1.413f)
            verticalLineToRelative(-2f)
            curveToRelative(0f, -0.55f, 0.196f, -1.021f, 0.587f, -1.413f)
            reflectiveCurveToRelative(0.863f, -0.587f, 1.413f, -0.587f)
            horizontalLineToRelative(10f)
            curveToRelative(0.55f, 0f, 1.021f, 0.196f, 1.413f, 0.587f)
            reflectiveCurveToRelative(0.587f, 0.863f, 0.587f, 1.413f)
            verticalLineToRelative(2f)
            curveToRelative(0f, 0.55f, -0.196f, 1.021f, -0.587f, 1.413f)
            reflectiveCurveToRelative(-0.863f, 0.587f, -1.413f, 0.587f)
            verticalLineToRelative(9f)
            curveToRelative(0f, 1.383f, -0.488f, 2.563f, -1.462f, 3.538f)
            reflectiveCurveToRelative(-2.154f, 1.462f, -3.538f, 1.462f)
            close()
            moveTo(14.125f, 19.125f)
            curveToRelative(0.583f, -0.583f, 0.875f, -1.292f, 0.875f, -2.125f)
            horizontalLineToRelative(-2f)
            curveToRelative(-0.283f, 0f, -0.521f, -0.096f, -0.712f, -0.287f)
            reflectiveCurveToRelative(-0.287f, -0.429f, -0.287f, -0.712f)
            reflectiveCurveToRelative(0.096f, -0.521f, 0.287f, -0.712f)
            reflectiveCurveToRelative(0.429f, -0.287f, 0.712f, -0.287f)
            horizontalLineToRelative(2f)
            verticalLineToRelative(-2f)
            horizontalLineToRelative(-2f)
            curveToRelative(-0.283f, 0f, -0.521f, -0.096f, -0.712f, -0.287f)
            reflectiveCurveToRelative(-0.287f, -0.429f, -0.287f, -0.712f)
            reflectiveCurveToRelative(0.096f, -0.521f, 0.287f, -0.712f)
            curveToRelative(0.192f, -0.192f, 0.429f, -0.287f, 0.712f, -0.287f)
            horizontalLineToRelative(2f)
            verticalLineToRelative(-3f)
            horizontalLineToRelative(-6f)
            verticalLineToRelative(9f)
            curveToRelative(0f, 0.833f, 0.292f, 1.542f, 0.875f, 2.125f)
            reflectiveCurveToRelative(1.292f, 0.875f, 2.125f, 0.875f)
            curveToRelative(0.833f, 0f, 1.542f, -0.292f, 2.125f, -0.875f)
            close()
        }
        path(
            fill = SolidColor(Color.Black),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(14.125f, 19.125f)
            curveToRelative(0.583f, -0.583f, 0.875f, -1.292f, 0.875f, -2.125f)
            horizontalLineToRelative(-2f)
            curveToRelative(-0.283f, 0f, -0.521f, -0.096f, -0.712f, -0.287f)
            reflectiveCurveToRelative(-0.287f, -0.429f, -0.287f, -0.712f)
            reflectiveCurveToRelative(0.096f, -0.521f, 0.287f, -0.712f)
            reflectiveCurveToRelative(0.429f, -0.287f, 0.712f, -0.287f)
            horizontalLineToRelative(2f)
            verticalLineToRelative(-2f)
            horizontalLineToRelative(-2f)
            curveToRelative(-0.283f, 0f, -0.521f, -0.096f, -0.712f, -0.287f)
            reflectiveCurveToRelative(-0.287f, -0.429f, -0.287f, -0.712f)
            reflectiveCurveToRelative(0.096f, -0.521f, 0.287f, -0.712f)
            curveToRelative(0.192f, -0.192f, 0.429f, -0.287f, 0.712f, -0.287f)
            horizontalLineToRelative(2f)
            verticalLineToRelative(-3f)
            horizontalLineToRelative(-6f)
            verticalLineToRelative(9f)
            curveToRelative(0f, 0.833f, 0.292f, 1.542f, 0.875f, 2.125f)
            reflectiveCurveToRelative(1.292f, 0.875f, 2.125f, 0.875f)
            curveToRelative(0.833f, 0f, 1.542f, -0.292f, 2.125f, -0.875f)
            close()
        }
    }.build()
}
