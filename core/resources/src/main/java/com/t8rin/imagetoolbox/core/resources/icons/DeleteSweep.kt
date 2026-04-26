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

import com.t8rin.imagetoolbox.core.resources.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Outlined.DeleteSweep: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.DeleteSweep",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(200f, 760f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(120f, 680f)
            verticalLineToRelative(-360f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(80f, 280f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(120f, 240f)
            horizontalLineToRelative(120f)
            verticalLineToRelative(-20f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(280f, 180f)
            horizontalLineToRelative(80f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(400f, 220f)
            verticalLineToRelative(20f)
            horizontalLineToRelative(120f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(560f, 280f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(520f, 320f)
            verticalLineToRelative(360f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(440f, 760f)
            lineTo(200f, 760f)
            close()
            moveTo(640f, 720f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(600f, 680f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(640f, 640f)
            horizontalLineToRelative(80f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(760f, 680f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(720f, 720f)
            horizontalLineToRelative(-80f)
            close()
            moveTo(640f, 560f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(600f, 520f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(640f, 480f)
            horizontalLineToRelative(160f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(840f, 520f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(800f, 560f)
            lineTo(640f, 560f)
            close()
            moveTo(640f, 400f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(600f, 360f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(640f, 320f)
            horizontalLineToRelative(200f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(880f, 360f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(840f, 400f)
            lineTo(640f, 400f)
            close()
            moveTo(200f, 320f)
            verticalLineToRelative(360f)
            horizontalLineToRelative(240f)
            verticalLineToRelative(-360f)
            lineTo(200f, 320f)
            close()
        }
    }.build()
}

val Icons.TwoTone.DeleteSweep: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "TwoTone.DeleteSweep",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(5f, 19f)
            curveToRelative(-0.55f, 0f, -1.021f, -0.196f, -1.413f, -0.587f)
            curveToRelative(-0.392f, -0.392f, -0.587f, -0.863f, -0.587f, -1.413f)
            verticalLineTo(8f)
            curveToRelative(-0.283f, 0f, -0.521f, -0.096f, -0.712f, -0.287f)
            reflectiveCurveToRelative(-0.287f, -0.429f, -0.287f, -0.712f)
            reflectiveCurveToRelative(0.096f, -0.521f, 0.287f, -0.712f)
            reflectiveCurveToRelative(0.429f, -0.287f, 0.712f, -0.287f)
            horizontalLineToRelative(3f)
            verticalLineToRelative(-0.5f)
            curveToRelative(0f, -0.283f, 0.096f, -0.521f, 0.287f, -0.712f)
            curveToRelative(0.192f, -0.192f, 0.429f, -0.287f, 0.712f, -0.287f)
            horizontalLineToRelative(2f)
            curveToRelative(0.283f, 0f, 0.521f, 0.096f, 0.712f, 0.287f)
            reflectiveCurveToRelative(0.287f, 0.429f, 0.287f, 0.712f)
            verticalLineToRelative(0.5f)
            horizontalLineToRelative(3f)
            curveToRelative(0.283f, 0f, 0.521f, 0.096f, 0.712f, 0.287f)
            reflectiveCurveToRelative(0.287f, 0.429f, 0.287f, 0.712f)
            reflectiveCurveToRelative(-0.096f, 0.521f, -0.287f, 0.712f)
            reflectiveCurveToRelative(-0.429f, 0.287f, -0.712f, 0.287f)
            verticalLineToRelative(9f)
            curveToRelative(0f, 0.55f, -0.196f, 1.021f, -0.587f, 1.413f)
            curveToRelative(-0.392f, 0.392f, -0.863f, 0.587f, -1.413f, 0.587f)
            horizontalLineToRelative(-6f)
            close()
            moveTo(16f, 18f)
            curveToRelative(-0.283f, 0f, -0.521f, -0.096f, -0.712f, -0.287f)
            curveToRelative(-0.192f, -0.192f, -0.287f, -0.429f, -0.287f, -0.712f)
            reflectiveCurveToRelative(0.096f, -0.521f, 0.287f, -0.712f)
            reflectiveCurveToRelative(0.429f, -0.287f, 0.712f, -0.287f)
            horizontalLineToRelative(2f)
            curveToRelative(0.283f, 0f, 0.521f, 0.096f, 0.712f, 0.287f)
            reflectiveCurveToRelative(0.287f, 0.429f, 0.287f, 0.712f)
            reflectiveCurveToRelative(-0.096f, 0.521f, -0.287f, 0.712f)
            curveToRelative(-0.192f, 0.192f, -0.429f, 0.287f, -0.712f, 0.287f)
            horizontalLineToRelative(-2f)
            close()
            moveTo(16f, 14f)
            curveToRelative(-0.283f, 0f, -0.521f, -0.096f, -0.712f, -0.287f)
            reflectiveCurveToRelative(-0.287f, -0.429f, -0.287f, -0.712f)
            reflectiveCurveToRelative(0.096f, -0.521f, 0.287f, -0.712f)
            reflectiveCurveToRelative(0.429f, -0.287f, 0.712f, -0.287f)
            horizontalLineToRelative(4f)
            curveToRelative(0.283f, 0f, 0.521f, 0.096f, 0.712f, 0.287f)
            reflectiveCurveToRelative(0.287f, 0.429f, 0.287f, 0.712f)
            reflectiveCurveToRelative(-0.096f, 0.521f, -0.287f, 0.712f)
            reflectiveCurveToRelative(-0.429f, 0.287f, -0.712f, 0.287f)
            horizontalLineToRelative(-4f)
            close()
            moveTo(16f, 10f)
            curveToRelative(-0.283f, 0f, -0.521f, -0.096f, -0.712f, -0.287f)
            reflectiveCurveToRelative(-0.287f, -0.429f, -0.287f, -0.712f)
            reflectiveCurveToRelative(0.096f, -0.521f, 0.287f, -0.712f)
            reflectiveCurveToRelative(0.429f, -0.287f, 0.712f, -0.287f)
            horizontalLineToRelative(5f)
            curveToRelative(0.283f, 0f, 0.521f, 0.096f, 0.712f, 0.287f)
            reflectiveCurveToRelative(0.287f, 0.429f, 0.287f, 0.712f)
            reflectiveCurveToRelative(-0.096f, 0.521f, -0.287f, 0.712f)
            reflectiveCurveToRelative(-0.429f, 0.287f, -0.712f, 0.287f)
            horizontalLineToRelative(-5f)
            close()
            moveTo(5f, 8f)
            verticalLineToRelative(9f)
            horizontalLineToRelative(6f)
            verticalLineTo(8f)
            horizontalLineToRelative(-6f)
            close()
        }
        path(
            fill = SolidColor(Color.Black),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(5f, 8f)
            horizontalLineToRelative(6f)
            verticalLineToRelative(9f)
            horizontalLineToRelative(-6f)
            close()
        }
    }.build()
}
