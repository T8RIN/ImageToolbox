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

val Icons.Outlined.GifBox: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.GifBox",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(200f, 840f)
            quadToRelative(-33f, 0f, -56.5f, -23.5f)
            reflectiveQuadTo(120f, 760f)
            verticalLineToRelative(-560f)
            quadToRelative(0f, -33f, 23.5f, -56.5f)
            reflectiveQuadTo(200f, 120f)
            horizontalLineToRelative(560f)
            quadToRelative(33f, 0f, 56.5f, 23.5f)
            reflectiveQuadTo(840f, 200f)
            verticalLineToRelative(560f)
            quadToRelative(0f, 33f, -23.5f, 56.5f)
            reflectiveQuadTo(760f, 840f)
            lineTo(200f, 840f)
            close()
            moveTo(200f, 760f)
            horizontalLineToRelative(560f)
            verticalLineToRelative(-560f)
            lineTo(200f, 200f)
            verticalLineToRelative(560f)
            close()
            moveTo(200f, 200f)
            verticalLineToRelative(560f)
            verticalLineToRelative(-560f)
            close()
            moveTo(340f, 560f)
            horizontalLineToRelative(40f)
            quadToRelative(17f, 0f, 28.5f, -11.5f)
            reflectiveQuadTo(420f, 520f)
            verticalLineToRelative(-20f)
            quadToRelative(0f, -8f, -6f, -14f)
            reflectiveQuadToRelative(-14f, -6f)
            quadToRelative(-8f, 0f, -14f, 6f)
            reflectiveQuadToRelative(-6f, 14f)
            verticalLineToRelative(20f)
            horizontalLineToRelative(-40f)
            verticalLineToRelative(-80f)
            horizontalLineToRelative(60f)
            quadToRelative(8f, 0f, 14f, -6f)
            reflectiveQuadToRelative(6f, -14f)
            quadToRelative(0f, -8f, -6f, -14f)
            reflectiveQuadToRelative(-14f, -6f)
            horizontalLineToRelative(-60f)
            quadToRelative(-17f, 0f, -28.5f, 11.5f)
            reflectiveQuadTo(300f, 440f)
            verticalLineToRelative(80f)
            quadToRelative(0f, 17f, 11.5f, 28.5f)
            reflectiveQuadTo(340f, 560f)
            close()
            moveTo(480f, 560f)
            quadToRelative(8f, 0f, 14f, -6f)
            reflectiveQuadToRelative(6f, -14f)
            verticalLineToRelative(-120f)
            quadToRelative(0f, -8f, -6f, -14f)
            reflectiveQuadToRelative(-14f, -6f)
            quadToRelative(-8f, 0f, -14f, 6f)
            reflectiveQuadToRelative(-6f, 14f)
            verticalLineToRelative(120f)
            quadToRelative(0f, 8f, 6f, 14f)
            reflectiveQuadToRelative(14f, 6f)
            close()
            moveTo(560f, 560f)
            quadToRelative(8f, 0f, 14f, -6f)
            reflectiveQuadToRelative(6f, -14f)
            verticalLineToRelative(-40f)
            horizontalLineToRelative(40f)
            quadToRelative(8f, 0f, 14f, -6f)
            reflectiveQuadToRelative(6f, -14f)
            quadToRelative(0f, -8f, -6f, -14f)
            reflectiveQuadToRelative(-14f, -6f)
            horizontalLineToRelative(-40f)
            verticalLineToRelative(-20f)
            horizontalLineToRelative(60f)
            quadToRelative(8f, 0f, 14f, -6f)
            reflectiveQuadToRelative(6f, -14f)
            quadToRelative(0f, -8f, -6f, -14f)
            reflectiveQuadToRelative(-14f, -6f)
            horizontalLineToRelative(-80f)
            quadToRelative(-8f, 0f, -14f, 6f)
            reflectiveQuadToRelative(-6f, 14f)
            verticalLineToRelative(120f)
            quadToRelative(0f, 8f, 6f, 14f)
            reflectiveQuadToRelative(14f, 6f)
            close()
        }
    }.build()
}
