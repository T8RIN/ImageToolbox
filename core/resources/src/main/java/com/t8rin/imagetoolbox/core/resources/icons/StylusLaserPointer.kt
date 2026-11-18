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
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons

val Icons.Outlined.StylusLaserPointer: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.StylusLaserPointer",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        group(translationY = 960f) {
            path(fill = SolidColor(Color.Black)) {
                moveTo(360f, -80f)
                quadToRelative(-50f, 0f, -85f, -35f)
                reflectiveQuadToRelative(-35f, -85f)
                quadToRelative(0f, -50f, 35f, -85f)
                reflectiveQuadToRelative(85f, -35f)
                quadToRelative(50f, 0f, 85f, 35f)
                reflectiveQuadToRelative(35f, 85f)
                quadToRelative(0f, 50f, -35f, 85f)
                reflectiveQuadToRelative(-85f, 35f)
                close()
                moveToRelative(179f, -139f)
                quadToRelative(-6f, -55f, -41f, -97f)
                reflectiveQuadToRelative(-87f, -57f)
                lineToRelative(106f, -107f)
                horizontalLineTo(236f)
                quadToRelative(-32f, 0f, -54f, -22f)
                reflectiveQuadToRelative(-22f, -54f)
                quadToRelative(0f, -20f, 10.5f, -37.5f)
                reflectiveQuadTo(198f, -622f)
                lineToRelative(486f, -291f)
                quadToRelative(18f, -11f, 38f, -5.5f)
                reflectiveQuadToRelative(31f, 23.5f)
                quadToRelative(11f, 18f, 5.5f, 37.5f)
                reflectiveQuadTo(736f, -827f)
                lineTo(360f, -600f)
                horizontalLineToRelative(364f)
                quadToRelative(32f, 0f, 54f, 22f)
                reflectiveQuadToRelative(22f, 54f)
                quadToRelative(0f, 18f, -4.5f, 35.5f)
                reflectiveQuadTo(778f, -458f)
                lineTo(539f, -219f)
                close()
            }
        }
    }.build()
}