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

val Icons.Rounded.YouTube: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.YouTube",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(23.498f, 6.186f)
            arcToRelative(
                3.016f,
                3.016f,
                0f,
                isMoreThanHalf = false,
                isPositiveArc = false,
                -2.122f,
                -2.136f
            )
            curveTo(19.505f, 3.545f, 12f, 3.545f, 12f, 3.545f)
            reflectiveCurveToRelative(-7.505f, 0f, -9.377f, 0.505f)
            arcTo(3.017f, 3.017f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0.502f, 6.186f)
            curveTo(0f, 8.07f, 0f, 12f, 0f, 12f)
            reflectiveCurveToRelative(0f, 3.93f, 0.502f, 5.814f)
            arcToRelative(
                3.016f,
                3.016f,
                0f,
                isMoreThanHalf = false,
                isPositiveArc = false,
                2.122f,
                2.136f
            )
            curveToRelative(1.871f, 0.505f, 9.376f, 0.505f, 9.376f, 0.505f)
            reflectiveCurveToRelative(7.505f, 0f, 9.377f, -0.505f)
            arcToRelative(
                3.015f,
                3.015f,
                0f,
                isMoreThanHalf = false,
                isPositiveArc = false,
                2.122f,
                -2.136f
            )
            curveTo(24f, 15.93f, 24f, 12f, 24f, 12f)
            reflectiveCurveToRelative(0f, -3.93f, -0.502f, -5.814f)
            close()
            moveTo(9.545f, 15.568f)
            verticalLineTo(8.432f)
            lineTo(15.818f, 12f)
            lineToRelative(-6.273f, 3.568f)
            close()
        }
    }.build()
}
