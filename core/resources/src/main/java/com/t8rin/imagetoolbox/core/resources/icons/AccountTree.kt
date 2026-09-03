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

val Icons.Outlined.AccountTree: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.AccountTree",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(15f, 19f)
            verticalLineToRelative(-1f)
            horizontalLineToRelative(-2f)
            curveToRelative(-0.55f, 0f, -1.021f, -0.196f, -1.413f, -0.587f)
            curveToRelative(-0.392f, -0.392f, -0.587f, -0.863f, -0.587f, -1.413f)
            verticalLineTo(8f)
            horizontalLineToRelative(-2f)
            verticalLineToRelative(1f)
            curveToRelative(0f, 0.55f, -0.196f, 1.021f, -0.587f, 1.413f)
            reflectiveCurveToRelative(-0.863f, 0.587f, -1.413f, 0.587f)
            horizontalLineToRelative(-3f)
            curveToRelative(-0.55f, 0f, -1.021f, -0.196f, -1.413f, -0.587f)
            reflectiveCurveToRelative(-0.587f, -0.863f, -0.587f, -1.413f)
            verticalLineToRelative(-4f)
            curveToRelative(0f, -0.55f, 0.196f, -1.021f, 0.587f, -1.413f)
            reflectiveCurveToRelative(0.863f, -0.587f, 1.413f, -0.587f)
            horizontalLineToRelative(3f)
            curveToRelative(0.55f, 0f, 1.021f, 0.196f, 1.413f, 0.587f)
            reflectiveCurveToRelative(0.587f, 0.863f, 0.587f, 1.413f)
            verticalLineToRelative(1f)
            horizontalLineToRelative(6f)
            verticalLineToRelative(-1f)
            curveToRelative(0f, -0.55f, 0.196f, -1.021f, 0.587f, -1.413f)
            reflectiveCurveToRelative(0.863f, -0.587f, 1.413f, -0.587f)
            horizontalLineToRelative(3f)
            curveToRelative(0.55f, 0f, 1.021f, 0.196f, 1.413f, 0.587f)
            reflectiveCurveToRelative(0.587f, 0.863f, 0.587f, 1.413f)
            verticalLineToRelative(4f)
            curveToRelative(0f, 0.55f, -0.196f, 1.021f, -0.587f, 1.413f)
            reflectiveCurveToRelative(-0.863f, 0.587f, -1.413f, 0.587f)
            horizontalLineToRelative(-3f)
            curveToRelative(-0.55f, 0f, -1.021f, -0.196f, -1.413f, -0.587f)
            reflectiveCurveToRelative(-0.587f, -0.863f, -0.587f, -1.413f)
            verticalLineToRelative(-1f)
            horizontalLineToRelative(-2f)
            verticalLineToRelative(8f)
            horizontalLineToRelative(2f)
            verticalLineToRelative(-1f)
            curveToRelative(0f, -0.55f, 0.196f, -1.021f, 0.587f, -1.413f)
            reflectiveCurveToRelative(0.863f, -0.587f, 1.413f, -0.587f)
            horizontalLineToRelative(3f)
            curveToRelative(0.55f, 0f, 1.021f, 0.196f, 1.413f, 0.587f)
            reflectiveCurveToRelative(0.587f, 0.863f, 0.587f, 1.413f)
            verticalLineToRelative(4f)
            curveToRelative(0f, 0.55f, -0.196f, 1.021f, -0.587f, 1.413f)
            curveToRelative(-0.392f, 0.392f, -0.863f, 0.587f, -1.413f, 0.587f)
            horizontalLineToRelative(-3f)
            curveToRelative(-0.55f, 0f, -1.021f, -0.196f, -1.413f, -0.587f)
            curveToRelative(-0.392f, -0.392f, -0.587f, -0.863f, -0.587f, -1.413f)
            close()
            moveTo(4f, 5f)
            verticalLineToRelative(4f)
            verticalLineToRelative(-4f)
            close()
            moveTo(17f, 15f)
            verticalLineToRelative(4f)
            verticalLineToRelative(-4f)
            close()
            moveTo(17f, 5f)
            verticalLineToRelative(4f)
            verticalLineToRelative(-4f)
            close()
            moveTo(17f, 9f)
            horizontalLineToRelative(3f)
            verticalLineToRelative(-4f)
            horizontalLineToRelative(-3f)
            verticalLineToRelative(4f)
            close()
            moveTo(17f, 19f)
            horizontalLineToRelative(3f)
            verticalLineToRelative(-4f)
            horizontalLineToRelative(-3f)
            verticalLineToRelative(4f)
            close()
            moveTo(4f, 9f)
            horizontalLineToRelative(3f)
            verticalLineToRelative(-4f)
            horizontalLineToRelative(-3f)
            verticalLineToRelative(4f)
            close()
        }
    }.build()
}

