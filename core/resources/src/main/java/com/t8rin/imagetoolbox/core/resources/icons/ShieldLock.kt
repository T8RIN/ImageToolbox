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

val Icons.Outlined.ShieldLock: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.ShieldLock",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(14.713f, 11.287f)
            curveToRelative(-0.192f, -0.192f, -0.429f, -0.287f, -0.713f, -0.287f)
            verticalLineToRelative(-1f)
            curveToRelative(0f, -0.55f, -0.196f, -1.021f, -0.588f, -1.412f)
            reflectiveCurveToRelative(-0.862f, -0.588f, -1.412f, -0.588f)
            reflectiveCurveToRelative(-1.021f, 0.196f, -1.412f, 0.588f)
            reflectiveCurveToRelative(-0.588f, 0.862f, -0.588f, 1.412f)
            verticalLineToRelative(1f)
            curveToRelative(-0.283f, 0f, -0.521f, 0.096f, -0.713f, 0.287f)
            curveToRelative(-0.192f, 0.192f, -0.287f, 0.429f, -0.287f, 0.713f)
            verticalLineToRelative(3f)
            curveToRelative(0f, 0.283f, 0.096f, 0.521f, 0.287f, 0.713f)
            curveToRelative(0.192f, 0.192f, 0.429f, 0.287f, 0.713f, 0.287f)
            horizontalLineToRelative(4f)
            curveToRelative(0.283f, 0f, 0.521f, -0.096f, 0.713f, -0.287f)
            curveToRelative(0.192f, -0.192f, 0.287f, -0.429f, 0.287f, -0.713f)
            verticalLineToRelative(-3f)
            curveToRelative(0f, -0.283f, -0.096f, -0.521f, -0.287f, -0.713f)
            close()
            moveTo(13f, 11f)
            horizontalLineToRelative(-2f)
            verticalLineToRelative(-1f)
            curveToRelative(0f, -0.283f, 0.096f, -0.521f, 0.287f, -0.713f)
            curveToRelative(0.192f, -0.192f, 0.429f, -0.287f, 0.713f, -0.287f)
            reflectiveCurveToRelative(0.521f, 0.096f, 0.713f, 0.287f)
            curveToRelative(0.192f, 0.192f, 0.287f, 0.429f, 0.287f, 0.713f)
            verticalLineToRelative(1f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(19.638f, 5.25f)
            curveToRelative(-0.242f, -0.333f, -0.554f, -0.575f, -0.938f, -0.725f)
            lineToRelative(-6f, -2.25f)
            curveToRelative(-0.233f, -0.083f, -0.467f, -0.125f, -0.7f, -0.125f)
            reflectiveCurveToRelative(-0.467f, 0.042f, -0.7f, 0.125f)
            lineToRelative(-6f, 2.25f)
            curveToRelative(-0.383f, 0.15f, -0.696f, 0.392f, -0.938f, 0.725f)
            curveToRelative(-0.242f, 0.333f, -0.362f, 0.708f, -0.362f, 1.125f)
            verticalLineToRelative(4.725f)
            curveToRelative(0f, 2.333f, 0.667f, 4.513f, 2f, 6.538f)
            curveToRelative(1.333f, 2.025f, 3.125f, 3.412f, 5.375f, 4.162f)
            curveToRelative(0.1f, 0.033f, 0.2f, 0.058f, 0.3f, 0.075f)
            curveToRelative(0.1f, 0.017f, 0.208f, 0.025f, 0.325f, 0.025f)
            reflectiveCurveToRelative(0.225f, -0.008f, 0.325f, -0.025f)
            curveToRelative(0.1f, -0.017f, 0.2f, -0.042f, 0.3f, -0.075f)
            curveToRelative(2.25f, -0.75f, 4.042f, -2.138f, 5.375f, -4.162f)
            curveToRelative(1.333f, -2.025f, 2f, -4.204f, 2f, -6.538f)
            verticalLineToRelative(-4.725f)
            curveToRelative(0f, -0.417f, -0.121f, -0.792f, -0.362f, -1.125f)
            close()
            moveTo(18f, 11.1f)
            curveToRelative(0f, 2.017f, -0.567f, 3.85f, -1.7f, 5.5f)
            curveToRelative(-1.133f, 1.65f, -2.567f, 2.75f, -4.3f, 3.3f)
            curveToRelative(-1.733f, -0.55f, -3.167f, -1.65f, -4.3f, -3.3f)
            curveToRelative(-1.133f, -1.65f, -1.7f, -3.483f, -1.7f, -5.5f)
            verticalLineToRelative(-4.725f)
            lineToRelative(6f, -2.25f)
            lineToRelative(6f, 2.25f)
            verticalLineToRelative(4.725f)
            close()
        }
    }.build()
}

