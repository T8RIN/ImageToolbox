/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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

val Icons.Rounded.FolderImageAlt: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.FolderImageAlt",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(10.509f, 20.464f)
            lineToRelative(2.457f, -3.272f)
            curveToRelative(0.061f, -0.082f, 0.136f, -0.143f, 0.223f, -0.184f)
            reflectiveCurveToRelative(0.177f, -0.061f, 0.269f, -0.061f)
            reflectiveCurveToRelative(0.182f, 0.02f, 0.269f, 0.061f)
            reflectiveCurveToRelative(0.161f, 0.102f, 0.223f, 0.184f)
            lineToRelative(2.089f, 2.78f)
            curveToRelative(0.061f, 0.082f, 0.133f, 0.143f, 0.215f, 0.184f)
            reflectiveCurveToRelative(0.174f, 0.061f, 0.276f, 0.061f)
            curveToRelative(0.256f, 0f, 0.44f, -0.115f, 0.553f, -0.346f)
            curveToRelative(0.113f, -0.23f, 0.092f, -0.448f, -0.061f, -0.653f)
            lineToRelative(-1.29f, -1.705f)
            curveToRelative(-0.082f, -0.113f, -0.123f, -0.236f, -0.123f, -0.369f)
            curveToRelative(0f, -0.133f, 0.041f, -0.256f, 0.123f, -0.369f)
            lineToRelative(1.536f, -2.043f)
            curveToRelative(0.061f, -0.082f, 0.136f, -0.143f, 0.223f, -0.184f)
            reflectiveCurveToRelative(0.177f, -0.061f, 0.269f, -0.061f)
            reflectiveCurveToRelative(0.182f, 0.02f, 0.269f, 0.061f)
            reflectiveCurveToRelative(0.161f, 0.102f, 0.223f, 0.184f)
            lineToRelative(4.301f, 5.729f)
            curveToRelative(0.154f, 0.205f, 0.174f, 0.42f, 0.061f, 0.645f)
            curveToRelative(-0.113f, 0.225f, -0.297f, 0.338f, -0.553f, 0.338f)
            horizontalLineToRelative(-11.059f)
            curveToRelative(-0.256f, 0f, -0.44f, -0.113f, -0.553f, -0.338f)
            curveToRelative(-0.113f, -0.225f, -0.092f, -0.44f, 0.061f, -0.645f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(4f, 20f)
            curveToRelative(-0.55f, 0f, -1.021f, -0.196f, -1.412f, -0.587f)
            curveToRelative(-0.392f, -0.392f, -0.588f, -0.863f, -0.588f, -1.413f)
            verticalLineTo(6f)
            curveToRelative(0f, -0.55f, 0.196f, -1.021f, 0.588f, -1.413f)
            curveToRelative(0.392f, -0.392f, 0.863f, -0.587f, 1.412f, -0.587f)
            horizontalLineToRelative(5.175f)
            curveToRelative(0.267f, 0f, 0.521f, 0.05f, 0.762f, 0.15f)
            curveToRelative(0.242f, 0.1f, 0.454f, 0.242f, 0.637f, 0.425f)
            lineToRelative(1.425f, 1.425f)
            horizontalLineToRelative(8f)
            curveToRelative(0.55f, 0f, 1.021f, 0.196f, 1.413f, 0.587f)
            curveToRelative(0.392f, 0.392f, 0.587f, 0.863f, 0.587f, 1.413f)
            verticalLineToRelative(4f)
            curveToRelative(0f, 0.283f, -0.096f, 0.521f, -0.288f, 0.713f)
            curveToRelative(-0.192f, 0.192f, -0.429f, 0.287f, -0.712f, 0.287f)
            horizontalLineToRelative(-6f)
            curveToRelative(-1.667f, 0f, -3.083f, 0.583f, -4.25f, 1.75f)
            curveToRelative(-1.167f, 1.167f, -1.75f, 2.583f, -1.75f, 4.25f)
            curveToRelative(0f, 0.283f, -0.096f, 0.521f, -0.287f, 0.713f)
            curveToRelative(-0.192f, 0.192f, -0.429f, 0.288f, -0.713f, 0.288f)
            horizontalLineToRelative(-4f)
            close()
        }
    }.build()
}

