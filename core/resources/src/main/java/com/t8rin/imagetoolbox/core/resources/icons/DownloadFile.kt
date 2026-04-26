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

val Icons.Outlined.DownloadFile: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.DownloadFile",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(13f, 15.6f)
            verticalLineToRelative(-3.175f)
            curveToRelative(0f, -0.283f, -0.096f, -0.521f, -0.287f, -0.712f)
            reflectiveCurveToRelative(-0.429f, -0.287f, -0.712f, -0.287f)
            reflectiveCurveToRelative(-0.521f, 0.096f, -0.712f, 0.287f)
            curveToRelative(-0.192f, 0.192f, -0.287f, 0.429f, -0.287f, 0.712f)
            verticalLineToRelative(3.175f)
            lineToRelative(-0.9f, -0.9f)
            curveToRelative(-0.1f, -0.1f, -0.213f, -0.175f, -0.338f, -0.225f)
            reflectiveCurveToRelative(-0.25f, -0.071f, -0.375f, -0.063f)
            reflectiveCurveToRelative(-0.246f, 0.038f, -0.363f, 0.087f)
            reflectiveCurveToRelative(-0.225f, 0.125f, -0.325f, 0.225f)
            curveToRelative(-0.183f, 0.2f, -0.279f, 0.433f, -0.287f, 0.7f)
            reflectiveCurveToRelative(0.087f, 0.5f, 0.287f, 0.7f)
            lineToRelative(2.6f, 2.6f)
            curveToRelative(0.1f, 0.1f, 0.208f, 0.171f, 0.325f, 0.213f)
            reflectiveCurveToRelative(0.242f, 0.063f, 0.375f, 0.063f)
            reflectiveCurveToRelative(0.258f, -0.021f, 0.375f, -0.063f)
            reflectiveCurveToRelative(0.225f, -0.112f, 0.325f, -0.213f)
            lineToRelative(2.6f, -2.6f)
            curveToRelative(0.2f, -0.2f, 0.296f, -0.433f, 0.287f, -0.7f)
            reflectiveCurveToRelative(-0.112f, -0.5f, -0.313f, -0.7f)
            curveToRelative(-0.2f, -0.183f, -0.433f, -0.279f, -0.7f, -0.287f)
            reflectiveCurveToRelative(-0.5f, 0.087f, -0.7f, 0.287f)
            lineToRelative(-0.875f, 0.875f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(19.85f, 8.063f)
            curveToRelative(-0.1f, -0.242f, -0.242f, -0.454f, -0.425f, -0.638f)
            lineToRelative(-4.85f, -4.85f)
            curveToRelative(-0.183f, -0.183f, -0.396f, -0.325f, -0.638f, -0.425f)
            curveToRelative(-0.242f, -0.1f, -0.496f, -0.15f, -0.763f, -0.15f)
            horizontalLineToRelative(-7.175f)
            curveToRelative(-0.55f, 0f, -1.021f, 0.196f, -1.412f, 0.588f)
            reflectiveCurveToRelative(-0.588f, 0.862f, -0.588f, 1.412f)
            verticalLineToRelative(16f)
            curveToRelative(0f, 0.55f, 0.196f, 1.021f, 0.588f, 1.412f)
            reflectiveCurveToRelative(0.862f, 0.588f, 1.412f, 0.588f)
            horizontalLineToRelative(12f)
            curveToRelative(0.55f, 0f, 1.021f, -0.196f, 1.412f, -0.588f)
            reflectiveCurveToRelative(0.588f, -0.862f, 0.588f, -1.412f)
            verticalLineToRelative(-11.175f)
            curveToRelative(0f, -0.267f, -0.05f, -0.521f, -0.15f, -0.763f)
            close()
            moveTo(18f, 20f)
            horizontalLineTo(6f)
            verticalLineTo(4f)
            horizontalLineToRelative(7f)
            verticalLineToRelative(4f)
            curveToRelative(0f, 0.283f, 0.096f, 0.521f, 0.287f, 0.713f)
            curveToRelative(0.192f, 0.192f, 0.429f, 0.287f, 0.713f, 0.287f)
            horizontalLineToRelative(4f)
            verticalLineToRelative(11f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(6f, 4f)
            lineToRelative(0f, 5f)
            lineToRelative(0f, -5f)
            lineToRelative(0f, 16f)
            lineToRelative(0f, -16f)
            close()
        }
    }.build()
}
