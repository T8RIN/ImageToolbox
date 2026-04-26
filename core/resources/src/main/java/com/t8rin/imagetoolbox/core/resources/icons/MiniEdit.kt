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

import com.t8rin.imagetoolbox.core.resources.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Rounded.MiniEdit: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.MiniEdit",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(18.825f, 7.917f)
            curveToRelative(-0.117f, -0.285f, -0.278f, -0.544f, -0.486f, -0.778f)
            lineToRelative(-1.439f, -1.439f)
            curveToRelative(-0.233f, -0.233f, -0.492f, -0.408f, -0.778f, -0.525f)
            curveToRelative(-0.285f, -0.117f, -0.583f, -0.175f, -0.894f, -0.175f)
            curveToRelative(-0.285f, 0f, -0.57f, 0.052f, -0.856f, 0.156f)
            curveToRelative(-0.285f, 0.103f, -0.544f, 0.272f, -0.778f, 0.506f)
            lineTo(5.467f, 13.75f)
            curveToRelative(-0.156f, 0.156f, -0.272f, 0.331f, -0.35f, 0.525f)
            reflectiveCurveToRelative(-0.117f, 0.395f, -0.117f, 0.603f)
            verticalLineToRelative(2.567f)
            curveToRelative(0f, 0.441f, 0.149f, 0.81f, 0.447f, 1.108f)
            curveToRelative(0.298f, 0.298f, 0.667f, 0.447f, 1.108f, 0.447f)
            horizontalLineToRelative(2.567f)
            curveToRelative(0.208f, 0f, 0.408f, -0.039f, 0.603f, -0.117f)
            reflectiveCurveToRelative(0.369f, -0.194f, 0.525f, -0.35f)
            lineToRelative(8.089f, -8.089f)
            curveToRelative(0.233f, -0.233f, 0.402f, -0.499f, 0.506f, -0.797f)
            curveToRelative(0.103f, -0.298f, 0.156f, -0.59f, 0.156f, -0.875f)
            reflectiveCurveToRelative(-0.058f, -0.57f, -0.175f, -0.856f)
            close()
            moveTo(8.811f, 16.667f)
            horizontalLineToRelative(-1.478f)
            verticalLineToRelative(-1.478f)
            lineToRelative(4.744f, -4.706f)
            lineToRelative(0.739f, 0.7f)
            lineToRelative(0.7f, 0.739f)
            lineToRelative(-4.706f, 4.744f)
            close()
        }
    }.build()
}