val Icons.TwoTone.ShieldLock: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "TwoTone.ShieldLock",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(14.713f, 11.287f)
            curveToRelative(-0.192f, -0.192f, -0.429f, -0.287f, -0.713f, -0.287f)
            verticalLineToRelative(-1f)
            curveToRelative(0f, -0.55f, -0.196f, -1.021f, -0.588f, -1.412f)
            reflectiveCurveToRelative(-0.862f, -0.588f, -1.412f, -0.588f)
            reflectiveCurveToRelative(-1.021f, 0.196f, -1.412f, 0.588f)
            reflectiveCurveToRelative(-0.588f, 0.862f, -0.588f, 1.412f)
            verticalLineToRelative(1f)
            curveToRelative(-0.283f, 0f, -0.521f, 0.096f, -0.713f, 0.287f)
            curveToRelative(-0.192f, 0.192f, -0.287f, 0.429f, -0.287f, 0.713f)
            verticalLineToRelative(3f)
            curveToRelative(0f, 0.283f, 0.096f, 0.521f, 0.287f, 0.713f)
            curveToRelative(0.192f, 0.192f, 0.429f, 0.287f, 0.713f, 0.287f)
            horizontalLineToRelative(4f)
            curveToRelative(0.283f, 0f, 0.521f, -0.096f, 0.713f, -0.287f)
            curveToRelative(0.192f, -0.192f, 0.287f, -0.429f, 0.287f, -0.713f)
            verticalLineToRelative(-3f)
            curveToRelative(0f, -0.283f, -0.096f, -0.521f, -0.287f, -0.713f)
            close()
            moveTo(13f, 11f)
            horizontalLineToRelative(-2f)
            verticalLineToRelative(-1f)
            curveToRelative(0f, -0.283f, 0.096f, -0.521f, 0.287f, -0.713f)
            curveToRelative(0.192f, -0.192f, 0.429f, -0.287f, 0.713f, -0.287f)
            reflectiveCurveToRelative(0.521f, 0.096f, 0.713f, 0.287f)
            curveToRelative(0.192f, 0.192f, 0.287f, 0.429f, 0.287f, 0.713f)
            verticalLineToRelative(1f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(19.638f, 5.25f)
            curveToRelative(-0.242f, -0.333f, -0.554f, -0.575f, -0.938f, -0.725f)
            lineToRelative(-6f, -2.25f)
            curveToRelative(-0.233f, -0.083f, -0.467f, -0.125f, -0.7f, -0.125f)
            reflectiveCurveToRelative(-0.467f, 0.042f, -0.7f, 0.125f)
            lineToRelative(-6f, 2.25f)
            curveToRelative(-0.383f, 0.15f, -0.696f, 0.392f, -0.938f, 0.725f)
            curveToRelative(-0.242f, 0.333f, -0.362f, 0.708f, -0.362f, 1.125f)
            verticalLineToRelative(4.725f)
            curveToRelative(0f, 2.333f, 0.667f, 4.513f, 2f, 6.538f)
            curveToRelative(1.333f, 2.025f, 3.125f, 3.412f, 5.375f, 4.162f)
            curveToRelative(0.1f, 0.033f, 0.2f, 0.058f, 0.3f, 0.075f)
            curveToRelative(0.1f, 0.017f, 0.208f, 0.025f, 0.325f, 0.025f)
            reflectiveCurveToRelative(0.225f, -0.008f, 0.325f, -0.025f)
            curveToRelative(0.1f, -0.017f, 0.2f, -0.042f, 0.3f, -0.075f)
            curveToRelative(2.25f, -0.75f, 4.042f, -2.138f, 5.375f, -4.162f)
            curveToRelative(1.333f, -2.025f, 2f, -4.204f, 2f, -6.538f)
            verticalLineToRelative(-4.725f)
            curveToRelative(0f, -0.417f, -0.121f, -0.792f, -0.362f, -1.125f)
            close()
            moveTo(18f, 11.1f)
            curveToRelative(0f, 2.017f, -0.567f, 3.85f, -1.7f, 5.5f)
            curveToRelative(-1.133f, 1.65f, -2.567f, 2.75f, -4.3f, 3.3f)
            curveToRelative(-1.733f, -0.55f, -3.167f, -1.65f, -4.3f, -3.3f)
            curveToRelative(-1.133f, -1.65f, -1.7f, -3.483f, -1.7f, -5.5f)
            verticalLineToRelative(-4.725f)
            lineToRelative(6f, -2.25f)
            lineToRelative(6f, 2.25f)
            verticalLineToRelative(4.725f)
            close()
        }
        path(
            fill = SolidColor(Color.Black),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(12f, 19.904f)
            curveToRelative(1.733f, -0.55f, 3.167f, -1.65f, 4.3f, -3.3f)
            reflectiveCurveToRelative(1.7f, -3.483f, 1.7f, -5.5f)
            verticalLineToRelative(-4.725f)
            lineToRelative(-6f, -2.25f)
            lineToRelative(-6f, 2.25f)
            verticalLineToRelative(4.725f)
            curveToRelative(0f, 2.017f, 0.567f, 3.85f, 1.7f, 5.5f)
            reflectiveCurveToRelative(2.567f, 2.75f, 4.3f, 3.3f)
            close()
        }
    }.build()
}