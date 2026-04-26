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

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Rounded.TextRotationAngleup: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.TextRotationAngleup",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(761f, 496f)
            lineTo(417f, 840f)
            quadToRelative(-11f, 11f, -28f, 11f)
            reflectiveQuadToRelative(-28f, -11f)
            quadToRelative(-11f, -11f, -11f, -28f)
            reflectiveQuadToRelative(11f, -28f)
            lineToRelative(344f, -344f)
            horizontalLineToRelative(-24f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(641f, 400f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(681f, 360f)
            horizontalLineToRelative(120f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(841f, 400f)
            verticalLineToRelative(120f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(801f, 560f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(761f, 520f)
            verticalLineToRelative(-24f)
            close()
            moveTo(333f, 484f)
            lineTo(372f, 568f)
            quadToRelative(5f, 10f, 3.5f, 20.5f)
            reflectiveQuadTo(366f, 607f)
            quadToRelative(-14f, 14f, -32f, 10.5f)
            reflectiveQuadTo(308f, 597f)
            lineTo(146f, 239f)
            quadToRelative(-5f, -11f, -3f, -22f)
            reflectiveQuadToRelative(10f, -19f)
            lineToRelative(20f, -20f)
            quadToRelative(8f, -8f, 19f, -10f)
            reflectiveQuadToRelative(22f, 3f)
            lineToRelative(359f, 164f)
            quadToRelative(17f, 8f, 20f, 26f)
            reflectiveQuadToRelative(-10f, 31f)
            quadToRelative(-8f, 8f, -19f, 10f)
            reflectiveQuadToRelative(-22f, -3f)
            lineToRelative(-83f, -41f)
            lineToRelative(-126f, 126f)
            close()
            moveTo(303f, 422f)
            lineTo(397f, 330f)
            lineTo(223f, 246f)
            lineTo(221f, 248f)
            lineTo(303f, 422f)
            close()
        }
    }.build()
}
