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

val Icons.Outlined.ImagesMode: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.ImagesMode",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
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
            moveTo(200f, 760f)
            verticalLineToRelative(-560f)
            verticalLineToRelative(560f)
            close()
            moveTo(280f, 680f)
            horizontalLineToRelative(400f)
            quadToRelative(12f, 0f, 18f, -11f)
            reflectiveQuadToRelative(-2f, -21f)
            lineTo(586f, 501f)
            quadToRelative(-6f, -8f, -16f, -8f)
            reflectiveQuadToRelative(-16f, 8f)
            lineTo(450f, 640f)
            lineToRelative(-74f, -99f)
            quadToRelative(-6f, -8f, -16f, -8f)
            reflectiveQuadToRelative(-16f, 8f)
            lineToRelative(-80f, 107f)
            quadToRelative(-8f, 10f, -2f, 21f)
            reflectiveQuadToRelative(18f, 11f)
            close()
            moveTo(382.5f, 382.5f)
            quadTo(400f, 365f, 400f, 340f)
            reflectiveQuadToRelative(-17.5f, -42.5f)
            quadTo(365f, 280f, 340f, 280f)
            reflectiveQuadToRelative(-42.5f, 17.5f)
            quadTo(280f, 315f, 280f, 340f)
            reflectiveQuadToRelative(17.5f, 42.5f)
            quadTo(315f, 400f, 340f, 400f)
            reflectiveQuadToRelative(42.5f, -17.5f)
            close()
        }
    }.build()
}
