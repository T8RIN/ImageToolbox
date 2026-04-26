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

val Icons.Outlined.FindInPage: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.FindInPage",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(14.75f, 20f)
            lineToRelative(2f, 2f)
            horizontalLineTo(6f)
            curveToRelative(-0.55f, 0f, -1.021f, -0.196f, -1.413f, -0.587f)
            reflectiveCurveToRelative(-0.587f, -0.863f, -0.587f, -1.413f)
            verticalLineTo(4f)
            curveToRelative(0f, -0.55f, 0.196f, -1.021f, 0.587f, -1.413f)
            reflectiveCurveToRelative(0.863f, -0.587f, 1.413f, -0.587f)
            horizontalLineToRelative(9f)
            lineToRelative(5f, 6f)
            verticalLineToRelative(12f)
            curveToRelative(0f, 0.333f, -0.071f, 0.637f, -0.213f, 0.913f)
            reflectiveCurveToRelative(-0.338f, 0.504f, -0.587f, 0.688f)
            lineToRelative(-5.2f, -5.15f)
            curveToRelative(-0.283f, 0.183f, -0.592f, 0.321f, -0.925f, 0.412f)
            curveToRelative(-0.333f, 0.092f, -0.692f, 0.138f, -1.075f, 0.138f)
            curveToRelative(-1.1f, 0f, -2.042f, -0.392f, -2.825f, -1.175f)
            reflectiveCurveToRelative(-1.175f, -1.725f, -1.175f, -2.825f)
            reflectiveCurveToRelative(0.392f, -2.042f, 1.175f, -2.825f)
            reflectiveCurveToRelative(1.725f, -1.175f, 2.825f, -1.175f)
            reflectiveCurveToRelative(2.042f, 0.392f, 2.825f, 1.175f)
            reflectiveCurveToRelative(1.175f, 1.725f, 1.175f, 2.825f)
            curveToRelative(0f, 0.383f, -0.046f, 0.742f, -0.138f, 1.075f)
            reflectiveCurveToRelative(-0.229f, 0.642f, -0.412f, 0.925f)
            lineToRelative(2.55f, 2.6f)
            verticalLineToRelative(-8.9f)
            lineToRelative(-3.95f, -4.7f)
            horizontalLineTo(6f)
            verticalLineToRelative(16f)
            horizontalLineToRelative(8.75f)
            close()
            moveTo(13.413f, 14.412f)
            curveToRelative(0.392f, -0.392f, 0.587f, -0.863f, 0.587f, -1.413f)
            reflectiveCurveToRelative(-0.196f, -1.021f, -0.587f, -1.413f)
            curveToRelative(-0.392f, -0.392f, -0.863f, -0.587f, -1.413f, -0.587f)
            reflectiveCurveToRelative(-1.021f, 0.196f, -1.413f, 0.587f)
            curveToRelative(-0.392f, 0.392f, -0.587f, 0.863f, -0.587f, 1.413f)
            reflectiveCurveToRelative(0.196f, 1.021f, 0.587f, 1.413f)
            reflectiveCurveToRelative(0.863f, 0.587f, 1.413f, 0.587f)
            reflectiveCurveToRelative(1.021f, -0.196f, 1.413f, -0.587f)
            close()
        }
    }.build()
}

