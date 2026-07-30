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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons

val Icons.Outlined.ToneCurve: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.ToneCurve",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round
        ) {
            moveTo(7.5f, 16.5f)
            curveToRelative(6.446f, 0f, 2.355f, -9f, 9f, -9f)
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(5f, 6f)
            verticalLineToRelative(-1f)
            horizontalLineToRelative(2f)
            curveToRelative(0.552f, 0f, 1f, -0.448f, 1f, -1f)
            curveToRelative(0f, -0.552f, -0.448f, -1f, -1f, -1f)
            horizontalLineToRelative(-2f)
            curveToRelative(-0.55f, 0f, -1.021f, 0.196f, -1.413f, 0.588f)
            curveToRelative(-0.392f, 0.392f, -0.587f, 0.862f, -0.587f, 1.412f)
            verticalLineToRelative(2f)
            curveToRelative(0f, 0.552f, 0.448f, 1f, 1f, 1f)
            reflectiveCurveToRelative(1f, -0.448f, 1f, -1f)
            verticalLineToRelative(-1f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(20.412f, 3.588f)
            curveToRelative(-0.392f, -0.392f, -0.862f, -0.588f, -1.412f, -0.588f)
            horizontalLineToRelative(-2f)
            curveToRelative(-0.552f, 0f, -1f, 0.448f, -1f, 1f)
            curveToRelative(0f, 0.552f, 0.448f, 1f, 1f, 1f)
            horizontalLineToRelative(2f)
            verticalLineToRelative(2f)
            curveToRelative(0f, 0.552f, 0.448f, 1f, 1f, 1f)
            reflectiveCurveToRelative(1f, -0.448f, 1f, -1f)
            verticalLineToRelative(-2f)
            curveToRelative(0f, -0.55f, -0.196f, -1.021f, -0.588f, -1.412f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(7f, 19f)
            horizontalLineToRelative(-2f)
            verticalLineToRelative(-2f)
            curveToRelative(0f, -0.552f, -0.448f, -1f, -1f, -1f)
            reflectiveCurveToRelative(-1f, 0.448f, -1f, 1f)
            verticalLineToRelative(2f)
            curveToRelative(0f, 0.55f, 0.196f, 1.021f, 0.587f, 1.413f)
            curveToRelative(0.392f, 0.392f, 0.863f, 0.587f, 1.413f, 0.587f)
            horizontalLineToRelative(2f)
            curveToRelative(0.552f, 0f, 1f, -0.448f, 1f, -1f)
            curveToRelative(0f, -0.552f, -0.448f, -1f, -1f, -1f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(19f, 17f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(-2f)
            curveToRelative(-0.552f, 0f, -1f, 0.448f, -1f, 1f)
            reflectiveCurveToRelative(0.448f, 1f, 1f, 1f)
            horizontalLineToRelative(2f)
            curveToRelative(0.55f, 0f, 1.021f, -0.196f, 1.413f, -0.587f)
            curveToRelative(0.392f, -0.392f, 0.587f, -0.863f, 0.587f, -1.413f)
            verticalLineToRelative(-2f)
            curveToRelative(0f, -0.552f, -0.448f, -1f, -1f, -1f)
            curveToRelative(-0.552f, 0f, -1f, 0.448f, -1f, 1f)
            close()
        }
    }.build()
}

val Icons.TwoTone.ToneCurve: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "TwoTone.ToneCurve",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 2f,
            strokeLineCap = StrokeCap.Round
        ) {
            moveTo(7.5f, 16.5f)
            curveToRelative(6.446f, 0f, 2.355f, -9f, 9f, -9f)
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(4.5f, 5.5f)
            verticalLineToRelative(-1f)
            horizontalLineToRelative(2f)
            curveToRelative(0.552f, 0f, 1f, -0.448f, 1f, -1f)
            curveToRelative(0f, -0.552f, -0.448f, -1f, -1f, -1f)
            horizontalLineToRelative(-2f)
            curveToRelative(-0.55f, 0f, -1.021f, 0.196f, -1.413f, 0.588f)
            curveToRelative(-0.392f, 0.392f, -0.587f, 0.862f, -0.587f, 1.412f)
            verticalLineToRelative(2f)
            curveToRelative(0f, 0.552f, 0.448f, 1f, 1f, 1f)
            reflectiveCurveToRelative(1f, -0.448f, 1f, -1f)
            verticalLineToRelative(-1f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(20.912f, 3.088f)
            curveToRelative(-0.392f, -0.392f, -0.862f, -0.588f, -1.412f, -0.588f)
            horizontalLineToRelative(-2f)
            curveToRelative(-0.552f, 0f, -1f, 0.448f, -1f, 1f)
            curveToRelative(0f, 0.552f, 0.448f, 1f, 1f, 1f)
            horizontalLineToRelative(2f)
            verticalLineToRelative(2f)
            curveToRelative(0f, 0.552f, 0.448f, 1f, 1f, 1f)
            reflectiveCurveToRelative(1f, -0.448f, 1f, -1f)
            verticalLineToRelative(-2f)
            curveToRelative(0f, -0.55f, -0.196f, -1.021f, -0.588f, -1.412f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(6.5f, 19.5f)
            horizontalLineToRelative(-2f)
            verticalLineToRelative(-2f)
            curveToRelative(0f, -0.552f, -0.448f, -1f, -1f, -1f)
            reflectiveCurveToRelative(-1f, 0.448f, -1f, 1f)
            verticalLineToRelative(2f)
            curveToRelative(0f, 0.55f, 0.196f, 1.021f, 0.587f, 1.413f)
            curveToRelative(0.392f, 0.392f, 0.863f, 0.587f, 1.413f, 0.587f)
            horizontalLineToRelative(2f)
            curveToRelative(0.552f, 0f, 1f, -0.448f, 1f, -1f)
            curveToRelative(0f, -0.552f, -0.448f, -1f, -1f, -1f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(19.5f, 17.5f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(-2f)
            curveToRelative(-0.552f, 0f, -1f, 0.448f, -1f, 1f)
            reflectiveCurveToRelative(0.448f, 1f, 1f, 1f)
            horizontalLineToRelative(2f)
            curveToRelative(0.55f, 0f, 1.021f, -0.196f, 1.413f, -0.587f)
            curveToRelative(0.392f, -0.392f, 0.587f, -0.863f, 0.587f, -1.413f)
            verticalLineToRelative(-2f)
            curveToRelative(0f, -0.552f, -0.448f, -1f, -1f, -1f)
            curveToRelative(-0.552f, 0f, -1f, 0.448f, -1f, 1f)
            close()
        }
        path(
            fill = SolidColor(Color.Black),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(5.168f, 2.5f)
            lineTo(18.832f, 2.5f)
            arcTo(2.668f, 2.668f, 0f, isMoreThanHalf = false, isPositiveArc = true, 21.5f, 5.168f)
            lineTo(21.5f, 18.832f)
            arcTo(2.668f, 2.668f, 0f, isMoreThanHalf = false, isPositiveArc = true, 18.832f, 21.5f)
            lineTo(5.168f, 21.5f)
            arcTo(2.668f, 2.668f, 0f, isMoreThanHalf = false, isPositiveArc = true, 2.5f, 18.832f)
            lineTo(2.5f, 5.168f)
            arcTo(2.668f, 2.668f, 0f, isMoreThanHalf = false, isPositiveArc = true, 5.168f, 2.5f)
            close()
        }
    }.build()
}