val Icons.Outlined.FolderImageAlt: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.FolderImageAlt",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(4f, 18f)
            lineToRelative(0f, -12f)
            lineToRelative(0f, 6.925f)
            lineToRelative(0f, -0.925f)
            lineToRelative(0f, 6f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(10.617f, 20.464f)
            lineToRelative(2.457f, -3.272f)
            curveToRelative(0.061f, -0.082f, 0.136f, -0.143f, 0.223f, -0.184f)
            reflectiveCurveToRelative(0.177f, -0.061f, 0.269f, -0.061f)
            reflectiveCurveToRelative(0.182f, 0.02f, 0.269f, 0.061f)
            reflectiveCurveToRelative(0.161f, 0.102f, 0.223f, 0.184f)
            lineToRelative(2.089f, 2.78f)
            curveToRelative(0.061f, 0.082f, 0.133f, 0.143f, 0.215f, 0.184f)
            reflectiveCurveToRelative(0.174f, 0.061f, 0.276f, 0.061f)
            curveToRelative(0.256f, 0f, 0.44f, -0.115f, 0.553f, -0.346f)
            curveToRelative(0.113f, -0.23f, 0.092f, -0.448f, -0.061f, -0.653f)
            lineToRelative(-1.29f, -1.705f)
            curveToRelative(-0.082f, -0.113f, -0.123f, -0.236f, -0.123f, -0.369f)
            curveToRelative(0f, -0.133f, 0.041f, -0.256f, 0.123f, -0.369f)
            lineToRelative(1.536f, -2.043f)
            curveToRelative(0.061f, -0.082f, 0.136f, -0.143f, 0.223f, -0.184f)
            reflectiveCurveToRelative(0.177f, -0.061f, 0.269f, -0.061f)
            reflectiveCurveToRelative(0.182f, 0.02f, 0.269f, 0.061f)
            reflectiveCurveToRelative(0.161f, 0.102f, 0.223f, 0.184f)
            lineToRelative(4.301f, 5.729f)
            curveToRelative(0.154f, 0.205f, 0.174f, 0.42f, 0.061f, 0.645f)
            curveToRelative(-0.113f, 0.225f, -0.297f, 0.338f, -0.553f, 0.338f)
            horizontalLineToRelative(-11.059f)
            curveToRelative(-0.256f, 0f, -0.44f, -0.113f, -0.553f, -0.338f)
            curveToRelative(-0.113f, -0.225f, -0.092f, -0.44f, 0.061f, -0.645f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(21.412f, 6.588f)
            curveToRelative(-0.392f, -0.392f, -0.862f, -0.588f, -1.412f, -0.588f)
            horizontalLineToRelative(-8f)
            lineToRelative(-1.425f, -1.425f)
            curveToRelative(-0.183f, -0.183f, -0.396f, -0.325f, -0.638f, -0.425f)
            curveToRelative(-0.242f, -0.1f, -0.496f, -0.15f, -0.763f, -0.15f)
            horizontalLineToRelative(-5.175f)
            curveToRelative(-0.55f, 0f, -1.021f, 0.196f, -1.412f, 0.588f)
            reflectiveCurveToRelative(-0.588f, 0.862f, -0.588f, 1.412f)
            verticalLineToRelative(12f)
            curveToRelative(0f, 0.55f, 0.196f, 1.021f, 0.588f, 1.412f)
            reflectiveCurveToRelative(0.862f, 0.588f, 1.412f, 0.588f)
            horizontalLineToRelative(4f)
            curveToRelative(0.552f, 0f, 1f, -0.448f, 1f, -1f)
            reflectiveCurveToRelative(-0.448f, -1f, -1f, -1f)
            horizontalLineToRelative(-4f)
            verticalLineTo(6f)
            horizontalLineToRelative(5.175f)
            lineToRelative(1.425f, 1.425f)
            curveToRelative(0.183f, 0.183f, 0.396f, 0.325f, 0.638f, 0.425f)
            curveToRelative(0.242f, 0.1f, 0.496f, 0.15f, 0.763f, 0.15f)
            horizontalLineToRelative(8f)
            verticalLineToRelative(4f)
            curveToRelative(0f, 0.552f, 0.448f, 1f, 1f, 1f)
            reflectiveCurveToRelative(1f, -0.448f, 1f, -1f)
            verticalLineToRelative(-4f)
            curveToRelative(0f, -0.55f, -0.196f, -1.021f, -0.588f, -1.412f)
            close()
        }
    }.build()
}
