/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

val Icons.Outlined.ImageConvert: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.ImageConvert",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(15.6f, 8.9f)
            verticalLineToRelative(-1.9f)
            horizontalLineToRelative(2.4f)
            curveToRelative(-0.7f, -0.9f, -1.5f, -1.5f, -2.5f, -2f)
            reflectiveCurveToRelative(-2f, -0.7f, -3.2f, -0.7f)
            curveToRelative(-0.8f, 0f, -1.6f, 0.1f, -2.4f, 0.4f)
            curveToRelative(-0.7f, 0.3f, -1.4f, 0.6f, -2f, 1.1f)
            lineToRelative(-0.9f, -1.6f)
            curveToRelative(0.8f, -0.5f, 1.6f, -0.9f, 2.5f, -1.2f)
            reflectiveCurveToRelative(1.9f, -0.5f, 2.9f, -0.5f)
            curveToRelative(1.2f, 0f, 2.4f, 0.2f, 3.5f, 0.7f)
            reflectiveCurveToRelative(2.1f, 1.1f, 2.9f, 1.9f)
            verticalLineToRelative(-1.2f)
            horizontalLineToRelative(1.8f)
            verticalLineToRelative(5f)
            horizontalLineToRelative(-5f)
            close()
        }
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(8.2f, 13.5f)
            horizontalLineToRelative(8f)
            lineToRelative(-2.6f, -3.5f)
            lineToRelative(-1.9f, 2.5f)
            lineToRelative(-1.3f, -1.8f)
            lineToRelative(-2.2f, 2.8f)
            close()
        }
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(13.11f, 15.744f)
            lineToRelative(1.645f, 0.95f)
            lineToRelative(-1.2f, 2.078f)
            curveToRelative(1.129f, -0.156f, 2.049f, -0.549f, 2.982f, -1.165f)
            reflectiveCurveToRelative(1.606f, -1.382f, 2.206f, -2.421f)
            curveToRelative(0.4f, -0.693f, 0.713f, -1.436f, 0.854f, -2.278f)
            curveToRelative(0.09f, -0.756f, 0.18f, -1.512f, 0.047f, -2.282f)
            lineToRelative(1.836f, 0.021f)
            curveToRelative(0.033f, 0.943f, -0.021f, 1.836f, -0.211f, 2.765f)
            reflectiveCurveToRelative(-0.517f, 1.895f, -1.017f, 2.761f)
            curveToRelative(-0.6f, 1.039f, -1.373f, 1.978f, -2.356f, 2.681f)
            reflectiveCurveToRelative(-2.003f, 1.269f, -3.095f, 1.561f)
            lineToRelative(1.039f, 0.6f)
            lineToRelative(-0.9f, 1.559f)
            lineToRelative(-4.33f, -2.5f)
            lineToRelative(2.5f, -4.33f)
            close()
        }
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(8.33f, 10.126f)
            lineToRelative(-1.634f, 0.969f)
            lineToRelative(-1.224f, -2.065f)
            curveToRelative(-0.417f, 1.061f, -0.525f, 2.055f, -0.446f, 3.17f)
            reflectiveCurveToRelative(0.418f, 2.077f, 1.03f, 3.11f)
            curveToRelative(0.408f, 0.688f, 0.902f, 1.325f, 1.568f, 1.861f)
            curveToRelative(0.615f, 0.449f, 1.23f, 0.898f, 1.966f, 1.16f)
            lineToRelative(-0.917f, 1.59f)
            curveToRelative(-0.838f, -0.433f, -1.59f, -0.917f, -2.307f, -1.539f)
            reflectiveCurveToRelative(-1.399f, -1.379f, -1.909f, -2.24f)
            curveToRelative(-0.612f, -1.032f, -1.052f, -2.167f, -1.183f, -3.368f)
            reflectiveCurveToRelative(-0.125f, -2.367f, 0.156f, -3.463f)
            lineToRelative(-1.032f, 0.612f)
            lineToRelative(-0.918f, -1.548f)
            lineToRelative(4.301f, -2.55f)
            lineToRelative(2.55f, 4.301f)
            close()
        }
    }.build()
}

