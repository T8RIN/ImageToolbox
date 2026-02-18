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

val Icons.Outlined.Bolt: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Bolt",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(10.55f, 18.2f)
            lineToRelative(5.175f, -6.2f)
            horizontalLineToRelative(-4f)
            lineToRelative(0.725f, -5.675f)
            lineToRelative(-4.625f, 6.675f)
            horizontalLineToRelative(3.475f)
            lineToRelative(-0.75f, 5.2f)
            close()
            moveTo(9f, 15f)
            horizontalLineToRelative(-3.1f)
            curveToRelative(-0.4f, 0f, -0.696f, -0.179f, -0.887f, -0.538f)
            reflectiveCurveToRelative(-0.171f, -0.704f, 0.063f, -1.038f)
            lineTo(12.55f, 2.675f)
            curveToRelative(0.167f, -0.233f, 0.383f, -0.396f, 0.65f, -0.488f)
            reflectiveCurveToRelative(0.542f, -0.087f, 0.825f, 0.013f)
            reflectiveCurveToRelative(0.492f, 0.275f, 0.625f, 0.525f)
            reflectiveCurveToRelative(0.183f, 0.517f, 0.15f, 0.8f)
            lineToRelative(-0.8f, 6.475f)
            horizontalLineToRelative(3.875f)
            curveToRelative(0.433f, 0f, 0.738f, 0.192f, 0.913f, 0.575f)
            reflectiveCurveToRelative(0.121f, 0.742f, -0.162f, 1.075f)
            lineToRelative(-8.225f, 9.85f)
            curveToRelative(-0.183f, 0.217f, -0.408f, 0.358f, -0.675f, 0.425f)
            reflectiveCurveToRelative(-0.525f, 0.042f, -0.775f, -0.075f)
            reflectiveCurveToRelative(-0.446f, -0.296f, -0.587f, -0.538f)
            reflectiveCurveToRelative(-0.196f, -0.504f, -0.162f, -0.788f)
            lineToRelative(0.8f, -5.525f)
            close()
        }
    }.build()
}


val Icons.TwoTone.Bolt: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "TwoToneBolt",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(18.787f, 10.575f)
            curveToRelative(-0.175f, -0.383f, -0.479f, -0.575f, -0.912f, -0.575f)
            horizontalLineToRelative(-3.875f)
            lineToRelative(0.8f, -6.475f)
            curveToRelative(0.033f, -0.283f, -0.017f, -0.55f, -0.15f, -0.8f)
            curveToRelative(-0.133f, -0.25f, -0.342f, -0.425f, -0.625f, -0.525f)
            curveToRelative(-0.283f, -0.1f, -0.558f, -0.104f, -0.825f, -0.013f)
            reflectiveCurveToRelative(-0.483f, 0.254f, -0.65f, 0.487f)
            lineToRelative(-7.475f, 10.75f)
            curveToRelative(-0.233f, 0.333f, -0.254f, 0.679f, -0.063f, 1.038f)
            reflectiveCurveToRelative(0.487f, 0.537f, 0.888f, 0.537f)
            horizontalLineToRelative(3.1f)
            lineToRelative(-0.8f, 5.525f)
            curveToRelative(-0.033f, 0.283f, 0.021f, 0.546f, 0.162f, 0.787f)
            reflectiveCurveToRelative(0.338f, 0.421f, 0.588f, 0.537f)
            curveToRelative(0.25f, 0.117f, 0.508f, 0.142f, 0.775f, 0.075f)
            reflectiveCurveToRelative(0.492f, -0.208f, 0.675f, -0.425f)
            lineToRelative(8.225f, -9.85f)
            curveToRelative(0.283f, -0.333f, 0.338f, -0.692f, 0.162f, -1.075f)
            close()
            moveTo(10.55f, 18.2f)
            lineToRelative(0.75f, -5.2f)
            horizontalLineToRelative(-3.475f)
            lineToRelative(4.625f, -6.675f)
            lineToRelative(-0.725f, 5.675f)
            horizontalLineToRelative(4f)
            lineToRelative(-5.175f, 6.2f)
            close()
        }
        path(
            fill = SolidColor(Color.Black),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(10.55f, 18.2f)
            lineToRelative(5.175f, -6.2f)
            lineToRelative(-4f, 0f)
            lineToRelative(0.725f, -5.675f)
            lineToRelative(-4.625f, 6.675f)
            lineToRelative(3.475f, 0f)
            lineToRelative(-0.75f, 5.2f)
            close()
        }
    }.build()
}