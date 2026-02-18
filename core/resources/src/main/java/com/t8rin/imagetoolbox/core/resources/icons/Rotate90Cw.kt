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

val Icons.Outlined.Rotate90Cw: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rotate90Cw",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(11f, 22f)
            curveToRelative(-2.5f, 0f, -4.625f, -0.875f, -6.375f, -2.625f)
            reflectiveCurveToRelative(-2.625f, -3.875f, -2.625f, -6.375f)
            reflectiveCurveToRelative(0.879f, -4.625f, 2.638f, -6.375f)
            curveToRelative(1.758f, -1.75f, 3.888f, -2.625f, 6.388f, -2.625f)
            horizontalLineToRelative(0.175f)
            lineToRelative(-0.9f, -0.9f)
            curveToRelative(-0.183f, -0.183f, -0.275f, -0.417f, -0.275f, -0.7f)
            reflectiveCurveToRelative(0.092f, -0.517f, 0.275f, -0.7f)
            reflectiveCurveToRelative(0.417f, -0.275f, 0.7f, -0.275f)
            reflectiveCurveToRelative(0.517f, 0.092f, 0.7f, 0.275f)
            lineToRelative(2.6f, 2.6f)
            curveToRelative(0.083f, 0.083f, 0.15f, 0.188f, 0.2f, 0.313f)
            reflectiveCurveToRelative(0.075f, 0.254f, 0.075f, 0.387f)
            reflectiveCurveToRelative(-0.025f, 0.262f, -0.075f, 0.387f)
            reflectiveCurveToRelative(-0.117f, 0.229f, -0.2f, 0.313f)
            lineToRelative(-2.6f, 2.6f)
            curveToRelative(-0.183f, 0.183f, -0.417f, 0.275f, -0.7f, 0.275f)
            reflectiveCurveToRelative(-0.517f, -0.092f, -0.7f, -0.275f)
            reflectiveCurveToRelative(-0.275f, -0.417f, -0.275f, -0.7f)
            reflectiveCurveToRelative(0.092f, -0.517f, 0.275f, -0.7f)
            lineToRelative(0.9f, -0.9f)
            horizontalLineToRelative(-0.175f)
            curveToRelative(-1.95f, 0f, -3.608f, 0.679f, -4.975f, 2.037f)
            curveToRelative(-1.367f, 1.358f, -2.05f, 3.013f, -2.05f, 4.963f)
            reflectiveCurveToRelative(0.679f, 3.604f, 2.037f, 4.963f)
            curveToRelative(1.358f, 1.358f, 3.013f, 2.037f, 4.963f, 2.037f)
            curveToRelative(0.483f, 0f, 0.954f, -0.046f, 1.413f, -0.138f)
            curveToRelative(0.458f, -0.092f, 0.904f, -0.229f, 1.337f, -0.412f)
            curveToRelative(0.25f, -0.117f, 0.5f, -0.121f, 0.75f, -0.013f)
            reflectiveCurveToRelative(0.425f, 0.287f, 0.525f, 0.538f)
            reflectiveCurveToRelative(0.108f, 0.504f, 0.025f, 0.762f)
            curveToRelative(-0.083f, 0.258f, -0.25f, 0.438f, -0.5f, 0.538f)
            curveToRelative(-0.567f, 0.233f, -1.146f, 0.412f, -1.737f, 0.538f)
            curveToRelative(-0.592f, 0.125f, -1.196f, 0.188f, -1.813f, 0.188f)
            close()
            moveTo(16.3f, 18.3f)
            lineToRelative(-4.6f, -4.6f)
            curveToRelative(-0.1f, -0.1f, -0.171f, -0.208f, -0.213f, -0.325f)
            reflectiveCurveToRelative(-0.063f, -0.242f, -0.063f, -0.375f)
            reflectiveCurveToRelative(0.021f, -0.258f, 0.063f, -0.375f)
            reflectiveCurveToRelative(0.112f, -0.225f, 0.213f, -0.325f)
            lineToRelative(4.6f, -4.6f)
            curveToRelative(0.1f, -0.1f, 0.208f, -0.171f, 0.325f, -0.213f)
            reflectiveCurveToRelative(0.242f, -0.063f, 0.375f, -0.063f)
            reflectiveCurveToRelative(0.258f, 0.021f, 0.375f, 0.063f)
            reflectiveCurveToRelative(0.225f, 0.112f, 0.325f, 0.213f)
            lineToRelative(4.6f, 4.6f)
            curveToRelative(0.1f, 0.1f, 0.171f, 0.208f, 0.213f, 0.325f)
            reflectiveCurveToRelative(0.063f, 0.242f, 0.063f, 0.375f)
            reflectiveCurveToRelative(-0.021f, 0.258f, -0.063f, 0.375f)
            reflectiveCurveToRelative(-0.112f, 0.225f, -0.213f, 0.325f)
            lineToRelative(-4.6f, 4.6f)
            curveToRelative(-0.1f, 0.1f, -0.208f, 0.171f, -0.325f, 0.213f)
            reflectiveCurveToRelative(-0.242f, 0.063f, -0.375f, 0.063f)
            reflectiveCurveToRelative(-0.258f, -0.021f, -0.375f, -0.063f)
            reflectiveCurveToRelative(-0.225f, -0.112f, -0.325f, -0.213f)
            close()
            moveTo(17f, 16.15f)
            lineToRelative(3.15f, -3.15f)
            lineToRelative(-3.15f, -3.15f)
            lineToRelative(-3.15f, 3.15f)
            lineToRelative(3.15f, 3.15f)
            close()
        }
    }.build()
}

