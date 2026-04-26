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

val Icons.Outlined.SamsungLetter: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.SamsungLetter",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF010101))) {
            moveTo(13.706f, 15.551f)
            curveToRelative(0.163f, 0.401f, 0.113f, 0.919f, 0.036f, 1.231f)
            curveToRelative(-0.139f, 0.551f, -0.514f, 1.115f, -1.616f, 1.115f)
            curveToRelative(-1.042f, 0f, -1.672f, -0.597f, -1.672f, -1.506f)
            verticalLineToRelative(-1.606f)
            horizontalLineToRelative(-4.452f)
            lineToRelative(-0.003f, 1.284f)
            curveToRelative(0f, 3.699f, 2.913f, 4.817f, 6.034f, 4.817f)
            curveToRelative(3.002f, 0f, 5.474f, -1.025f, 5.865f, -3.791f)
            curveToRelative(0.202f, -1.433f, 0.05f, -2.372f, -0.017f, -2.727f)
            curveToRelative(-0.7f, -3.473f, -6.999f, -4.511f, -7.467f, -6.452f)
            curveToRelative(-0.08f, -0.331f, -0.056f, -0.687f, -0.017f, -0.876f)
            curveToRelative(0.116f, -0.527f, 0.478f, -1.111f, 1.516f, -1.111f)
            curveToRelative(0.969f, 0f, 1.542f, 0.601f, 1.542f, 1.506f)
            verticalLineToRelative(1.025f)
            horizontalLineToRelative(4.136f)
            verticalLineToRelative(-1.164f)
            curveToRelative(0f, -3.616f, -3.244f, -4.18f, -5.593f, -4.18f)
            curveToRelative(-2.952f, 0f, -5.364f, 0.975f, -5.805f, 3.675f)
            curveToRelative(-0.119f, 0.746f, -0.136f, 1.41f, 0.036f, 2.243f)
            curveToRelative(0.726f, 3.387f, 6.618f, 4.369f, 7.474f, 6.518f)
            lineToRelative(0.001f, -0.001f)
            close()
        }
    }.build()
}