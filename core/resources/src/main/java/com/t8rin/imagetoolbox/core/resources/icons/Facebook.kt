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

val Icons.Rounded.Facebook: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.Facebook",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(9.101f, 23.691f)
            verticalLineToRelative(-7.98f)
            horizontalLineTo(6.627f)
            verticalLineToRelative(-3.667f)
            horizontalLineToRelative(2.474f)
            verticalLineToRelative(-1.58f)
            curveToRelative(0f, -4.085f, 1.848f, -5.978f, 5.858f, -5.978f)
            curveToRelative(0.401f, 0f, 0.955f, 0.042f, 1.468f, 0.103f)
            arcToRelative(
                8.68f,
                8.68f,
                0f,
                isMoreThanHalf = false,
                isPositiveArc = true,
                1.141f,
                0.195f
            )
            verticalLineToRelative(3.325f)
            arcToRelative(
                8.623f,
                8.623f,
                0f,
                isMoreThanHalf = false,
                isPositiveArc = false,
                -0.653f,
                -0.036f
            )
            arcToRelative(
                26.805f,
                26.805f,
                0f,
                isMoreThanHalf = false,
                isPositiveArc = false,
                -0.733f,
                -0.009f
            )
            curveToRelative(-0.707f, 0f, -1.259f, 0.096f, -1.675f, 0.309f)
            arcToRelative(
                1.686f,
                1.686f,
                0f,
                isMoreThanHalf = false,
                isPositiveArc = false,
                -0.679f,
                0.622f
            )
            curveToRelative(-0.258f, 0.42f, -0.374f, 0.995f, -0.374f, 1.752f)
            verticalLineToRelative(1.297f)
            horizontalLineToRelative(3.919f)
            lineToRelative(-0.386f, 2.103f)
            lineToRelative(-0.287f, 1.564f)
            horizontalLineToRelative(-3.246f)
            verticalLineToRelative(8.245f)
            curveTo(19.396f, 23.238f, 24f, 18.179f, 24f, 12.044f)
            curveToRelative(0f, -6.627f, -5.373f, -12f, -12f, -12f)
            reflectiveCurveToRelative(-12f, 5.373f, -12f, 12f)
            curveToRelative(0f, 5.628f, 3.874f, 10.35f, 9.101f, 11.647f)
            close()
        }
    }.build()
}
