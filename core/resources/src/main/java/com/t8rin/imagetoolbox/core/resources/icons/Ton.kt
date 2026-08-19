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

val Icons.Rounded.Ton: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.Ton",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(12f, 0f)
            curveTo(5.373f, 0f, 0f, 5.373f, 0f, 12f)
            reflectiveCurveToRelative(5.373f, 12f, 12f, 12f)
            reflectiveCurveToRelative(12f, -5.373f, 12f, -12f)
            reflectiveCurveTo(18.627f, 0f, 12f, 0f)
            close()
            moveTo(7.902f, 6.697f)
            horizontalLineToRelative(8.196f)
            curveToRelative(1.505f, 0f, 2.462f, 1.628f, 1.705f, 2.94f)
            lineToRelative(-5.059f, 8.765f)
            arcToRelative(
                0.86f,
                0.86f,
                0f,
                isMoreThanHalf = false,
                isPositiveArc = true,
                -1.488f,
                0f
            )
            lineTo(6.199f, 9.637f)
            curveToRelative(-0.758f, -1.314f, 0.197f, -2.94f, 1.703f, -2.94f)
            close()
            moveTo(12.746f, 8.193f)
            verticalLineToRelative(7.58f)
            lineToRelative(1.102f, -2.128f)
            lineToRelative(2.656f, -4.756f)
            arcToRelative(
                0.465f,
                0.465f,
                0f,
                isMoreThanHalf = false,
                isPositiveArc = false,
                -0.408f,
                -0.696f
            )
            horizontalLineToRelative(-3.35f)
            close()
            moveTo(7.9f, 8.195f)
            arcToRelative(
                0.464f,
                0.464f,
                0f,
                isMoreThanHalf = false,
                isPositiveArc = false,
                -0.408f,
                0.694f
            )
            lineToRelative(2.658f, 4.754f)
            lineToRelative(1.102f, 2.13f)
            lineTo(11.252f, 8.195f)
            lineTo(7.9f, 8.195f)
            close()
        }
    }.build()
}