val Icons.TwoTone.ImageConvert: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "TwoTone.ImageConvert",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(12.267f, 4.3f)
            lineTo(12.487f, 4.3f)
            arcTo(
                7.264f,
                7.264f,
                0f,
                isMoreThanHalf = false,
                isPositiveArc = true,
                19.751f,
                11.564f
            )
            lineTo(19.751f, 11.609f)
            arcTo(
                7.264f,
                7.264f,
                0f,
                isMoreThanHalf = false,
                isPositiveArc = true,
                12.487f,
                18.874f
            )
            lineTo(12.267f, 18.874f)
            arcTo(7.264f, 7.264f, 0f, isMoreThanHalf = false, isPositiveArc = true, 5.002f, 11.609f)
            lineTo(5.002f, 11.564f)
            arcTo(7.264f, 7.264f, 0f, isMoreThanHalf = false, isPositiveArc = true, 12.267f, 4.3f)
            close()
        }
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(15.6f, 8.9f)
            verticalLineToRelative(-1.9f)
            horizontalLineToRelative(2.4f)
            curveToRelative(-0.7f, -0.9f, -1.5f, -1.5f, -2.5f, -2f)
            reflectiveCurveToRelative(-2f, -0.7f, -3.2f, -0.7f)
            curveToRelative(-0.8f, 0f, -1.6f, 0.1f, -2.4f, 0.4f)
            curveToRelative(-0.7f, 0.3f, -1.4f, 0.6f, -2f, 1.1f)
            lineToRelative(-0.9f, -1.6f)
            curveToRelative(0.8f, -0.5f, 1.6f, -0.9f, 2.5f, -1.2f)
            reflectiveCurveToRelative(1.9f, -0.5f, 2.9f, -0.5f)
            curveToRelative(1.2f, 0f, 2.4f, 0.2f, 3.5f, 0.7f)
            reflectiveCurveToRelative(2.1f, 1.1f, 2.9f, 1.9f)
            verticalLineToRelative(-1.2f)
            horizontalLineToRelative(1.8f)
            verticalLineToRelative(5f)
            horizontalLineToRelative(-5f)
            close()
        }
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(8.2f, 13.5f)
            horizontalLineToRelative(8f)
            lineToRelative(-2.6f, -3.5f)
            lineToRelative(-1.9f, 2.5f)
            lineToRelative(-1.3f, -1.8f)
            lineToRelative(-2.2f, 2.8f)
            close()
        }
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(13.11f, 15.744f)
            lineToRelative(1.645f, 0.95f)
            lineToRelative(-1.2f, 2.078f)
            curveToRelative(1.129f, -0.156f, 2.049f, -0.549f, 2.982f, -1.165f)
            reflectiveCurveToRelative(1.606f, -1.382f, 2.206f, -2.421f)
            curveToRelative(0.4f, -0.693f, 0.713f, -1.436f, 0.854f, -2.278f)
            curveToRelative(0.09f, -0.756f, 0.18f, -1.512f, 0.047f, -2.282f)
            lineToRelative(1.836f, 0.021f)
            curveToRelative(0.033f, 0.943f, -0.021f, 1.836f, -0.211f, 2.765f)
            reflectiveCurveToRelative(-0.517f, 1.895f, -1.017f, 2.761f)
            curveToRelative(-0.6f, 1.039f, -1.373f, 1.978f, -2.356f, 2.681f)
            reflectiveCurveToRelative(-2.003f, 1.269f, -3.095f, 1.561f)
            lineToRelative(1.039f, 0.6f)
            lineToRelative(-0.9f, 1.559f)
            lineToRelative(-4.33f, -2.5f)
            lineToRelative(2.5f, -4.33f)
            close()
        }
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(8.33f, 10.126f)
            lineToRelative(-1.634f, 0.969f)
            lineToRelative(-1.224f, -2.065f)
            curveToRelative(-0.417f, 1.061f, -0.525f, 2.055f, -0.446f, 3.17f)
            reflectiveCurveToRelative(0.418f, 2.077f, 1.03f, 3.11f)
            curveToRelative(0.408f, 0.688f, 0.902f, 1.325f, 1.568f, 1.861f)
            curveToRelative(0.615f, 0.449f, 1.23f, 0.898f, 1.966f, 1.16f)
            lineToRelative(-0.917f, 1.59f)
            curveToRelative(-0.838f, -0.433f, -1.59f, -0.917f, -2.307f, -1.539f)
            reflectiveCurveToRelative(-1.399f, -1.379f, -1.909f, -2.24f)
            curveToRelative(-0.612f, -1.032f, -1.052f, -2.167f, -1.183f, -3.368f)
            reflectiveCurveToRelative(-0.125f, -2.367f, 0.156f, -3.463f)
            lineToRelative(-1.032f, 0.612f)
            lineToRelative(-0.918f, -1.548f)
            lineToRelative(4.301f, -2.55f)
            lineToRelative(2.55f, 4.301f)
            close()
        }
    }.build()
}