val Icons.TwoTone.Rotate90Cw: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "TwoToneRotate90Cw",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(11f, 22f)
            curveToRelative(-2.5f, 0f, -4.625f, -0.875f, -6.375f, -2.625f)
            reflectiveCurveToRelative(-2.625f, -3.875f, -2.625f, -6.375f)
            reflectiveCurveToRelative(0.879f, -4.625f, 2.638f, -6.375f)
            curveToRelative(1.758f, -1.75f, 3.888f, -2.625f, 6.388f, -2.625f)
            horizontalLineToRelative(0.175f)
            lineToRelative(-0.9f, -0.9f)
            curveToRelative(-0.183f, -0.183f, -0.275f, -0.417f, -0.275f, -0.7f)
            reflectiveCurveToRelative(0.092f, -0.517f, 0.275f, -0.7f)
            reflectiveCurveToRelative(0.417f, -0.275f, 0.7f, -0.275f)
            reflectiveCurveToRelative(0.517f, 0.092f, 0.7f, 0.275f)
            lineToRelative(2.6f, 2.6f)
            curveToRelative(0.083f, 0.083f, 0.15f, 0.188f, 0.2f, 0.313f)
            reflectiveCurveToRelative(0.075f, 0.254f, 0.075f, 0.387f)
            reflectiveCurveToRelative(-0.025f, 0.262f, -0.075f, 0.387f)
            reflectiveCurveToRelative(-0.117f, 0.229f, -0.2f, 0.313f)
            lineToRelative(-2.6f, 2.6f)
            curveToRelative(-0.183f, 0.183f, -0.417f, 0.275f, -0.7f, 0.275f)
            reflectiveCurveToRelative(-0.517f, -0.092f, -0.7f, -0.275f)
            reflectiveCurveToRelative(-0.275f, -0.417f, -0.275f, -0.7f)
            reflectiveCurveToRelative(0.092f, -0.517f, 0.275f, -0.7f)
            lineToRelative(0.9f, -0.9f)
            horizontalLineToRelative(-0.175f)
            curveToRelative(-1.95f, 0f, -3.608f, 0.679f, -4.975f, 2.037f)
            curveToRelative(-1.367f, 1.358f, -2.05f, 3.013f, -2.05f, 4.963f)
            reflectiveCurveToRelative(0.679f, 3.604f, 2.037f, 4.963f)
            curveToRelative(1.358f, 1.358f, 3.013f, 2.037f, 4.963f, 2.037f)
            curveToRelative(0.483f, 0f, 0.954f, -0.046f, 1.413f, -0.138f)
            curveToRelative(0.458f, -0.092f, 0.904f, -0.229f, 1.337f, -0.412f)
            curveToRelative(0.25f, -0.117f, 0.5f, -0.121f, 0.75f, -0.013f)
            reflectiveCurveToRelative(0.425f, 0.287f, 0.525f, 0.538f)
            reflectiveCurveToRelative(0.108f, 0.504f, 0.025f, 0.762f)
            curveToRelative(-0.083f, 0.258f, -0.25f, 0.438f, -0.5f, 0.538f)
            curveToRelative(-0.567f, 0.233f, -1.146f, 0.412f, -1.737f, 0.538f)
            curveToRelative(-0.592f, 0.125f, -1.196f, 0.188f, -1.813f, 0.188f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(22.513f, 12.625f)
            curveToRelative(-0.042f, -0.117f, -0.112f, -0.225f, -0.213f, -0.325f)
            lineToRelative(-4.6f, -4.6f)
            curveToRelative(-0.1f, -0.1f, -0.208f, -0.171f, -0.325f, -0.213f)
            curveToRelative(-0.117f, -0.042f, -0.242f, -0.063f, -0.375f, -0.063f)
            reflectiveCurveToRelative(-0.258f, 0.021f, -0.375f, 0.063f)
            curveToRelative(-0.117f, 0.042f, -0.225f, 0.112f, -0.325f, 0.213f)
            lineToRelative(-4.6f, 4.6f)
            curveToRelative(-0.1f, 0.1f, -0.171f, 0.208f, -0.213f, 0.325f)
            curveToRelative(-0.042f, 0.117f, -0.063f, 0.242f, -0.063f, 0.375f)
            reflectiveCurveToRelative(0.021f, 0.258f, 0.063f, 0.375f)
            curveToRelative(0.042f, 0.117f, 0.112f, 0.225f, 0.213f, 0.325f)
            lineToRelative(4.6f, 4.6f)
            curveToRelative(0.1f, 0.1f, 0.208f, 0.171f, 0.325f, 0.213f)
            curveToRelative(0.117f, 0.042f, 0.242f, 0.063f, 0.375f, 0.063f)
            reflectiveCurveToRelative(0.258f, -0.021f, 0.375f, -0.063f)
            curveToRelative(0.117f, -0.042f, 0.225f, -0.112f, 0.325f, -0.213f)
            lineToRelative(4.6f, -4.6f)
            curveToRelative(0.1f, -0.1f, 0.171f, -0.208f, 0.213f, -0.325f)
            curveToRelative(0.042f, -0.117f, 0.063f, -0.242f, 0.063f, -0.375f)
            reflectiveCurveToRelative(-0.021f, -0.258f, -0.063f, -0.375f)
            close()
            moveTo(17f, 16.15f)
            lineToRelative(-3.15f, -3.15f)
            lineToRelative(3.15f, -3.15f)
            lineToRelative(3.15f, 3.15f)
            lineToRelative(-3.15f, 3.15f)
            close()
        }
        path(
            fill = SolidColor(Color.Black),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(13.85f, 13f)
            lineToRelative(3.15f, -3.15f)
            lineToRelative(3.15f, 3.15f)
            lineToRelative(-3.15f, 3.15f)
            close()
        }
    }.build()
}