val Icons.TwoTone.AccountTree: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "TwoTone.AccountTree",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(15f, 19f)
            verticalLineToRelative(-1f)
            horizontalLineToRelative(-2f)
            curveToRelative(-0.55f, 0f, -1.021f, -0.196f, -1.413f, -0.587f)
            curveToRelative(-0.392f, -0.392f, -0.587f, -0.863f, -0.587f, -1.413f)
            verticalLineTo(8f)
            horizontalLineToRelative(-2f)
            verticalLineToRelative(1f)
            curveToRelative(0f, 0.55f, -0.196f, 1.021f, -0.587f, 1.413f)
            reflectiveCurveToRelative(-0.863f, 0.587f, -1.413f, 0.587f)
            horizontalLineToRelative(-3f)
            curveToRelative(-0.55f, 0f, -1.021f, -0.196f, -1.413f, -0.587f)
            reflectiveCurveToRelative(-0.587f, -0.863f, -0.587f, -1.413f)
            verticalLineToRelative(-4f)
            curveToRelative(0f, -0.55f, 0.196f, -1.021f, 0.587f, -1.413f)
            reflectiveCurveToRelative(0.863f, -0.587f, 1.413f, -0.587f)
            horizontalLineToRelative(3f)
            curveToRelative(0.55f, 0f, 1.021f, 0.196f, 1.413f, 0.587f)
            reflectiveCurveToRelative(0.587f, 0.863f, 0.587f, 1.413f)
            verticalLineToRelative(1f)
            horizontalLineToRelative(6f)
            verticalLineToRelative(-1f)
            curveToRelative(0f, -0.55f, 0.196f, -1.021f, 0.587f, -1.413f)
            reflectiveCurveToRelative(0.863f, -0.587f, 1.413f, -0.587f)
            horizontalLineToRelative(3f)
            curveToRelative(0.55f, 0f, 1.021f, 0.196f, 1.413f, 0.587f)
            reflectiveCurveToRelative(0.587f, 0.863f, 0.587f, 1.413f)
            verticalLineToRelative(4f)
            curveToRelative(0f, 0.55f, -0.196f, 1.021f, -0.587f, 1.413f)
            reflectiveCurveToRelative(-0.863f, 0.587f, -1.413f, 0.587f)
            horizontalLineToRelative(-3f)
            curveToRelative(-0.55f, 0f, -1.021f, -0.196f, -1.413f, -0.587f)
            reflectiveCurveToRelative(-0.587f, -0.863f, -0.587f, -1.413f)
            verticalLineToRelative(-1f)
            horizontalLineToRelative(-2f)
            verticalLineToRelative(8f)
            horizontalLineToRelative(2f)
            verticalLineToRelative(-1f)
            curveToRelative(0f, -0.55f, 0.196f, -1.021f, 0.587f, -1.413f)
            reflectiveCurveToRelative(0.863f, -0.587f, 1.413f, -0.587f)
            horizontalLineToRelative(3f)
            curveToRelative(0.55f, 0f, 1.021f, 0.196f, 1.413f, 0.587f)
            reflectiveCurveToRelative(0.587f, 0.863f, 0.587f, 1.413f)
            verticalLineToRelative(4f)
            curveToRelative(0f, 0.55f, -0.196f, 1.021f, -0.587f, 1.413f)
            curveToRelative(-0.392f, 0.392f, -0.863f, 0.587f, -1.413f, 0.587f)
            horizontalLineToRelative(-3f)
            curveToRelative(-0.55f, 0f, -1.021f, -0.196f, -1.413f, -0.587f)
            curveToRelative(-0.392f, -0.392f, -0.587f, -0.863f, -0.587f, -1.413f)
            close()
            moveTo(4f, 5f)
            verticalLineToRelative(4f)
            verticalLineToRelative(-4f)
            close()
            moveTo(17f, 15f)
            verticalLineToRelative(4f)
            verticalLineToRelative(-4f)
            close()
            moveTo(17f, 5f)
            verticalLineToRelative(4f)
            verticalLineToRelative(-4f)
            close()
            moveTo(17f, 9f)
            horizontalLineToRelative(3f)
            verticalLineToRelative(-4f)
            horizontalLineToRelative(-3f)
            verticalLineToRelative(4f)
            close()
            moveTo(17f, 19f)
            horizontalLineToRelative(3f)
            verticalLineToRelative(-4f)
            horizontalLineToRelative(-3f)
            verticalLineToRelative(4f)
            close()
            moveTo(4f, 9f)
            horizontalLineToRelative(3f)
            verticalLineToRelative(-4f)
            horizontalLineToRelative(-3f)
            verticalLineToRelative(4f)
            close()
        }
        path(
            fill = SolidColor(Color.Black),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(17f, 5f)
            horizontalLineToRelative(3f)
            verticalLineToRelative(4f)
            horizontalLineToRelative(-3f)
            close()
        }
        path(
            fill = SolidColor(Color.Black),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(17f, 15f)
            horizontalLineToRelative(3f)
            verticalLineToRelative(4f)
            horizontalLineToRelative(-3f)
            close()
        }
        path(
            fill = SolidColor(Color.Black),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(4f, 5f)
            horizontalLineToRelative(3f)
            verticalLineToRelative(4f)
            horizontalLineToRelative(-3f)
            close()
        }
    }.build()
}
