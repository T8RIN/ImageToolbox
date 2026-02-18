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

val Icons.Outlined.Build: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Build",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(9f, 15f)
            curveToRelative(-1.667f, 0f, -3.083f, -0.583f, -4.25f, -1.75f)
            reflectiveCurveToRelative(-1.75f, -2.583f, -1.75f, -4.25f)
            curveToRelative(0f, -0.333f, 0.025f, -0.667f, 0.075f, -1f)
            reflectiveCurveToRelative(0.142f, -0.65f, 0.275f, -0.95f)
            curveToRelative(0.083f, -0.167f, 0.188f, -0.292f, 0.313f, -0.375f)
            reflectiveCurveToRelative(0.262f, -0.142f, 0.412f, -0.175f)
            reflectiveCurveToRelative(0.304f, -0.029f, 0.463f, 0.013f)
            reflectiveCurveToRelative(0.304f, 0.129f, 0.438f, 0.262f)
            lineToRelative(2.625f, 2.625f)
            lineToRelative(1.8f, -1.8f)
            lineToRelative(-2.625f, -2.625f)
            curveToRelative(-0.133f, -0.133f, -0.221f, -0.279f, -0.262f, -0.438f)
            reflectiveCurveToRelative(-0.046f, -0.313f, -0.013f, -0.463f)
            reflectiveCurveToRelative(0.092f, -0.287f, 0.175f, -0.412f)
            reflectiveCurveToRelative(0.208f, -0.229f, 0.375f, -0.313f)
            curveToRelative(0.3f, -0.133f, 0.617f, -0.225f, 0.95f, -0.275f)
            reflectiveCurveToRelative(0.667f, -0.075f, 1f, -0.075f)
            curveToRelative(1.667f, 0f, 3.083f, 0.583f, 4.25f, 1.75f)
            curveToRelative(1.167f, 1.167f, 1.75f, 2.583f, 1.75f, 4.25f)
            curveToRelative(0f, 0.383f, -0.033f, 0.746f, -0.1f, 1.087f)
            reflectiveCurveToRelative(-0.167f, 0.679f, -0.3f, 1.013f)
            lineToRelative(5.05f, 5f)
            curveToRelative(0.483f, 0.483f, 0.725f, 1.075f, 0.725f, 1.775f)
            reflectiveCurveToRelative(-0.242f, 1.292f, -0.725f, 1.775f)
            reflectiveCurveToRelative(-1.075f, 0.725f, -1.775f, 0.725f)
            reflectiveCurveToRelative(-1.292f, -0.25f, -1.775f, -0.75f)
            lineToRelative(-5f, -5.025f)
            curveToRelative(-0.333f, 0.133f, -0.671f, 0.233f, -1.013f, 0.3f)
            reflectiveCurveToRelative(-0.704f, 0.1f, -1.087f, 0.1f)
            close()
            moveTo(9f, 13f)
            curveToRelative(0.433f, 0f, 0.867f, -0.067f, 1.3f, -0.2f)
            reflectiveCurveToRelative(0.825f, -0.342f, 1.175f, -0.625f)
            lineToRelative(6.075f, 6.075f)
            curveToRelative(0.083f, 0.083f, 0.196f, 0.121f, 0.338f, 0.112f)
            reflectiveCurveToRelative(0.254f, -0.054f, 0.338f, -0.138f)
            reflectiveCurveToRelative(0.125f, -0.196f, 0.125f, -0.338f)
            reflectiveCurveToRelative(-0.042f, -0.254f, -0.125f, -0.338f)
            lineToRelative(-6.075f, -6.05f)
            curveToRelative(0.3f, -0.333f, 0.517f, -0.721f, 0.65f, -1.163f)
            curveToRelative(0.133f, -0.442f, 0.2f, -0.887f, 0.2f, -1.337f)
            curveToRelative(0f, -1f, -0.321f, -1.871f, -0.962f, -2.612f)
            reflectiveCurveToRelative(-1.438f, -1.188f, -2.388f, -1.337f)
            lineToRelative(1.85f, 1.85f)
            curveToRelative(0.2f, 0.2f, 0.3f, 0.433f, 0.3f, 0.7f)
            reflectiveCurveToRelative(-0.1f, 0.5f, -0.3f, 0.7f)
            lineToRelative(-3.2f, 3.2f)
            curveToRelative(-0.2f, 0.2f, -0.433f, 0.3f, -0.7f, 0.3f)
            reflectiveCurveToRelative(-0.5f, -0.1f, -0.7f, -0.3f)
            lineToRelative(-1.85f, -1.85f)
            curveToRelative(0.15f, 0.95f, 0.596f, 1.746f, 1.337f, 2.388f)
            curveToRelative(0.742f, 0.642f, 1.612f, 0.962f, 2.612f, 0.962f)
            close()
        }
    }.build()
}

