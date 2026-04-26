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

val Icons.Rounded.Public: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.Public",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(480f, 880f)
            quadToRelative(-83f, 0f, -156f, -31.5f)
            reflectiveQuadTo(197f, 763f)
            quadToRelative(-54f, -54f, -85.5f, -127f)
            reflectiveQuadTo(80f, 480f)
            quadToRelative(0f, -83f, 31.5f, -156f)
            reflectiveQuadTo(197f, 197f)
            quadToRelative(54f, -54f, 127f, -85.5f)
            reflectiveQuadTo(480f, 80f)
            quadToRelative(83f, 0f, 156f, 31.5f)
            reflectiveQuadTo(763f, 197f)
            quadToRelative(54f, 54f, 85.5f, 127f)
            reflectiveQuadTo(880f, 480f)
            quadToRelative(0f, 83f, -31.5f, 156f)
            reflectiveQuadTo(763f, 763f)
            quadToRelative(-54f, 54f, -127f, 85.5f)
            reflectiveQuadTo(480f, 880f)
            close()
            moveTo(440f, 798f)
            verticalLineToRelative(-78f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(360f, 640f)
            verticalLineToRelative(-40f)
            lineTo(168f, 408f)
            quadToRelative(-3f, 18f, -5.5f, 36f)
            reflectiveQuadToRelative(-2.5f, 36f)
            quadToRelative(0f, 121f, 79.5f, 212f)
            reflectiveQuadTo(440f, 798f)
            close()
            moveTo(716f, 696f)
            quadToRelative(20f, -22f, 36f, -47.5f)
            reflectiveQuadToRelative(26.5f, -53f)
            quadToRelative(10.5f, -27.5f, 16f, -56.5f)
            reflectiveQuadToRelative(5.5f, -59f)
            quadToRelative(0f, -98f, -54.5f, -179f)
            reflectiveQuadTo(600f, 184f)
            verticalLineToRelative(16f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(520f, 280f)
            horizontalLineToRelative(-80f)
            verticalLineToRelative(80f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(400f, 400f)
            horizontalLineToRelative(-80f)
            verticalLineToRelative(80f)
            horizontalLineToRelative(240f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(600f, 520f)
            verticalLineToRelative(120f)
            horizontalLineToRelative(40f)
            quadToRelative(26f, 0f, 47f, 15.5f)
            reflectiveQuadToRelative(29f, 40.5f)
            close()
        }
    }.build()
}
