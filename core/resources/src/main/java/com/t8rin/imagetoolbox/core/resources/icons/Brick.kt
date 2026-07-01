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

val Icons.Outlined.Brick: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.Brick",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(80f, 740f)
            verticalLineToRelative(-360f)
            quadToRelative(0f, -25f, 17.5f, -42.5f)
            reflectiveQuadTo(140f, 320f)
            horizontalLineToRelative(60f)
            verticalLineToRelative(-100f)
            quadToRelative(0f, -25f, 17.5f, -42.5f)
            reflectiveQuadTo(260f, 160f)
            horizontalLineToRelative(120f)
            quadToRelative(25f, 0f, 42.5f, 17.5f)
            reflectiveQuadTo(440f, 220f)
            verticalLineToRelative(100f)
            horizontalLineToRelative(80f)
            verticalLineToRelative(-100f)
            quadToRelative(0f, -25f, 17.5f, -42.5f)
            reflectiveQuadTo(580f, 160f)
            horizontalLineToRelative(120f)
            quadToRelative(25f, 0f, 42.5f, 17.5f)
            reflectiveQuadTo(760f, 220f)
            verticalLineToRelative(100f)
            horizontalLineToRelative(60f)
            quadToRelative(25f, 0f, 42.5f, 17.5f)
            reflectiveQuadTo(880f, 380f)
            verticalLineToRelative(360f)
            quadToRelative(0f, 25f, -17.5f, 42.5f)
            reflectiveQuadTo(820f, 800f)
            lineTo(140f, 800f)
            quadToRelative(-25f, 0f, -42.5f, -17.5f)
            reflectiveQuadTo(80f, 740f)
            close()
            moveTo(160f, 720f)
            horizontalLineToRelative(640f)
            verticalLineToRelative(-320f)
            lineTo(160f, 400f)
            verticalLineToRelative(320f)
            close()
            moveTo(280f, 320f)
            horizontalLineToRelative(80f)
            verticalLineToRelative(-80f)
            horizontalLineToRelative(-80f)
            verticalLineToRelative(80f)
            close()
            moveTo(600f, 320f)
            horizontalLineToRelative(80f)
            verticalLineToRelative(-80f)
            horizontalLineToRelative(-80f)
            verticalLineToRelative(80f)
            close()
            moveTo(160f, 720f)
            horizontalLineToRelative(640f)
            horizontalLineToRelative(-640f)
            close()
            moveTo(280f, 320f)
            horizontalLineToRelative(80f)
            horizontalLineToRelative(-80f)
            close()
            moveTo(600f, 320f)
            horizontalLineToRelative(80f)
            horizontalLineToRelative(-80f)
            close()
        }
    }.build()
}

val Icons.TwoTone.Brick: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "TwoTone.Brick",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(21.563f, 8.438f)
            curveToRelative(-0.292f, -0.292f, -0.646f, -0.438f, -1.063f, -0.438f)
            horizontalLineToRelative(-1.5f)
            verticalLineToRelative(-2.5f)
            curveToRelative(0f, -0.417f, -0.146f, -0.771f, -0.438f, -1.063f)
            reflectiveCurveToRelative(-0.646f, -0.438f, -1.063f, -0.438f)
            horizontalLineToRelative(-3f)
            curveToRelative(-0.417f, 0f, -0.771f, 0.146f, -1.063f, 0.438f)
            reflectiveCurveToRelative(-0.438f, 0.646f, -0.438f, 1.063f)
            verticalLineToRelative(2.5f)
            horizontalLineToRelative(-2f)
            verticalLineToRelative(-2.5f)
            curveToRelative(0f, -0.417f, -0.146f, -0.771f, -0.438f, -1.063f)
            reflectiveCurveToRelative(-0.646f, -0.438f, -1.063f, -0.438f)
            horizontalLineToRelative(-3f)
            curveToRelative(-0.417f, 0f, -0.771f, 0.146f, -1.063f, 0.438f)
            reflectiveCurveToRelative(-0.438f, 0.646f, -0.438f, 1.063f)
            verticalLineToRelative(2.5f)
            horizontalLineToRelative(-1.5f)
            curveToRelative(-0.417f, 0f, -0.771f, 0.146f, -1.063f, 0.438f)
            reflectiveCurveToRelative(-0.438f, 0.646f, -0.438f, 1.063f)
            verticalLineToRelative(9f)
            curveToRelative(0f, 0.417f, 0.146f, 0.771f, 0.438f, 1.063f)
            reflectiveCurveToRelative(0.646f, 0.438f, 1.063f, 0.438f)
            horizontalLineToRelative(17f)
            curveToRelative(0.417f, 0f, 0.771f, -0.146f, 1.063f, -0.438f)
            reflectiveCurveToRelative(0.438f, -0.646f, 0.438f, -1.063f)
            verticalLineToRelative(-9f)
            curveToRelative(0f, -0.417f, -0.146f, -0.771f, -0.438f, -1.063f)
            close()
            moveTo(15f, 6f)
            horizontalLineToRelative(2f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(-2f)
            verticalLineToRelative(-2f)
            close()
            moveTo(7f, 6f)
            horizontalLineToRelative(2f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(-2f)
            verticalLineToRelative(-2f)
            close()
            moveTo(20f, 18f)
            horizontalLineTo(4f)
            verticalLineToRelative(-8f)
            horizontalLineToRelative(16f)
            verticalLineToRelative(8f)
            close()
        }
        path(
            fill = SolidColor(Color.Black),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(4f, 10f)
            horizontalLineToRelative(16f)
            verticalLineToRelative(8f)
            horizontalLineToRelative(-16f)
            close()
        }
        path(
            fill = SolidColor(Color.Black),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(7f, 6f)
            horizontalLineToRelative(2f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(-2f)
            close()
        }
        path(
            fill = SolidColor(Color.Black),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(15f, 6f)
            horizontalLineToRelative(2f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(-2f)
            close()
        }
    }.build()
}