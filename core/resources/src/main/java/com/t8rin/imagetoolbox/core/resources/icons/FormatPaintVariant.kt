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

val Icons.Outlined.FormatPaintVariant: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.FormatPaintVariant",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(440f, 880f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(360f, 800f)
            verticalLineToRelative(-160f)
            lineTo(240f, 640f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(160f, 560f)
            verticalLineToRelative(-280f)
            quadToRelative(0f, -66f, 47f, -113f)
            reflectiveQuadToRelative(113f, -47f)
            horizontalLineToRelative(480f)
            verticalLineToRelative(440f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(720f, 640f)
            lineTo(600f, 640f)
            verticalLineToRelative(160f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(520f, 880f)
            horizontalLineToRelative(-80f)
            close()
            moveTo(240f, 400f)
            horizontalLineToRelative(480f)
            verticalLineToRelative(-200f)
            horizontalLineToRelative(-40f)
            verticalLineToRelative(160f)
            horizontalLineToRelative(-80f)
            verticalLineToRelative(-160f)
            horizontalLineToRelative(-40f)
            verticalLineToRelative(80f)
            horizontalLineToRelative(-80f)
            verticalLineToRelative(-80f)
            lineTo(320f, 200f)
            quadToRelative(-33f, 0f, -56.5f, 23.5f)
            reflectiveQuadTo(240f, 280f)
            verticalLineToRelative(120f)
            close()
            moveTo(240f, 560f)
            horizontalLineToRelative(480f)
            verticalLineToRelative(-80f)
            lineTo(240f, 480f)
            verticalLineToRelative(80f)
            close()
            moveTo(240f, 560f)
            verticalLineToRelative(-80f)
            verticalLineToRelative(80f)
            close()
        }
    }.build()
}

val Icons.TwoTone.FormatPaintVariant: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "TwoTone.FormatPaintVariant",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(8f, 3f)
            curveToRelative(-1.1f, 0f, -2.042f, 0.392f, -2.825f, 1.175f)
            reflectiveCurveToRelative(-1.175f, 1.725f, -1.175f, 2.825f)
            verticalLineToRelative(7f)
            curveToRelative(0f, 0.55f, 0.196f, 1.021f, 0.588f, 1.412f)
            reflectiveCurveToRelative(0.862f, 0.588f, 1.412f, 0.588f)
            horizontalLineToRelative(3f)
            verticalLineToRelative(4f)
            curveToRelative(0f, 0.55f, 0.196f, 1.021f, 0.588f, 1.412f)
            reflectiveCurveToRelative(0.862f, 0.588f, 1.412f, 0.588f)
            horizontalLineToRelative(2f)
            curveToRelative(0.55f, 0f, 1.021f, -0.196f, 1.412f, -0.588f)
            reflectiveCurveToRelative(0.588f, -0.862f, 0.588f, -1.412f)
            verticalLineToRelative(-4f)
            horizontalLineToRelative(3f)
            curveToRelative(0.55f, 0f, 1.021f, -0.196f, 1.412f, -0.588f)
            reflectiveCurveToRelative(0.588f, -0.862f, 0.588f, -1.412f)
            verticalLineTo(3f)
            horizontalLineToRelative(-12f)
            close()
            moveTo(18f, 14f)
            horizontalLineTo(6f)
            verticalLineToRelative(-2f)
            horizontalLineToRelative(12f)
            verticalLineToRelative(2f)
            close()
            moveTo(18f, 10f)
            horizontalLineTo(6f)
            verticalLineToRelative(-3f)
            curveToRelative(0f, -0.55f, 0.196f, -1.021f, 0.588f, -1.412f)
            reflectiveCurveToRelative(0.862f, -0.588f, 1.412f, -0.588f)
            horizontalLineToRelative(4f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(2f)
            verticalLineToRelative(-2f)
            horizontalLineToRelative(1f)
            verticalLineToRelative(4f)
            horizontalLineToRelative(2f)
            verticalLineToRelative(-4f)
            horizontalLineToRelative(1f)
            verticalLineToRelative(5f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(6f, 14f)
            verticalLineToRelative(-2f)
            verticalLineToRelative(2f)
            close()
        }
        path(
            fill = SolidColor(Color.Black),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(6f, 10f)
            horizontalLineToRelative(12f)
            verticalLineToRelative(-5f)
            horizontalLineToRelative(-1f)
            verticalLineToRelative(4f)
            horizontalLineToRelative(-2f)
            verticalLineToRelative(-4f)
            horizontalLineToRelative(-1f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(-2f)
            verticalLineToRelative(-2f)
            horizontalLineToRelative(-4f)
            curveToRelative(-0.55f, 0f, -1.021f, 0.196f, -1.413f, 0.587f)
            curveToRelative(-0.392f, 0.392f, -0.587f, 0.863f, -0.587f, 1.413f)
            verticalLineToRelative(3f)
            close()
        }
        path(
            fill = SolidColor(Color.Black),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(6f, 12f)
            horizontalLineToRelative(12f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(-12f)
            close()
        }
    }.build()
}