val Icons.TwoTone.Build: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "TwoToneBuild",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(19.65f, 16.1f)
            lineToRelative(-5.05f, -5f)
            curveToRelative(0.133f, -0.333f, 0.233f, -0.671f, 0.3f, -1.012f)
            curveToRelative(0.067f, -0.342f, 0.1f, -0.704f, 0.1f, -1.088f)
            curveToRelative(0f, -1.667f, -0.583f, -3.083f, -1.75f, -4.25f)
            reflectiveCurveToRelative(-2.583f, -1.75f, -4.25f, -1.75f)
            curveToRelative(-0.333f, 0f, -0.667f, 0.025f, -1f, 0.075f)
            reflectiveCurveToRelative(-0.65f, 0.142f, -0.95f, 0.275f)
            curveToRelative(-0.167f, 0.083f, -0.292f, 0.188f, -0.375f, 0.313f)
            curveToRelative(-0.083f, 0.125f, -0.142f, 0.263f, -0.175f, 0.413f)
            curveToRelative(-0.033f, 0.15f, -0.029f, 0.304f, 0.013f, 0.462f)
            curveToRelative(0.042f, 0.158f, 0.129f, 0.304f, 0.263f, 0.438f)
            lineToRelative(2.625f, 2.625f)
            lineToRelative(-1.8f, 1.8f)
            lineToRelative(-2.625f, -2.625f)
            curveToRelative(-0.133f, -0.133f, -0.279f, -0.221f, -0.438f, -0.263f)
            curveToRelative(-0.158f, -0.042f, -0.313f, -0.046f, -0.462f, -0.013f)
            curveToRelative(-0.15f, 0.033f, -0.288f, 0.092f, -0.413f, 0.175f)
            curveToRelative(-0.125f, 0.083f, -0.229f, 0.208f, -0.313f, 0.375f)
            curveToRelative(-0.133f, 0.3f, -0.225f, 0.617f, -0.275f, 0.95f)
            reflectiveCurveToRelative(-0.075f, 0.667f, -0.075f, 1f)
            curveToRelative(0f, 1.667f, 0.583f, 3.083f, 1.75f, 4.25f)
            reflectiveCurveToRelative(2.583f, 1.75f, 4.25f, 1.75f)
            curveToRelative(0.383f, 0f, 0.746f, -0.033f, 1.088f, -0.1f)
            curveToRelative(0.342f, -0.067f, 0.679f, -0.167f, 1.012f, -0.3f)
            lineToRelative(5f, 5.025f)
            curveToRelative(0.483f, 0.5f, 1.075f, 0.75f, 1.775f, 0.75f)
            reflectiveCurveToRelative(1.292f, -0.242f, 1.775f, -0.725f)
            reflectiveCurveToRelative(0.725f, -1.075f, 0.725f, -1.775f)
            reflectiveCurveToRelative(-0.242f, -1.292f, -0.725f, -1.775f)
            close()
            moveTo(18.225f, 18.225f)
            curveToRelative(-0.083f, 0.083f, -0.196f, 0.129f, -0.337f, 0.138f)
            curveToRelative(-0.142f, 0.008f, -0.254f, -0.029f, -0.338f, -0.112f)
            lineToRelative(-6.075f, -6.075f)
            curveToRelative(-0.35f, 0.283f, -0.742f, 0.492f, -1.175f, 0.625f)
            curveToRelative(-0.433f, 0.133f, -0.867f, 0.2f, -1.3f, 0.2f)
            curveToRelative(-1f, 0f, -1.871f, -0.321f, -2.612f, -0.963f)
            curveToRelative(-0.742f, -0.642f, -1.188f, -1.438f, -1.338f, -2.387f)
            lineToRelative(1.85f, 1.85f)
            curveToRelative(0.2f, 0.2f, 0.433f, 0.3f, 0.7f, 0.3f)
            reflectiveCurveToRelative(0.5f, -0.1f, 0.7f, -0.3f)
            lineToRelative(3.2f, -3.2f)
            curveToRelative(0.2f, -0.2f, 0.3f, -0.433f, 0.3f, -0.7f)
            reflectiveCurveToRelative(-0.1f, -0.5f, -0.3f, -0.7f)
            lineToRelative(-1.85f, -1.85f)
            curveToRelative(0.95f, 0.15f, 1.746f, 0.596f, 2.387f, 1.338f)
            curveToRelative(0.642f, 0.742f, 0.963f, 1.612f, 0.963f, 2.612f)
            curveToRelative(0f, 0.45f, -0.067f, 0.896f, -0.2f, 1.338f)
            curveToRelative(-0.133f, 0.442f, -0.35f, 0.829f, -0.65f, 1.162f)
            lineToRelative(6.075f, 6.05f)
            curveToRelative(0.083f, 0.083f, 0.125f, 0.196f, 0.125f, 0.338f)
            reflectiveCurveToRelative(-0.042f, 0.254f, -0.125f, 0.337f)
            close()
        }
        path(
            fill = SolidColor(Color.Black),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(9f, 13f)
            curveToRelative(0.433f, 0f, 0.867f, -0.067f, 1.3f, -0.2f)
            reflectiveCurveToRelative(0.825f, -0.342f, 1.175f, -0.625f)
            lineToRelative(6.075f, 6.075f)
            curveToRelative(0.083f, 0.083f, 0.196f, 0.121f, 0.338f, 0.112f)
            reflectiveCurveToRelative(0.254f, -0.054f, 0.338f, -0.138f)
            reflectiveCurveToRelative(0.125f, -0.196f, 0.125f, -0.338f)
            reflectiveCurveToRelative(-0.042f, -0.254f, -0.125f, -0.338f)
            lineToRelative(-6.075f, -6.05f)
            curveToRelative(0.3f, -0.333f, 0.517f, -0.721f, 0.65f, -1.163f)
            curveToRelative(0.133f, -0.442f, 0.2f, -0.887f, 0.2f, -1.337f)
            curveToRelative(0f, -1f, -0.321f, -1.871f, -0.962f, -2.612f)
            reflectiveCurveToRelative(-1.438f, -1.188f, -2.388f, -1.337f)
            lineToRelative(1.85f, 1.85f)
            curveToRelative(0.2f, 0.2f, 0.3f, 0.433f, 0.3f, 0.7f)
            reflectiveCurveToRelative(-0.1f, 0.5f, -0.3f, 0.7f)
            lineToRelative(-3.2f, 3.2f)
            curveToRelative(-0.2f, 0.2f, -0.433f, 0.3f, -0.7f, 0.3f)
            reflectiveCurveToRelative(-0.5f, -0.1f, -0.7f, -0.3f)
            lineToRelative(-1.85f, -1.85f)
            curveToRelative(0.15f, 0.95f, 0.596f, 1.746f, 1.337f, 2.388f)
            curveToRelative(0.742f, 0.642f, 1.612f, 0.962f, 2.612f, 0.962f)
            close()
        }
    }.build()
}
