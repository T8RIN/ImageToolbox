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

val Icons.Outlined.ImageSaw: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.ImageSawTwoTone",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(7f, 14.143f)
            lineToRelative(10f, 0f)
            lineToRelative(-3.214f, -4.286f)
            lineToRelative(-2.5f, 3.214f)
            lineToRelative(-1.786f, -2.143f)
            lineToRelative(-2.5f, 3.214f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(20f, 15f)
            lineToRelative(2f, -2f)
            verticalLineToRelative(-4f)
            curveToRelative(-2.3f, 1.4f, -2.2f, -0.5f, -2.2f, -0.5f)
            verticalLineToRelative(-2.9f)
            lineToRelative(-2.8f, -2.8f)
            curveToRelative(-0.7f, 2.6f, -2f, 1.2f, -2f, 1.2f)
            lineToRelative(-2f, -2f)
            horizontalLineToRelative(-4f)
            curveToRelative(1.4f, 2.3f, -0.5f, 2.2f, -0.5f, 2.2f)
            horizontalLineToRelative(-2.9f)
            lineToRelative(-2.8f, 2.9f)
            curveToRelative(2.6f, 0.6f, 1.2f, 1.9f, 1.2f, 1.9f)
            lineToRelative(-2f, 2f)
            verticalLineToRelative(4f)
            curveToRelative(2.3f, -1.4f, 2.2f, 0.5f, 2.2f, 0.5f)
            verticalLineToRelative(2.8f)
            lineToRelative(2.8f, 2.8f)
            curveToRelative(0.7f, -2.5f, 2f, -1.1f, 2f, -1.1f)
            lineToRelative(2f, 2f)
            horizontalLineToRelative(4f)
            curveToRelative(-1.4f, -2.3f, 0.5f, -2.2f, 0.5f, -2.2f)
            horizontalLineToRelative(2.8f)
            lineToRelative(2.8f, -2.8f)
            curveToRelative(-2.5f, -0.7f, -1.1f, -2f, -1.1f, -2f)
            close()
            moveTo(12f, 19f)
            curveToRelative(-3.866f, 0f, -7f, -3.134f, -7f, -7f)
            reflectiveCurveToRelative(3.134f, -7f, 7f, -7f)
            reflectiveCurveToRelative(7f, 3.134f, 7f, 7f)
            reflectiveCurveToRelative(-3.134f, 7f, -7f, 7f)
            close()
        }
    }.build()
}
val Icons.TwoTone.ImageSaw: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "TwoTone.ImageSawTwoTone",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color.Black),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(12f, 5f)
            lineTo(12f, 5f)
            arcTo(7f, 7f, 0f, isMoreThanHalf = false, isPositiveArc = true, 19f, 12f)
            lineTo(19f, 12f)
            arcTo(7f, 7f, 0f, isMoreThanHalf = false, isPositiveArc = true, 12f, 19f)
            lineTo(12f, 19f)
            arcTo(7f, 7f, 0f, isMoreThanHalf = false, isPositiveArc = true, 5f, 12f)
            lineTo(5f, 12f)
            arcTo(7f, 7f, 0f, isMoreThanHalf = false, isPositiveArc = true, 12f, 5f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(7f, 14.143f)
            lineToRelative(10f, 0f)
            lineToRelative(-3.214f, -4.286f)
            lineToRelative(-2.5f, 3.214f)
            lineToRelative(-1.786f, -2.143f)
            lineToRelative(-2.5f, 3.214f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(20f, 15f)
            lineToRelative(2f, -2f)
            verticalLineToRelative(-4f)
            curveToRelative(-2.3f, 1.4f, -2.2f, -0.5f, -2.2f, -0.5f)
            verticalLineToRelative(-2.9f)
            lineToRelative(-2.8f, -2.8f)
            curveToRelative(-0.7f, 2.6f, -2f, 1.2f, -2f, 1.2f)
            lineToRelative(-2f, -2f)
            horizontalLineToRelative(-4f)
            curveToRelative(1.4f, 2.3f, -0.5f, 2.2f, -0.5f, 2.2f)
            horizontalLineToRelative(-2.9f)
            lineToRelative(-2.8f, 2.9f)
            curveToRelative(2.6f, 0.6f, 1.2f, 1.9f, 1.2f, 1.9f)
            lineToRelative(-2f, 2f)
            verticalLineToRelative(4f)
            curveToRelative(2.3f, -1.4f, 2.2f, 0.5f, 2.2f, 0.5f)
            verticalLineToRelative(2.8f)
            lineToRelative(2.8f, 2.8f)
            curveToRelative(0.7f, -2.5f, 2f, -1.1f, 2f, -1.1f)
            lineToRelative(2f, 2f)
            horizontalLineToRelative(4f)
            curveToRelative(-1.4f, -2.3f, 0.5f, -2.2f, 0.5f, -2.2f)
            horizontalLineToRelative(2.8f)
            lineToRelative(2.8f, -2.8f)
            curveToRelative(-2.5f, -0.7f, -1.1f, -2f, -1.1f, -2f)
            close()
            moveTo(12f, 19f)
            curveToRelative(-3.866f, 0f, -7f, -3.134f, -7f, -7f)
            reflectiveCurveToRelative(3.134f, -7f, 7f, -7f)
            reflectiveCurveToRelative(7f, 3.134f, 7f, 7f)
            reflectiveCurveToRelative(-3.134f, 7f, -7f, 7f)
            close()
        }
    }.build()
}
