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

val Icons.Rounded.Blender: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.Blender",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(320f, 880f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(240f, 800f)
            verticalLineToRelative(-40f)
            quadToRelative(0f, -47f, 20.5f, -87f)
            reflectiveQuadToRelative(53.5f, -67f)
            lineToRelative(-25f, -166f)
            horizontalLineToRelative(-89f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(120f, 360f)
            verticalLineToRelative(-160f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(200f, 120f)
            horizontalLineToRelative(200f)
            quadToRelative(11f, -11f, 17.5f, -25.5f)
            reflectiveQuadTo(440f, 80f)
            horizontalLineToRelative(80f)
            quadToRelative(16f, 0f, 22.5f, 14.5f)
            reflectiveQuadTo(560f, 120f)
            horizontalLineToRelative(112f)
            quadToRelative(18f, 0f, 30.5f, 14f)
            reflectiveQuadToRelative(9.5f, 32f)
            lineToRelative(-66f, 440f)
            quadToRelative(33f, 27f, 53.5f, 67f)
            reflectiveQuadToRelative(20.5f, 87f)
            verticalLineToRelative(40f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(640f, 880f)
            lineTo(320f, 880f)
            close()
            moveTo(277f, 360f)
            lineTo(253f, 200f)
            horizontalLineToRelative(-53f)
            verticalLineToRelative(160f)
            horizontalLineToRelative(77f)
            close()
            moveTo(480f, 760f)
            quadToRelative(17f, 0f, 28.5f, -11.5f)
            reflectiveQuadTo(520f, 720f)
            quadToRelative(0f, -17f, -11.5f, -28.5f)
            reflectiveQuadTo(480f, 680f)
            quadToRelative(-17f, 0f, -28.5f, 11.5f)
            reflectiveQuadTo(440f, 720f)
            quadToRelative(0f, 17f, 11.5f, 28.5f)
            reflectiveQuadTo(480f, 760f)
            close()
            moveTo(388f, 560f)
            horizontalLineToRelative(184f)
            lineToRelative(54f, -360f)
            lineTo(334f, 200f)
            lineToRelative(54f, 360f)
            close()
        }
    }.build()
}