val Icons.TwoTone.FindInPage: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "TwoTone.FindInPage",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(15f, 2f)
            horizontalLineTo(6f)
            curveToRelative(-0.55f, 0f, -1.021f, 0.196f, -1.412f, 0.588f)
            reflectiveCurveToRelative(-0.588f, 0.862f, -0.588f, 1.412f)
            verticalLineToRelative(16f)
            curveToRelative(0f, 0.55f, 0.196f, 1.021f, 0.588f, 1.412f)
            reflectiveCurveToRelative(0.862f, 0.588f, 1.412f, 0.588f)
            horizontalLineToRelative(10.75f)
            lineToRelative(-2f, -2f)
            horizontalLineTo(6f)
            verticalLineTo(4f)
            horizontalLineToRelative(8.05f)
            lineToRelative(3.95f, 4.7f)
            verticalLineToRelative(8.9f)
            lineToRelative(-2.55f, -2.6f)
            curveToRelative(0.183f, -0.283f, 0.321f, -0.592f, 0.412f, -0.925f)
            curveToRelative(0.092f, -0.333f, 0.138f, -0.692f, 0.138f, -1.075f)
            curveToRelative(0f, -1.1f, -0.392f, -2.042f, -1.175f, -2.825f)
            reflectiveCurveToRelative(-1.725f, -1.175f, -2.825f, -1.175f)
            reflectiveCurveToRelative(-2.042f, 0.392f, -2.825f, 1.175f)
            reflectiveCurveToRelative(-1.175f, 1.725f, -1.175f, 2.825f)
            reflectiveCurveToRelative(0.392f, 2.042f, 1.175f, 2.825f)
            reflectiveCurveToRelative(1.725f, 1.175f, 2.825f, 1.175f)
            curveToRelative(0.383f, 0f, 0.742f, -0.046f, 1.075f, -0.138f)
            curveToRelative(0.333f, -0.092f, 0.642f, -0.229f, 0.925f, -0.412f)
            lineToRelative(5.2f, 5.15f)
            curveToRelative(0.25f, -0.183f, 0.446f, -0.412f, 0.587f, -0.688f)
            curveToRelative(0.142f, -0.275f, 0.213f, -0.579f, 0.213f, -0.912f)
            verticalLineToRelative(-12f)
            lineToRelative(-5f, -6f)
            close()
            moveTo(13.412f, 14.412f)
            curveToRelative(-0.392f, 0.392f, -0.862f, 0.588f, -1.412f, 0.588f)
            reflectiveCurveToRelative(-1.021f, -0.196f, -1.412f, -0.588f)
            reflectiveCurveToRelative(-0.588f, -0.862f, -0.588f, -1.412f)
            reflectiveCurveToRelative(0.196f, -1.021f, 0.588f, -1.412f)
            reflectiveCurveToRelative(0.862f, -0.588f, 1.412f, -0.588f)
            reflectiveCurveToRelative(1.021f, 0.196f, 1.412f, 0.588f)
            reflectiveCurveToRelative(0.588f, 0.862f, 0.588f, 1.412f)
            reflectiveCurveToRelative(-0.196f, 1.021f, -0.588f, 1.412f)
            close()
        }
        path(
            fill = SolidColor(Color.Black),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(18.003f, 8.623f)
            lineToRelative(-3.88f, -4.625f)
            horizontalLineTo(6.003f)
            verticalLineToRelative(16f)
            horizontalLineToRelative(12f)
            verticalLineToRelative(-11.375f)
            close()
            moveTo(13.415f, 14.411f)
            curveToRelative(-0.392f, 0.392f, -0.862f, 0.588f, -1.412f, 0.588f)
            reflectiveCurveToRelative(-1.021f, -0.196f, -1.412f, -0.588f)
            reflectiveCurveToRelative(-0.588f, -0.862f, -0.588f, -1.412f)
            reflectiveCurveToRelative(0.196f, -1.021f, 0.588f, -1.412f)
            reflectiveCurveToRelative(0.862f, -0.588f, 1.412f, -0.588f)
            reflectiveCurveToRelative(1.021f, 0.196f, 1.412f, 0.588f)
            reflectiveCurveToRelative(0.588f, 0.862f, 0.588f, 1.412f)
            reflectiveCurveToRelative(-0.196f, 1.021f, -0.588f, 1.412f)
            close()
        }
        path(
            fill = SolidColor(Color.Black),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(18.003f, 19.999f)
            horizontalLineToRelative(-5.186f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(5.186f)
            curveToRelative(0.55f, 0f, 1.021f, -0.196f, 1.412f, -0.588f)
            reflectiveCurveToRelative(0.588f, -0.862f, 0.588f, -1.412f)
            horizontalLineToRelative(-2f)
            close()
        }
    }.build()
}