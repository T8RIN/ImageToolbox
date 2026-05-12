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

val Icons.Outlined.ImageWeight: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.ImageWeight",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(6f, 19f)
            horizontalLineToRelative(12f)
            lineToRelative(-1.425f, -10f)
            horizontalLineTo(7.425f)
            lineToRelative(-1.425f, 10f)
            close()
            moveTo(12f, 7f)
            curveToRelative(0.283f, 0f, 0.521f, -0.096f, 0.712f, -0.287f)
            curveToRelative(0.192f, -0.192f, 0.287f, -0.429f, 0.287f, -0.712f)
            reflectiveCurveToRelative(-0.096f, -0.521f, -0.287f, -0.712f)
            curveToRelative(-0.192f, -0.192f, -0.429f, -0.287f, -0.712f, -0.287f)
            reflectiveCurveToRelative(-0.521f, 0.096f, -0.712f, 0.287f)
            reflectiveCurveToRelative(-0.287f, 0.429f, -0.287f, 0.712f)
            reflectiveCurveToRelative(0.096f, 0.521f, 0.287f, 0.712f)
            reflectiveCurveToRelative(0.429f, 0.287f, 0.712f, 0.287f)
            close()
            moveTo(14.825f, 7f)
            horizontalLineToRelative(1.75f)
            curveToRelative(0.5f, 0f, 0.933f, 0.167f, 1.3f, 0.5f)
            reflectiveCurveToRelative(0.592f, 0.742f, 0.675f, 1.225f)
            lineToRelative(1.425f, 10f)
            curveToRelative(0.083f, 0.6f, -0.071f, 1.129f, -0.463f, 1.587f)
            reflectiveCurveToRelative(-0.896f, 0.688f, -1.513f, 0.688f)
            horizontalLineTo(6f)
            curveToRelative(-0.617f, 0f, -1.121f, -0.229f, -1.513f, -0.688f)
            reflectiveCurveToRelative(-0.546f, -0.988f, -0.463f, -1.587f)
            lineToRelative(1.425f, -10f)
            curveToRelative(0.083f, -0.483f, 0.308f, -0.892f, 0.675f, -1.225f)
            curveToRelative(0.367f, -0.333f, 0.8f, -0.5f, 1.3f, -0.5f)
            horizontalLineToRelative(1.75f)
            curveToRelative(-0.05f, -0.167f, -0.092f, -0.329f, -0.125f, -0.488f)
            reflectiveCurveToRelative(-0.05f, -0.329f, -0.05f, -0.512f)
            curveToRelative(0f, -0.833f, 0.292f, -1.542f, 0.875f, -2.125f)
            reflectiveCurveToRelative(1.292f, -0.875f, 2.125f, -0.875f)
            curveToRelative(0.833f, 0f, 1.542f, 0.292f, 2.125f, 0.875f)
            reflectiveCurveToRelative(0.875f, 1.292f, 0.875f, 2.125f)
            curveToRelative(0f, 0.183f, -0.017f, 0.354f, -0.05f, 0.512f)
            reflectiveCurveToRelative(-0.075f, 0.321f, -0.125f, 0.488f)
            close()
            moveTo(6f, 19f)
            horizontalLineToRelative(12f)
            horizontalLineTo(6f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(11.511f, 15.587f)
            lineToRelative(-1f, -1.325f)
            curveToRelative(-0.1f, -0.133f, -0.233f, -0.196f, -0.4f, -0.188f)
            reflectiveCurveToRelative(-0.3f, 0.079f, -0.4f, 0.213f)
            lineToRelative(-1.125f, 1.5f)
            curveToRelative(-0.133f, 0.167f, -0.146f, 0.342f, -0.038f, 0.525f)
            reflectiveCurveToRelative(0.262f, 0.275f, 0.463f, 0.275f)
            horizontalLineToRelative(6f)
            curveToRelative(0.2f, 0f, 0.35f, -0.092f, 0.45f, -0.275f)
            reflectiveCurveToRelative(0.083f, -0.358f, -0.05f, -0.525f)
            lineToRelative(-1.6f, -2.175f)
            curveToRelative(-0.1f, -0.133f, -0.233f, -0.2f, -0.4f, -0.2f)
            reflectiveCurveToRelative(-0.3f, 0.067f, -0.4f, 0.2f)
            lineToRelative(-1.5f, 1.975f)
            close()
        }
    }.build()
}

