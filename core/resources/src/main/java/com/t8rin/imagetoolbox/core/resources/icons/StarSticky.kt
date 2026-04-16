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

val Icons.Outlined.StarSticky: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.StarSticky",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(20.412f, 3.588f)
            curveToRelative(-0.392f, -0.392f, -0.862f, -0.588f, -1.412f, -0.588f)
            horizontalLineTo(5f)
            curveToRelative(-0.55f, 0f, -1.021f, 0.196f, -1.412f, 0.588f)
            reflectiveCurveToRelative(-0.588f, 0.862f, -0.588f, 1.412f)
            verticalLineToRelative(14f)
            curveToRelative(0f, 0.55f, 0.196f, 1.021f, 0.588f, 1.412f)
            reflectiveCurveToRelative(0.862f, 0.588f, 1.412f, 0.588f)
            horizontalLineToRelative(10.175f)
            curveToRelative(0.267f, 0f, 0.521f, -0.05f, 0.763f, -0.15f)
            curveToRelative(0.242f, -0.1f, 0.454f, -0.242f, 0.638f, -0.425f)
            lineToRelative(3.85f, -3.85f)
            curveToRelative(0.183f, -0.183f, 0.325f, -0.396f, 0.425f, -0.638f)
            curveToRelative(0.1f, -0.242f, 0.15f, -0.496f, 0.15f, -0.763f)
            verticalLineTo(5f)
            curveToRelative(0f, -0.55f, -0.196f, -1.021f, -0.588f, -1.412f)
            close()
            moveTo(19f, 15f)
            horizontalLineToRelative(-2f)
            curveToRelative(-0.55f, 0f, -1.021f, 0.196f, -1.412f, 0.588f)
            reflectiveCurveToRelative(-0.588f, 0.862f, -0.588f, 1.412f)
            verticalLineToRelative(2f)
            horizontalLineTo(5f)
            verticalLineTo(5f)
            horizontalLineToRelative(14f)
            verticalLineToRelative(10f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(5f, 19f)
            verticalLineTo(5f)
            verticalLineToRelative(14f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(12.11f, 13.595f)
            lineToRelative(1.938f, 1.454f)
            curveToRelative(0.114f, 0.095f, 0.223f, 0.105f, 0.328f, 0.029f)
            reflectiveCurveToRelative(0.138f, -0.181f, 0.1f, -0.314f)
            lineToRelative(-0.713f, -2.423f)
            lineToRelative(1.995f, -1.596f)
            curveToRelative(0.095f, -0.095f, 0.124f, -0.204f, 0.086f, -0.328f)
            reflectiveCurveToRelative(-0.124f, -0.185f, -0.257f, -0.185f)
            horizontalLineToRelative(-2.451f)
            lineToRelative(-0.741f, -2.337f)
            curveToRelative(-0.038f, -0.133f, -0.133f, -0.2f, -0.285f, -0.2f)
            reflectiveCurveToRelative(-0.247f, 0.067f, -0.285f, 0.2f)
            lineToRelative(-0.741f, 2.337f)
            horizontalLineToRelative(-2.451f)
            curveToRelative(-0.133f, 0f, -0.219f, 0.062f, -0.257f, 0.185f)
            reflectiveCurveToRelative(-0.01f, 0.233f, 0.086f, 0.328f)
            lineToRelative(1.995f, 1.596f)
            lineToRelative(-0.713f, 2.423f)
            curveToRelative(-0.038f, 0.133f, -0.005f, 0.238f, 0.1f, 0.314f)
            reflectiveCurveToRelative(0.214f, 0.067f, 0.328f, -0.029f)
            lineToRelative(1.938f, -1.454f)
            close()
        }
    }.build()
}
