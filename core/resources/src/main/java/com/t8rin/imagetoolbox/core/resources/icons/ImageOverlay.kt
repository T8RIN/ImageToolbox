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

val Icons.Outlined.ImageOverlay: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.ImageOverlay",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(4f, 16f)
            curveToRelative(-0.55f, 0f, -1.021f, -0.196f, -1.413f, -0.587f)
            reflectiveCurveToRelative(-0.587f, -0.863f, -0.587f, -1.413f)
            verticalLineTo(4f)
            curveToRelative(0f, -0.55f, 0.196f, -1.021f, 0.587f, -1.413f)
            reflectiveCurveToRelative(0.863f, -0.587f, 1.413f, -0.587f)
            horizontalLineToRelative(10f)
            curveToRelative(0.55f, 0f, 1.021f, 0.196f, 1.413f, 0.587f)
            reflectiveCurveToRelative(0.587f, 0.863f, 0.587f, 1.413f)
            verticalLineToRelative(1f)
            curveToRelative(0f, 0.283f, -0.096f, 0.521f, -0.287f, 0.712f)
            reflectiveCurveToRelative(-0.429f, 0.287f, -0.712f, 0.287f)
            reflectiveCurveToRelative(-0.521f, -0.096f, -0.712f, -0.287f)
            reflectiveCurveToRelative(-0.287f, -0.429f, -0.287f, -0.712f)
            verticalLineToRelative(-1f)
            horizontalLineTo(4f)
            verticalLineToRelative(10f)
            horizontalLineToRelative(1f)
            curveToRelative(0.283f, 0f, 0.521f, 0.096f, 0.712f, 0.287f)
            reflectiveCurveToRelative(0.287f, 0.429f, 0.287f, 0.712f)
            reflectiveCurveToRelative(-0.096f, 0.521f, -0.287f, 0.712f)
            reflectiveCurveToRelative(-0.429f, 0.287f, -0.712f, 0.287f)
            horizontalLineToRelative(-1f)
            close()
            moveTo(10f, 22f)
            curveToRelative(-0.55f, 0f, -1.021f, -0.196f, -1.413f, -0.587f)
            reflectiveCurveToRelative(-0.587f, -0.863f, -0.587f, -1.413f)
            verticalLineToRelative(-10f)
            curveToRelative(0f, -0.55f, 0.196f, -1.021f, 0.587f, -1.413f)
            reflectiveCurveToRelative(0.863f, -0.587f, 1.413f, -0.587f)
            horizontalLineToRelative(10f)
            curveToRelative(0.55f, 0f, 1.021f, 0.196f, 1.413f, 0.587f)
            reflectiveCurveToRelative(0.587f, 0.863f, 0.587f, 1.413f)
            verticalLineToRelative(10f)
            curveToRelative(0f, 0.55f, -0.196f, 1.021f, -0.587f, 1.413f)
            reflectiveCurveToRelative(-0.863f, 0.587f, -1.413f, 0.587f)
            horizontalLineToRelative(-10f)
            close()
            moveTo(10f, 20f)
            horizontalLineToRelative(10f)
            verticalLineToRelative(-10f)
            horizontalLineToRelative(-10f)
            verticalLineToRelative(10f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(14.5f, 17f)
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

val Icons.TwoTone.ImageOverlay: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "TwoTone.ImageOverlay",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(4f, 16f)
            curveToRelative(-0.55f, 0f, -1.021f, -0.196f, -1.413f, -0.587f)
            reflectiveCurveToRelative(-0.587f, -0.863f, -0.587f, -1.413f)
            verticalLineTo(4f)
            curveToRelative(0f, -0.55f, 0.196f, -1.021f, 0.587f, -1.413f)
            reflectiveCurveToRelative(0.863f, -0.587f, 1.413f, -0.587f)
            horizontalLineToRelative(10f)
            curveToRelative(0.55f, 0f, 1.021f, 0.196f, 1.413f, 0.587f)
            reflectiveCurveToRelative(0.587f, 0.863f, 0.587f, 1.413f)
            verticalLineToRelative(1f)
            curveToRelative(0f, 0.283f, -0.096f, 0.521f, -0.287f, 0.712f)
            reflectiveCurveToRelative(-0.429f, 0.287f, -0.712f, 0.287f)
            reflectiveCurveToRelative(-0.521f, -0.096f, -0.712f, -0.287f)
            reflectiveCurveToRelative(-0.287f, -0.429f, -0.287f, -0.712f)
            verticalLineToRelative(-1f)
            horizontalLineTo(4f)
            verticalLineToRelative(10f)
            horizontalLineToRelative(1f)
            curveToRelative(0.283f, 0f, 0.521f, 0.096f, 0.712f, 0.287f)
            reflectiveCurveToRelative(0.287f, 0.429f, 0.287f, 0.712f)
            reflectiveCurveToRelative(-0.096f, 0.521f, -0.287f, 0.712f)
            reflectiveCurveToRelative(-0.429f, 0.287f, -0.712f, 0.287f)
            horizontalLineToRelative(-1f)
            close()
            moveTo(10f, 22f)
            curveToRelative(-0.55f, 0f, -1.021f, -0.196f, -1.413f, -0.587f)
            reflectiveCurveToRelative(-0.587f, -0.863f, -0.587f, -1.413f)
            verticalLineToRelative(-10f)
            curveToRelative(0f, -0.55f, 0.196f, -1.021f, 0.587f, -1.413f)
            reflectiveCurveToRelative(0.863f, -0.587f, 1.413f, -0.587f)
            horizontalLineToRelative(10f)
            curveToRelative(0.55f, 0f, 1.021f, 0.196f, 1.413f, 0.587f)
            reflectiveCurveToRelative(0.587f, 0.863f, 0.587f, 1.413f)
            verticalLineToRelative(10f)
            curveToRelative(0f, 0.55f, -0.196f, 1.021f, -0.587f, 1.413f)
            reflectiveCurveToRelative(-0.863f, 0.587f, -1.413f, 0.587f)
            horizontalLineToRelative(-10f)
            close()
            moveTo(10f, 20f)
            horizontalLineToRelative(10f)
            verticalLineToRelative(-10f)
            horizontalLineToRelative(-10f)
            verticalLineToRelative(10f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(14.5f, 17f)
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
            moveTo(10f, 10f)
            horizontalLineToRelative(10f)
            verticalLineToRelative(10f)
            horizontalLineToRelative(-10f)
            close()
        }
    }.build()
}