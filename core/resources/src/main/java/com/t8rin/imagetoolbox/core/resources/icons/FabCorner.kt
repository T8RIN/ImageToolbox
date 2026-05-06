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

val Icons.Outlined.FabCorner: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.FabCorner",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(10.997f, 9f)
            lineTo(15.003f, 9f)
            arcTo(1.997f, 1.997f, 0f, isMoreThanHalf = false, isPositiveArc = true, 17f, 10.997f)
            lineTo(17f, 15.003f)
            arcTo(1.997f, 1.997f, 0f, isMoreThanHalf = false, isPositiveArc = true, 15.003f, 17f)
            lineTo(10.997f, 17f)
            arcTo(1.997f, 1.997f, 0f, isMoreThanHalf = false, isPositiveArc = true, 9f, 15.003f)
            lineTo(9f, 10.997f)
            arcTo(1.997f, 1.997f, 0f, isMoreThanHalf = false, isPositiveArc = true, 10.997f, 9f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(20f, 3f)
            curveToRelative(-0.552f, 0f, -1f, 0.448f, -1f, 1f)
            verticalLineToRelative(11.998f)
            curveToRelative(0f, 1.658f, -1.344f, 3.002f, -3.002f, 3.002f)
            horizontalLineTo(4f)
            curveToRelative(-0.552f, 0f, -1f, 0.448f, -1f, 1f)
            reflectiveCurveToRelative(0.448f, 1f, 1f, 1f)
            horizontalLineToRelative(12.497f)
            curveToRelative(2.487f, 0f, 4.503f, -2.016f, 4.503f, -4.503f)
            verticalLineTo(4f)
            curveToRelative(0f, -0.552f, -0.448f, -1f, -1f, -1f)
            close()
        }
    }.build()
}
