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

val Icons.Outlined.WallpaperAlt: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.WallpaperAlt",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(280f, 920f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(200f, 840f)
            verticalLineToRelative(-720f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(280f, 40f)
            horizontalLineToRelative(400f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(760f, 120f)
            verticalLineToRelative(124f)
            quadToRelative(18f, 7f, 29f, 22f)
            reflectiveQuadToRelative(11f, 34f)
            verticalLineToRelative(80f)
            quadToRelative(0f, 19f, -11f, 34f)
            reflectiveQuadToRelative(-29f, 22f)
            verticalLineToRelative(404f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(680f, 920f)
            lineTo(280f, 920f)
            close()
            moveTo(280f, 840f)
            horizontalLineToRelative(400f)
            verticalLineToRelative(-720f)
            lineTo(280f, 120f)
            verticalLineToRelative(720f)
            close()
            moveTo(620f, 600f)
            quadToRelative(6f, 0f, 9f, -5.5f)
            reflectiveQuadToRelative(-1f, -10.5f)
            lineToRelative(-85f, -113f)
            quadToRelative(-3f, -4f, -8f, -4f)
            reflectiveQuadToRelative(-8f, 4f)
            lineToRelative(-67f, 89f)
            lineToRelative(-47f, -63f)
            quadToRelative(-3f, -4f, -8f, -4f)
            reflectiveQuadToRelative(-8f, 4f)
            lineToRelative(-65f, 87f)
            quadToRelative(-4f, 5f, -1f, 10.5f)
            reflectiveQuadToRelative(9f, 5.5f)
            horizontalLineToRelative(280f)
            close()
            moveTo(628.5f, 388.5f)
            quadTo(640f, 377f, 640f, 360f)
            reflectiveQuadToRelative(-11.5f, -28.5f)
            quadTo(617f, 320f, 600f, 320f)
            reflectiveQuadToRelative(-28.5f, 11.5f)
            quadTo(560f, 343f, 560f, 360f)
            reflectiveQuadToRelative(11.5f, 28.5f)
            quadTo(583f, 400f, 600f, 400f)
            reflectiveQuadToRelative(28.5f, -11.5f)
            close()
            moveTo(280f, 840f)
            verticalLineToRelative(-720f)
            verticalLineToRelative(720f)
            close()
        }
    }.build()
}

val Icons.TwoTone.WallpaperAlt: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "TwoTone.WallpaperAlt",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(19.725f, 6.65f)
            curveToRelative(-0.183f, -0.25f, -0.425f, -0.433f, -0.725f, -0.55f)
            verticalLineToRelative(-3.1f)
            curveToRelative(0f, -0.55f, -0.196f, -1.021f, -0.588f, -1.412f)
            reflectiveCurveToRelative(-0.862f, -0.588f, -1.412f, -0.588f)
            horizontalLineTo(7f)
            curveToRelative(-0.55f, 0f, -1.021f, 0.196f, -1.412f, 0.588f)
            reflectiveCurveToRelative(-0.588f, 0.862f, -0.588f, 1.412f)
            verticalLineToRelative(18f)
            curveToRelative(0f, 0.55f, 0.196f, 1.021f, 0.588f, 1.412f)
            reflectiveCurveToRelative(0.862f, 0.588f, 1.412f, 0.588f)
            horizontalLineToRelative(10f)
            curveToRelative(0.55f, 0f, 1.021f, -0.196f, 1.412f, -0.588f)
            reflectiveCurveToRelative(0.588f, -0.862f, 0.588f, -1.412f)
            verticalLineToRelative(-10.1f)
            curveToRelative(0.3f, -0.117f, 0.542f, -0.3f, 0.725f, -0.55f)
            curveToRelative(0.183f, -0.25f, 0.275f, -0.533f, 0.275f, -0.85f)
            verticalLineToRelative(-2f)
            curveToRelative(0f, -0.317f, -0.092f, -0.6f, -0.275f, -0.85f)
            close()
            moveTo(17f, 21f)
            horizontalLineTo(7f)
            verticalLineTo(3f)
            horizontalLineToRelative(10f)
            verticalLineToRelative(18f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(15.5f, 15f)
            curveToRelative(0.1f, 0f, 0.175f, -0.046f, 0.225f, -0.138f)
            reflectiveCurveToRelative(0.042f, -0.179f, -0.025f, -0.262f)
            lineToRelative(-2.125f, -2.825f)
            curveToRelative(-0.05f, -0.067f, -0.117f, -0.1f, -0.2f, -0.1f)
            reflectiveCurveToRelative(-0.15f, 0.033f, -0.2f, 0.1f)
            lineToRelative(-1.675f, 2.225f)
            lineToRelative(-1.175f, -1.575f)
            curveToRelative(-0.05f, -0.067f, -0.117f, -0.1f, -0.2f, -0.1f)
            reflectiveCurveToRelative(-0.15f, 0.033f, -0.2f, 0.1f)
            lineToRelative(-1.625f, 2.175f)
            curveToRelative(-0.067f, 0.083f, -0.075f, 0.171f, -0.025f, 0.262f)
            reflectiveCurveToRelative(0.125f, 0.138f, 0.225f, 0.138f)
            horizontalLineToRelative(7f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(15.713f, 9.712f)
            curveToRelative(0.192f, -0.192f, 0.287f, -0.429f, 0.287f, -0.712f)
            reflectiveCurveToRelative(-0.096f, -0.521f, -0.287f, -0.712f)
            reflectiveCurveToRelative(-0.429f, -0.287f, -0.712f, -0.287f)
            reflectiveCurveToRelative(-0.521f, 0.096f, -0.712f, 0.287f)
            reflectiveCurveToRelative(-0.287f, 0.429f, -0.287f, 0.712f)
            reflectiveCurveToRelative(0.096f, 0.521f, 0.287f, 0.712f)
            reflectiveCurveToRelative(0.429f, 0.287f, 0.712f, 0.287f)
            reflectiveCurveToRelative(0.521f, -0.096f, 0.712f, -0.287f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(7f, 21f)
            verticalLineTo(3f)
            verticalLineToRelative(18f)
            close()
        }
        path(
            fill = SolidColor(Color.Black),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(7f, 3f)
            horizontalLineToRelative(10f)
            verticalLineToRelative(18f)
            horizontalLineToRelative(-10f)
            close()
        }
    }.build()
}