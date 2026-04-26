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

val Icons.Rounded.ImageReset: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.ImageReset",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(320f, 360f)
            lineTo(160f, 360f)
            quadToRelative(-17f, 0f, -28.5f, -11.5f)
            reflectiveQuadTo(120f, 320f)
            verticalLineToRelative(-160f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(160f, 120f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(200f, 160f)
            verticalLineToRelative(94f)
            quadToRelative(50f, -62f, 122.5f, -98f)
            reflectiveQuadTo(480f, 120f)
            quadToRelative(99f, 0f, 179.5f, 48f)
            reflectiveQuadTo(788f, 294f)
            quadToRelative(8f, 14f, 4.5f, 30f)
            reflectiveQuadTo(775f, 348f)
            quadToRelative(-14f, 8f, -30.5f, 4.5f)
            reflectiveQuadTo(720f, 335f)
            quadToRelative(-37f, -61f, -100f, -98f)
            reflectiveQuadToRelative(-140f, -37f)
            quadToRelative(-57f, 0f, -107.5f, 21f)
            reflectiveQuadTo(284f, 280f)
            horizontalLineToRelative(36f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(360f, 320f)
            quadToRelative(0f, 17f, -11.5f, 28.5f)
            reflectiveQuadTo(320f, 360f)
            close()
            moveTo(280f, 720f)
            horizontalLineToRelative(400f)
            quadToRelative(12f, 0f, 18f, -11f)
            reflectiveQuadToRelative(-2f, -21f)
            lineTo(586f, 541f)
            quadToRelative(-6f, -8f, -16f, -8f)
            reflectiveQuadToRelative(-16f, 8f)
            lineTo(450f, 680f)
            lineToRelative(-74f, -99f)
            quadToRelative(-6f, -8f, -16f, -8f)
            reflectiveQuadToRelative(-16f, 8f)
            lineToRelative(-80f, 107f)
            quadToRelative(-8f, 10f, -2f, 21f)
            reflectiveQuadToRelative(18f, 11f)
            close()
            moveTo(200f, 880f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(120f, 800f)
            verticalLineToRelative(-280f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(160f, 480f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(200f, 520f)
            verticalLineToRelative(280f)
            horizontalLineToRelative(560f)
            verticalLineToRelative(-280f)
            quadToRelative(0f, -17f, 11.5f, -28.5f)
            reflectiveQuadTo(800f, 480f)
            quadToRelative(17f, 0f, 28.5f, 11.5f)
            reflectiveQuadTo(840f, 520f)
            verticalLineToRelative(280f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(760f, 880f)
            lineTo(200f, 880f)
            close()
        }
    }.build()
}