val Icons.TwoTone.ImageWeight: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "TwoTone.ImageWeight",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(19.975f, 18.725f)
            lineToRelative(-1.425f, -10f)
            curveToRelative(-0.083f, -0.483f, -0.308f, -0.892f, -0.675f, -1.225f)
            reflectiveCurveToRelative(-0.8f, -0.5f, -1.3f, -0.5f)
            horizontalLineToRelative(-1.75f)
            curveToRelative(0.05f, -0.167f, 0.092f, -0.329f, 0.125f, -0.487f)
            reflectiveCurveToRelative(0.05f, -0.329f, 0.05f, -0.513f)
            curveToRelative(0f, -0.833f, -0.292f, -1.542f, -0.875f, -2.125f)
            reflectiveCurveToRelative(-1.292f, -0.875f, -2.125f, -0.875f)
            reflectiveCurveToRelative(-1.542f, 0.292f, -2.125f, 0.875f)
            reflectiveCurveToRelative(-0.875f, 1.292f, -0.875f, 2.125f)
            curveToRelative(0f, 0.183f, 0.017f, 0.354f, 0.05f, 0.513f)
            reflectiveCurveToRelative(0.075f, 0.321f, 0.125f, 0.487f)
            horizontalLineToRelative(-1.75f)
            curveToRelative(-0.5f, 0f, -0.933f, 0.167f, -1.3f, 0.5f)
            reflectiveCurveToRelative(-0.592f, 0.742f, -0.675f, 1.225f)
            lineToRelative(-1.425f, 10f)
            curveToRelative(-0.083f, 0.6f, 0.071f, 1.129f, 0.462f, 1.588f)
            reflectiveCurveToRelative(0.896f, 0.688f, 1.513f, 0.688f)
            horizontalLineToRelative(12f)
            curveToRelative(0.617f, 0f, 1.121f, -0.229f, 1.513f, -0.688f)
            reflectiveCurveToRelative(0.546f, -0.987f, 0.462f, -1.588f)
            close()
            moveTo(11.287f, 5.287f)
            curveToRelative(0.192f, -0.192f, 0.429f, -0.287f, 0.713f, -0.287f)
            reflectiveCurveToRelative(0.521f, 0.096f, 0.713f, 0.287f)
            curveToRelative(0.192f, 0.192f, 0.287f, 0.429f, 0.287f, 0.713f)
            reflectiveCurveToRelative(-0.096f, 0.521f, -0.287f, 0.713f)
            curveToRelative(-0.192f, 0.192f, -0.429f, 0.287f, -0.713f, 0.287f)
            reflectiveCurveToRelative(-0.521f, -0.096f, -0.713f, -0.287f)
            curveToRelative(-0.192f, -0.192f, -0.287f, -0.429f, -0.287f, -0.713f)
            reflectiveCurveToRelative(0.096f, -0.521f, 0.287f, -0.713f)
            close()
            moveTo(6f, 19f)
            lineToRelative(1.425f, -10f)
            horizontalLineToRelative(9.15f)
            lineToRelative(1.425f, 10f)
            horizontalLineTo(6f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(11.511f, 15.587f)
            lineToRelative(-1f, -1.325f)
            curveToRelative(-0.1f, -0.133f, -0.233f, -0.196f, -0.4f, -0.188f)
            reflectiveCurveToRelative(-0.3f, 0.079f, -0.4f, 0.213f)
            lineToRelative(-1.125f, 1.5f)
            curveToRelative(-0.133f, 0.167f, -0.146f, 0.342f, -0.038f, 0.525f)
            reflectiveCurveToRelative(0.262f, 0.275f, 0.463f, 0.275f)
            horizontalLineToRelative(6f)
            curveToRelative(0.2f, 0f, 0.35f, -0.092f, 0.45f, -0.275f)
            reflectiveCurveToRelative(0.083f, -0.358f, -0.05f, -0.525f)
            lineToRelative(-1.6f, -2.175f)
            curveToRelative(-0.1f, -0.133f, -0.233f, -0.2f, -0.4f, -0.2f)
            reflectiveCurveToRelative(-0.3f, 0.067f, -0.4f, 0.2f)
            lineToRelative(-1.5f, 1.975f)
            close()
        }
        path(
            fill = SolidColor(Color.Black),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(6f, 19f)
            lineToRelative(12f, 0f)
            lineToRelative(-1.425f, -10f)
            lineToRelative(-9.15f, 0f)
            lineToRelative(-1.425f, 10f)
            close()
        }
    }.build()
}